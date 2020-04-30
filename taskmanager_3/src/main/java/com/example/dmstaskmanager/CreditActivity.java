package com.example.dmstaskmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class CreditActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>{

    final int CM_DELETE_ID=1;
    final int CM_PAYMENT_ID=2;
    final int CM_EDIT_ID=3;

    ListView lvCredit;
    DB db;
    SimpleCursorAdapter scAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setVisibility(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                int curCnt = scAdapter.getCount()+1;
                long date = (new java.util.Date()).getTime();

                // добавляем запись
                db.credit_Add(new CREDIT( "Кредит "+curCnt , date, 3_200_000));
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();

                Log.d("DMS_LOG", "ЭТО МОЙ ЛОГ");

                Snackbar.make(view, "Credit added №"+curCnt, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

               // Intent intent = new Intent(this, CreditItemActivity.class);
             //   startActivityForResult(intent, 1);

            }
        });
*/
        // ========= Work with data base ==============
        // открываем подключение к БД
        db = new DB(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.CL_RESULT_CREDIT_NAME,
                DB.CL_RESULT_CREDIT_DATE,
                DB.CL_RESULT_CREDIT_SUMMA ,
                DB.CL_RESULT_PAYMENT_SUMMA,
                DB.CL_RESULT_PAYMENT_SUMMA_CREDIT,
                DB.CL_RESULT_CREDIT_PROCENT,
                DB.CL_RESULT_CREDIT_PERIOD,
                DB.CL_RESULT_RESULT_REST

        };

        int[] to = new int[] { R.id.tvName, R.id.tvDate, R.id.tvSumma , R.id.pbProgress, R.id.pbProgress, R.id.pbProgress, R.id.pbProgress, R.id.pbProgress};

        // создаем адаптер и настраиваем список
        //scAdapter = new SimpleCursorAdapter(this, R.layout.item_task, null, from, to, 0);
        scAdapter = new NewCursorAdapter(this, R.layout.item_credit, null, from, to, 0);
        lvCredit = (ListView) findViewById(R.id.lvCredit);
        lvCredit.setAdapter(scAdapter);


        // добавляем контекстное меню к списку
        registerForContextMenu(lvCredit);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);

        // =======================

        lvCredit.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Log.d("DMS_LOG", "itemClick: position = " + position + ", id = " + id);

                // извлекаем id записи
                Intent intent = new Intent(view.getContext(), GraphicActivity.class);
                intent.putExtra("id", ""+id);
                startActivityForResult(intent, 4);

            }
        });

        lvCredit.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d("DMS_LOG", "itemSelect: position = " + position + ", id = "
                        + id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("DMS_LOG", "itemSelect: nothing");
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 100, R.string.action_credit_delete);
        menu.add(0, CM_PAYMENT_ID, 1, R.string.action_credit_addpayment);
        menu.add(0, CM_EDIT_ID, 2, R.string.action_credit_editcredit);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.credit_Delete(acmi.id);

            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }

        if (item.getItemId() == CM_PAYMENT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();

            // извлекаем id записи
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("id", ""+acmi.id);
            startActivityForResult(intent, 2);

            return true;
        }

        if (item.getItemId() == CM_EDIT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();

            // извлекаем id записи
            Intent intent = new Intent(this, CreditItemActivity.class);
            intent.putExtra("id", ""+acmi.id);
            startActivityForResult(intent, 3);

            return true;
        }

        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            //Cursor cursor = db.credit_GetAll();
            Cursor cursor = db.credit_GetCreditData();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_credit_add) {
            Intent intent = new Intent(this, CreditItemActivity.class);
            startActivityForResult(intent, 1);
        }

        if (id == R.id.action_credit_deleteAll) {
            db.credit_DeleteAll();
            getSupportLoaderManager().getLoader(0).forceLoad();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                //CREDIT cred = data.getExtras("credit");
                //CREDIT cred = data.getExtras("credit");

                String cred_name = data.getStringExtra("name");

                Toast.makeText(this, "Создан новый кредит "+cred_name, Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode==2){
            if (resultCode==RESULT_OK){

                double summa = 0;
                String cred_name = data.getStringExtra("name");
                //summa = data.getDoubleExtra("summa");

                Toast.makeText(this, "Оплата кредита "+cred_name+" на сумму "+summa, Toast.LENGTH_LONG).show();
            }
        }

        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    // Переопределеяем Адаптер списка - класс SimpleAdapter
    public class NewCursorAdapter extends SimpleCursorAdapter {

        private int layout;

        public NewCursorAdapter(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, int _t) {
            super(_context, _layout, _cursor, _from, _to, _t);
            layout = _layout;
        }

        //связывает данные с view на которые указывает курсор
        @Override
        public void bindView(View view, Context _context, Cursor _cursor) {

            Spanned  RUB = Html.fromHtml(" &#x20bd");

            DecimalFormat decFormat_2 = new DecimalFormat("###,###.##");
            DecimalFormat decFormat = new DecimalFormat("###,###");

            String name = _cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_NAME));
            long date=0, date_pay=0 , date_last_pay=0, date_end=0;
            try {

                Log.d("DMS", "CONVERT DATE: "+_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_DATE));

                date = _cursor.getLong(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_DATE));

                date_pay = _cursor.getLong(_cursor.getColumnIndex(DB.CL_RESULT_GRAPHIC_DATE));

                date_last_pay = _cursor.getLong(_cursor.getColumnIndex(DB.CL_RESULT_PAYMENT_DATE_LAST_PAY));

                if (date_last_pay ==0) date_last_pay = (new Date()).getTime();

            }
            catch(Exception e){
                Log.d("DMS", "ERROR CONVERT DATE FROM: "+date);
            }

            Double summa = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_SUMMA)));
            Double summa_payment = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_PAYMENT_SUMMA_CREDIT)));
            int param_period = Integer.parseInt(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_PERIOD)));
            Double param_procent = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_PROCENT)));
            Double summa_rest = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST)));
            Double credit_summa_to_pay = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_SUMMA_PAY)));
            Double summa_to_pay = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_GRAPHIC_SUMMA)));

            Double summa_fin_res = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_FIN_RES)));

            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView tvSumma = (TextView) view.findViewById(R.id.tvSumma);
            TextView tvSummaPay = (TextView) view.findViewById(R.id.tvSummaPay);

            TextView tvSummaFinRes = (TextView) view.findViewById(R.id.tvSummaFinRes);

            // Дата кредитаd
            SimpleDateFormat dateFormatDec = new SimpleDateFormat("dd.MM.yyyy 'г.'");
            String str_date_end = dateFormatDec.format(date_last_pay);

            int days =  (int)((date_last_pay - date) / (24 * 60 * 60 * 1000)); // миллисекунды / (24ч * 60мин * 60сек * 1000мс)
            int years = (int)(days/365);
            int month = (int) (days%365 / 30);
            String str_period = "";
            if (years>0) str_period += String.valueOf(years)+"г. ";
            if (month >0) str_period += String.valueOf(month)+"м. ";
            if (str_period.isEmpty()) str_period+="<1м.";

            // Дата последнего платежа
            String str_date_cr = dateFormatDec.format(date);

            if (summa_rest>1){ // Действующий кредит
                // Заголовок
                tvName.setText(name);

                // Сумма к оплате
                if (summa_to_pay<=0.001 && summa_rest>0) summa_to_pay=credit_summa_to_pay;
                tvSummaPay.setText("к оплате "+decFormat_2.format(summa_to_pay)+RUB);

                // Очередная дата оплаты
                SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy 'г.'");
                String str_date = dateFormat.format(date_pay==0 ? date : date_pay);

                tvDate.setText(str_date);


            }else{ //Считаем что кредит закрыт

                // Типа зачеркнутый заголовок
                String myText = "<strike>"+name+"</strike>";
                Log.d("DNS", "1="+myText);
                tvName.setText(Html.fromHtml(myText), TextView.BufferType.SPANNABLE);

                tvSummaPay.setText("Закрыт");

                tvDate.setText("");
            }

            // Остаток суммы
            TextView tvSummaRest = (TextView) view.findViewById(R.id.tvSummaRest);
            tvSummaRest.setText(decFormat.format(summa_rest)+RUB);

            // Финансовый результат
            tvSummaFinRes.setText(decFormat.format(summa_fin_res)+RUB);

            /// Прогресс бар

            ProgressBar ProgBar =  (ProgressBar) view.findViewById(R.id.pbProgress);

            ProgBar.setMax((int)Math.round(summa));
            ProgBar.setProgress((int)Math.round(summa_payment));

            TextView tvInProgressSumma = (TextView) view.findViewById(R.id.tvInProgressSumma);
            tvInProgressSumma.setText(decFormat.format(summa)+RUB);


            // Процент выполнения

            double proc = (summa==0) ? 0 : summa_payment / summa * 100 ;
            String summa_pay = decFormat.format(summa_payment)+RUB+String.format(" (%.0f%%)", proc);

            TextView tvInProgressSumma_credit = (TextView) view.findViewById(R.id.tvInProgressSumma_credit);
            tvInProgressSumma_credit.setText(summa_pay);

            TextView tvParam = (TextView) view.findViewById(R.id.tvParam);
            tvParam.setText("Сумма "+decFormat.format(summa)+" под "+param_procent+"% на "+param_period+" мес."+"\nДата "+str_date_cr+" - " +str_date_end+" ("+str_period+")");

        }

        //сoздаёт нввую view для хранения данных на которую указывает курсор
        @Override
        public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layout, parent, false);
            return view;
        }

    }

}
