package com.example.dmstaskmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    DB db;
    int credit_id;
    int payment_id = -1 ;
    CREDIT cred;
    PAYMENT payment;

    EditText etDate, etSumma, etSummaCredit, etSummaProcent, etSumma_addon, etSumma_plus, etSumma_minus;
    Calendar dateAndTime=Calendar.getInstance();
    TextView tvNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        String str_id = intent.getStringExtra("id");

        if (str_id.isEmpty()) return; // нет ссылки на кредит => выход

        // кнопка Назад
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_payment);

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        credit_id = Integer.parseInt(str_id);
        cred = db.GetCredit(credit_id);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvParam = (TextView) findViewById(R.id.tvParam);

        String name = cred.name + " от "+ DateUtils.formatDateTime(this,
                cred.date,DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String param = "Сумма: "+String.format("%.0f",cred.summa)+", Процент: "+String.format("%.2f%%",cred.procent)+", Срок: "+String.valueOf(cred.period);

        tvName.setText(name);
        tvParam.setText(param);

        etDate = (EditText) findViewById(R.id.etDate);

        setInitialDateTime();

        etSumma = (EditText) findViewById(R.id.etSumma);
        etSummaCredit = (EditText) findViewById(R.id.etSumma_credit);
        etSummaProcent = (EditText) findViewById(R.id.etSumma_procent);
        etSumma_addon = (EditText) findViewById(R.id.etSumma_addon);
        etSumma_plus = (EditText) findViewById(R.id.etSumma_plus);
        etSumma_minus = (EditText) findViewById(R.id.etSumma_minus);

        TextView tvNew = (TextView) findViewById(R.id.tvNew);
        tvNew.setText("* Новый ");

        if (intent.hasExtra("payment_id")) {
            String str_payment_id = intent.getStringExtra("payment_id");
            if (!str_payment_id.isEmpty()) {
                // редактирование существующей записи
                payment_id = Integer.parseInt(str_payment_id);

                if (payment_id > 0) {

                    tvNew.setText("");

                    payment = db.GetPayment(payment_id);

                    Log.d("DMS", "SUMMA =" + String.valueOf(payment.summa));

                    etSumma.setText(DB.round2Str(payment.summa, 2));
                    etSummaCredit.setText(DB.round2Str(payment.summa_credit, 2));
                    etSummaProcent.setText(DB.round2Str(payment.summa_procent, 2));
                    etSumma_addon.setText(DB.round2Str(payment.summa_addon, 2));
                    etSumma_plus.setText(DB.round2Str(payment.summa_plus, 2));
                    etSumma_minus.setText(DB.round2Str(payment.summa_minus, 2));

                    if (payment.date > 0) {
                        dateAndTime.setTimeInMillis(payment.date);
                        setInitialDateTime();
                    }
                }
            }
        };

        if (payment_id<=0){
            HashMap<String, EditText> m = new HashMap<String, EditText>();
            m.put("summa", etSumma);
            m.put("summa_credit", etSummaCredit);
            m.put("summa_procent", etSummaProcent);

            String str="";
            for (HashMap.Entry<String, EditText> entry : m.entrySet()){
                if (intent.hasExtra(entry.getKey())) {
                    str = DB.round2Str(intent.getStringExtra(entry.getKey()), 2);
                    entry.getValue().setText(str);
                }
            }

            if (intent.hasExtra("date")) {
                long t_date = Long.valueOf(intent.getStringExtra("date"));
                dateAndTime.setTimeInMillis(t_date);
                setInitialDateTime();
            }
        }

        etSummaCredit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // Запрет на ввод более 2-х дробных знаков
                String str = s.toString();
                int p = str.indexOf(".");
                if (p != -1) {
                    String tmpStr = str.substring(p);
                    if (tmpStr.length() == 4) {
                        s.delete(s.length()-1, s.length());
                    }
                }


                BigDecimal new_proc = DB.round2Dec(etSumma.getText().toString(), 2).subtract(
                        DB.round2Dec(etSummaCredit.getText().toString(), 2));

                etSummaProcent.setText(String.valueOf(new_proc));


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

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
                    DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR
//                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
//                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
        ));
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    public void onClickCancel(View v) {
        finish();
    }

    public void onClickAddonSumm(View v) {
        etSumma_addon.setText(etSummaCredit.getText());
    }

    public void onClickAdd(View v) {

        double summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus;
        summa= summa_credit= summa_procent= summa_addon= summa_plus= summa_minus=0;

        int period = 0;

        EditText etSumma = (EditText) findViewById(R.id.etSumma);
        EditText etSumma_credit = (EditText) findViewById(R.id.etSumma_credit);
        EditText etSumma_procent = (EditText) findViewById(R.id.etSumma_procent);
        EditText etSumma_addon = (EditText) findViewById(R.id.etSumma_addon);
        EditText etSumma_plus = (EditText) findViewById(R.id.etSumma_plus);
        EditText etSumma_minus = (EditText) findViewById(R.id.etSumma_minus);


        String str_summa = etSumma.getText().toString();

        String str_summa_credit = etSumma_credit.getText().toString();
        String str_summa_procent = etSumma_procent.getText().toString();
        String str_summa_addon = etSumma_addon.getText().toString();
        String str_summa_plus = etSumma_plus.getText().toString();
        String str_summa_minus = etSumma_minus.getText().toString();



        if (TextUtils.isEmpty(str_summa)) {
            Toast.makeText(this, R.string.credit_item_error_summa, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(str_summa_credit)) {
            str_summa_credit="0";
        }

        if (TextUtils.isEmpty(str_summa_procent)) {
            str_summa_procent="0";
        }

        if (TextUtils.isEmpty(str_summa_plus)) {
            str_summa_plus="0";
        }

        if (TextUtils.isEmpty(str_summa_minus)) {
            str_summa_minus="0";
        }

        if (TextUtils.isEmpty(str_summa_addon)) {
            str_summa_addon="0";
        }

        summa = Double.parseDouble( str_summa );
        summa_credit = Double.parseDouble( str_summa_credit );
        summa_procent = Double.parseDouble( str_summa_procent );

        summa_plus = Double.parseDouble( str_summa_plus );
        summa_minus = Double.parseDouble( str_summa_minus );
        summa_addon = Double.parseDouble( str_summa_addon);

        long date = dateAndTime.getTimeInMillis();

        PAYMENT pay = new PAYMENT(credit_id, date, summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus);

        if (payment_id>0) {
            pay._id = payment_id;
            db.payment_Update(pay);
        }
        else
            db.payment_Add(pay);

        Intent intent = new Intent();
        intent.putExtra("name", credit_id);
        intent.putExtra("sum", summa);
        setResult(RESULT_OK, intent);

        finish();

    }


}
