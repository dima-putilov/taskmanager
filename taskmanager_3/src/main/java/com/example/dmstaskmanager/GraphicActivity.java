package com.example.dmstaskmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.dmstaskmanager.DB.ATTR_DATE;

public class GraphicActivity extends AppCompatActivity {

    final int CM_DELETE_ID=1;
    final int CM_PAYMENT_ID=2;
    final int CM_EDIT_ID=3;

    int credit_id = -1; // ИД кредита
    CREDIT cred; // Текущий кредит

    Button btnOk;
    DB db;

    // Дата  - определяет дату с которой считать плановые платежи
    // следующие даты будут = +1 месяц и т.д.
    EditText etDate;
    Calendar dateAndTime = Calendar.getInstance();

    // Основной список - график платежей
    // включает как уже совешенные платежи, так и плановые платежи
    ArrayList<Map<String, Object>> listData;
    ListView lvGraphic; // интерфейс списка
    SimpleAdapter sAdapter;  // адаптер списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);

        // === ToolBar ===
        // Заголовок
        getSupportActionBar().setTitle(R.string.toolbar_graphic);
        // Кнопка Назад
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // === Инициализация ===
        // === Инициализация текущего кредита, по которому открывается график платежей ===
        // Он передается в intent
        Intent intent = getIntent();
        String str_id = intent.getStringExtra("id");
        if (str_id.isEmpty()) {
            return; // нет ссылки на кредит => выход
        }

        // ИД кредита
        credit_id = Integer.parseInt(str_id);

        // открываем подключение к БД
        db = new DB(this);
        db.open();
        // получаем объект Кредит - текущий кредит
        cred = db.GetCredit(credit_id);
        // Устанавливаем заголовок (Имя и параметры кредита)
        setCreditHeader();
        // Дополнительные параметры
        TextView etSummaPay = (TextView) findViewById(R.id.etSummaPay);
        etSummaPay.setText(DB.round2Str(cred.summa_pay, 2));


        //
        btnOk = (Button) findViewById(R.id.btOk);
        etDate = (EditText) findViewById(R.id.etDate);

        // инициализируем основной список
        listData = new ArrayList<Map<String, Object>>();

        // Первоначальное заполнение списка Факт+План платежей
        fillGraphic(true);

        // Если график заполнен, то определяем очередную дату платежа
        // = +1 месяц от последней даты платежа
        if (listData.size() > 0) {

            long t_date = 0, cur_date = 0;
            int done = 0;
            for (Map<String, Object> cv : listData) {
                done = ((Integer) cv.get(DB.ATTR_DONE)).intValue();
                if (done == 1) {
                    cur_date = ((Long) cv.get(DB.ATTR_DATE)).longValue();
                    if (t_date < cur_date) t_date = cur_date;
                }
            }

            dateAndTime.setTimeInMillis(t_date); // устанавливаем дату последнего платежа
            dateAndTime.add(Calendar.MONTH, 1); // следующий месяц

            setInitialDateTime();
        } else {
            // пустой график - дата платежа = дата кредита (условно)
            if (cred.date > 0) {
                dateAndTime.setTimeInMillis(cred.date);
                setInitialDateTime();
            }
        }

        // ===== Инициализируем адаптер списка =====
        // массив имен атрибутов, из которых будут читаться данные
        String[] from = {DB.ATTR_DONE, DB.ATTR_DATE, DB.ATTR_REST, DB.ATTR_SUMMA, DB.ATTR_SUMMA_CREDIT, DB.ATTR_SUMMA_PROCENT};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = {R.id.llGraphic, R.id.tvGrDate, R.id.tvRest, R.id.tvSumma, R.id.tvSumma_credit, R.id.tvSumma_procent};

        // создаем адаптер
        sAdapter = new SimpleAdapter(this, listData, R.layout.item_graphic, from, to);

        // Указываем адаптеру свой биндер
        sAdapter.setViewBinder(new MyViewBinder());

        // определяем список и присваиваем ему адаптер
        lvGraphic = (ListView) findViewById(R.id.lvGraphic);

        // Добавляем Шапку списка (Header) (Строго перед присвоением адаптера списку)
        View header = getLayoutInflater().inflate(R.layout.item_graphic, null);
        ((TextView) header.findViewById(R.id.tvGrDate)).setText(R.string.graphic_header_date);
        ((TextView) header.findViewById(R.id.tvRest)).setText(R.string.graphic_header_rest);
        ((TextView) header.findViewById(R.id.tvSumma)).setText(R.string.graphic_header_summa);
        ((TextView) header.findViewById(R.id.tvSumma_credit)).setText(R.string.graphic_header_credit);
        ((TextView) header.findViewById(R.id.tvSumma_procent)).setText(R.string.graphic_header_procent);
        lvGraphic.addHeaderView(header);

        lvGraphic.setAdapter(sAdapter);
        // ===== //

        // добавляем контекстное меню к списку
        registerForContextMenu(lvGraphic);


        //  ==== Обработчики событий ====


        // Нажатие на пункт списка
        lvGraphic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // По нажатию открывается либо текущий фактический платеж (если у него задан _id > 0)
                // либо новый платеж (_id<0) на основании параметров планового платежа

                Log.d("DMS_LOG", "itemClick: position = " + position + ", id = " + id);

                // получаем ИД платежа из элемента списка, по его позиции
                int index = position-1;
                String str_payment_id = listData.get(index).get("_id").toString();
                int done = Integer.parseInt(listData.get(index).get("done").toString());

                // извлекаем id записи
                Intent intent;
                if (done==1)
                    // Если это факт - открываем для редактирования карточку платежа
                    intent = new Intent(view.getContext(), PaymentActivity.class);
                else
                    // Если это план - редактируем строку графика платежей
                    intent = new Intent(view.getContext(), GraphicItemActivity.class);

                // Параметры платежа
                intent.putExtra("id", "" + credit_id);
                intent.putExtra("payment_id", "" + str_payment_id);
                intent.putExtra("date", "" + listData.get(index).get("date").toString());
                intent.putExtra("summa", "" + listData.get(index).get("summa").toString());
                intent.putExtra("summa_credit", "" + listData.get(index).get("summa_credit").toString());
                intent.putExtra("summa_procent", "" + listData.get(index).get("summa_procent").toString());

                startActivityForResult(intent, 5);

            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, CM_PAYMENT_ID, 1, R.string.action_credit_addpayment);

        menu.add(0, CM_DELETE_ID, 100, R.string.action_credit_delete);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД

            // получаем ИД платежа из элемента списка, по его позиции
            int index = acmi.position-1;
            String str_payment_id = listData.get(index).get("_id").toString();
            int done = Integer.parseInt(listData.get(index).get("done").toString());

            // извлекаем id записи
            Intent intent;
            if (done==1)
                // Если это факт - удаляем платеж
                db.payment_Delete(str_payment_id);
            else
                // Если это план - удаляем строку графика
                db.graphic_Delete(str_payment_id);

            return true;
        }

        if (item.getItemId() == CM_PAYMENT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            Log.d("DMS_LOG", "itemClick: position = " + acmi.position + ", id = " + acmi.id);

            // получаем ИД платежа из элемента списка, по его позиции
            int index = acmi.position-1;
            String str_payment_id = listData.get(index).get("_id").toString();
            int done = Integer.parseInt(listData.get(index).get("done").toString());

            // извлекаем id записи
            Intent intent;
            if (done!=0) return true;

            // Если это факт - открываем для редактирования карточку платежа
            intent = new Intent(this, PaymentActivity.class);
            // Параметры платежа
            intent.putExtra("id", "" + credit_id);
            intent.putExtra("payment_id", "" + str_payment_id);
            intent.putExtra("date", "" + listData.get(index).get("date").toString());
            intent.putExtra("summa", "" + listData.get(index).get("summa").toString());
            intent.putExtra("summa_credit", "" + listData.get(index).get("summa_credit").toString());
            intent.putExtra("summa_procent", "" + listData.get(index).get("summa_procent").toString());

            startActivityForResult(intent, 5);

            return true;
        }

        return super.onContextItemSelected(item);
    }


    // Внимание! Объект cred (текщий кредит) должен быть инициализирован
    public void setCreditHeader() {
        TextView tvName     = (TextView) findViewById(R.id.tvName);
        TextView tvParam    = (TextView) findViewById(R.id.tvParam);

        String name = cred.name + " от " + DateUtils.formatDateTime(this,
                cred.date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

        String param = "Сумма: " + String.format("%.0f", cred.summa) + ", Процент: " + String.format("%.2f%%", cred.procent) + ", Срок: " + String.valueOf(cred.period);

        tvName.setText(name);
        tvParam.setText(param);
    }

    // Биндер списка - переопределение отображения полей
    class MyViewBinder implements SimpleAdapter.ViewBinder {

        int color_done = getResources().getColor(R.color.color_done);
        int color_other = getResources().getColor(R.color.color_other);
        int color_current_pay = getResources().getColor(R.color.color_current_pay);

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int i = 0;
            double d =0;
            switch (view.getId()) {
                // LinearLayout
                case R.id.llGraphic:
                    i = ((Integer) data).intValue();
                    if (i == 1) view.setBackgroundColor(color_done);
                    else view.setBackgroundColor(color_other);
                    return true;
                // Date
                case R.id.tvGrDate:

                    String str = data.toString();

                   // Log.d("DMS", "OBJECT="+str);

                    long ldata = Long.valueOf(str);

                    String str_date = DateUtils.formatDateTime(view.getContext(),
                            ldata,DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);

                    ((TextView) view).setText(str_date);
                    return true;

                // Rest
                case R.id.tvRest:
                    d = ((Double) data).doubleValue();
                    ((TextView) view).setText(String.format("%.0f", d));
                    return true;

                // Summa
                case R.id.tvSumma:
                    d = ((Double) data).doubleValue();
                    ((TextView) view).setText(String.format("%.0f", d));
                     return true;

                // Summa credit
                case R.id.tvSumma_credit:
                    d = ((Double) data).doubleValue();
                    ((TextView) view).setText(String.format("%.0f", d));
                    return true;

                // Summa procent
                case R.id.tvSumma_procent:
                    d = ((Double) data).doubleValue();
                    ((TextView) view).setText(String.format("%.0f", d));
                    return true;

            }
            return false;
        }
    }

    // ====== Главное Меню ======
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graphic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_payment_add) {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("id", ""+credit_id);
            startActivityForResult(intent, 1);
        }

        if (id == R.id.action_graphic_add) {
            Intent intent = new Intent(this, GraphicItemActivity.class);
            intent.putExtra("id", ""+credit_id);
            startActivityForResult(intent, 1);
        }


        if (id == R.id.action_payment_deleteAll) {
            db.payment_DeleteAll(credit_id);
        }

        return super.onOptionsItemSelected(item);
    }

    // Заполняет график из БД
    // ФАКТ - табл. PAYMENT, ПЛАН - табл. GRAPHIC (все даты после последнего платежа)
    // withPlan - если ИСТИНА, то получаем полный график из базы ФАКТ + ПЛАН
    // иначе, только ФАКТ
    public void fillGraphic(boolean withPlan) {
        // Создаем адаптер списка
        // упаковываем данные в понятную для адаптера структуру

        // очищаем список
        listData.clear();

        Cursor c;
        c = db.GetGraphic(credit_id, withPlan); // ФАКТ [+ ПЛАН (если withPlan=true)]

        if (c != null) {

            Map<String, Object> m;

            double summa_credit_all = cred.summa;

            if (c.moveToFirst()) {
                do {

                    double summa_credit = c.getDouble(c.getColumnIndex(DB.ATTR_SUMMA_CREDIT));

                    m = new HashMap<String, Object>();
                    m.put(DB.ATTR_DONE, c.getInt(c.getColumnIndex(DB.ATTR_DONE)));
                    m.put(DB.ATTR_DATE, c.getLong(c.getColumnIndex(DB.ATTR_DATE)));
                    m.put(DB.ATTR_REST, summa_credit_all);
                    m.put(DB.ATTR_SUMMA, c.getDouble(c.getColumnIndex(DB.ATTR_SUMMA)));
                    m.put(DB.ATTR_SUMMA_CREDIT, summa_credit);
                    m.put(DB.ATTR_SUMMA_PROCENT, c.getDouble(c.getColumnIndex(DB.ATTR_SUMMA_PROCENT)));
                    m.put(DB.CL_ID, c.getInt(c.getColumnIndex(DB.CL_ID)));

                    listData.add(m);

                    summa_credit_all -= summa_credit;

                } while (c.moveToNext());
            }
            c.close();
        }

      //  Log.d("DMS","Cnt = "+listData.size());

    }



    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    // установка начальных даты и времени
    private void setInitialDateTime() {
        etDate.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
//                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }


    // отображаем диалоговое окно для выбора даты
    public void execGraphic(View v) {

        fillGraphic(false);

        CalculateCredit();

        Log.d("DMS", "Обновляем адаптер");

        sAdapter.notifyDataSetChanged();

    }

    public void CalculateCredit(){

        EditText etSummaPay = (EditText) findViewById(R.id.etSummaPay);
        String str_pay = etSummaPay.getText().toString();

        if (str_pay.isEmpty()){
            Toast.makeText(this, R.string.graphic_error_summa_pay, Toast.LENGTH_LONG).show();
            return;
        }

        Calendar tmp_date = Calendar.getInstance();
        tmp_date.setTimeInMillis(dateAndTime.getTimeInMillis());

        double summa_credit_all = cred.summa;
        double e_pay = Double.parseDouble(str_pay);

        if (e_pay<=0.001){
            Toast.makeText(this, R.string.graphic_error_summa_pay, Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> m;

        for (Map<String, Object> cv : listData){
            summa_credit_all -= ((Double) cv.get(DB.ATTR_SUMMA_CREDIT)).doubleValue();
            if(tmp_date.getTimeInMillis() <= ((Long) cv.get(DB.ATTR_DATE)).longValue())
                tmp_date.add(Calendar.MONTH, 1);
        }

        while (summa_credit_all>0){

            double proc_sum = summa_credit_all * cred.procent / 12 / 100;
            proc_sum = new BigDecimal(proc_sum).setScale(2, RoundingMode.UP).doubleValue();
            double summa_credit = Math.max(e_pay - proc_sum, 0);

            if (summa_credit_all-summa_credit<=1000) summa_credit = summa_credit_all;

            Log.d("DMS", "summa_credit_all="+String.valueOf(summa_credit_all)+
                    ", proc_sum="+String.valueOf(proc_sum)+
                    ", summa_credit="+String.valueOf(summa_credit)
            );

            m = new HashMap<String, Object>();
            m.put(DB.ATTR_DONE, 0);
            m.put(DB.ATTR_DATE, tmp_date.getTimeInMillis());
            m.put(DB.ATTR_REST, summa_credit_all);
            m.put(DB.ATTR_SUMMA, e_pay);
            m.put(DB.ATTR_SUMMA_CREDIT, summa_credit);
            m.put(DB.ATTR_SUMMA_PROCENT, proc_sum);
            m.put(DB.CL_ID, -1);

            listData.add(m);

            summa_credit_all -= summa_credit;

            tmp_date.add(Calendar.MONTH, 1);

        }

    }


    // Обработчик кнопки Отмена / Закрыть / Назад
    public void onClickCancel(View v) {
        finish();
    }

    // Обработчик кнопки Записать / ОК - сохранение графика в БД
    public void onClickAdd(View v) {

        double summa, procent, summa_pay;
        summa=procent=summa_pay=0;

        int period = 0;

        long date;

        db.graphic_DeleteAll(credit_id);

        for(Map<String, Object> cv : listData){
            PAYMENT pay = new PAYMENT(credit_id);
            pay.date            = ((Long) cv.get("date")).longValue();
            pay.summa           = ((Double) cv.get("summa")).doubleValue();
            pay.summa_credit    = ((Double) cv.get("summa_credit")).doubleValue();
            pay.summa_procent   = ((Double) cv.get("summa_procent")).doubleValue();

            db.graphic_Add(pay);
        }

        Log.d("DMS", "SAVE Год = "+dateAndTime.get(Calendar.YEAR) + " Месяц "+  dateAndTime.get(Calendar.MONTH)+" День "+dateAndTime.get(Calendar.DAY_OF_MONTH));

        Intent intent = new Intent();
        intent.putExtra("credit", cred);
        intent.putExtra("name", cred.name);
        setResult(RESULT_OK, intent);

        finish();

    }

}
