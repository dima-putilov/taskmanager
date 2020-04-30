package com.example.dmstaskmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class CreditItemActivity extends AppCompatActivity implements OnClickListener {

    Button btnOk;
    DB db;

    int credit_id = -1;
    CREDIT cred;

    EditText etDate;
    Calendar dateAndTime=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_item);
        // кнопка Назад
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnOk = (Button) findViewById(R.id.btAddNewCredit);

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        etDate = (EditText) findViewById(R.id.etDate);

        Intent intent = getIntent();
        String str_id = intent.getStringExtra("id");
        if (!str_id.isEmpty()) {
            credit_id = Integer.parseInt(str_id);

            cred = db.GetCredit(credit_id);

            EditText etName = (EditText) findViewById(R.id.etName);
            etName.setText(cred.name);

            EditText etSumma = (EditText) findViewById(R.id.etSumma);
            etSumma.setText(String.valueOf(cred.summa));

            EditText etSummaPay = (EditText) findViewById(R.id.etSummaPay);
            etSummaPay.setText(String.valueOf(cred.summa_pay));


            EditText etProcent = (EditText) findViewById(R.id.etProcent);
            etProcent.setText(String.valueOf(cred.procent));

            EditText etPeriod = (EditText) findViewById(R.id.etPeriod);
            etPeriod.setText(String.valueOf(cred.period));

            if (cred.date>0) {

                dateAndTime.setTimeInMillis(cred.date);
                setInitialDateTime();
            }
        }
        else {
            setInitialDateTime();
        }

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
                dateAndTime.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE
//                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
//                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
     ));
    }


    @Override
    public void onClick(View v) {
        // по id определяем кнопку, вызвавшую этот обработчик
        switch (v.getId()) {
            case R.id.btAddNewCredit:
                // кнопка ОК

                onClickAdd(v);

                break;

            case R.id.btCancel:
                // кнопка Отмена

                finish();

                break;
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    public void onClickCancel(View v) {
        finish();
    }

    public void onClickAdd(View v) {

        double summa, procent, summa_pay;
        summa=procent=summa_pay=0;

        int period = 0;

        long date;

        EditText etName = (EditText) findViewById(R.id.etName);
        EditText etSumma = (EditText) findViewById(R.id.etSumma);
        EditText etSummaPay = (EditText) findViewById(R.id.etSummaPay);
        EditText etProcent = (EditText) findViewById(R.id.etProcent);
        EditText etPeriod = (EditText) findViewById(R.id.etPeriod);


        String str_summa = etSumma.getText().toString();
        String str_summa_pay = etSummaPay.getText().toString();
        String str_procent = etProcent.getText().toString();
        String str_period = etPeriod.getText().toString();

        if (TextUtils.isEmpty(str_summa)) {
            Toast.makeText(this, R.string.credit_item_error_summa, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(str_procent)) {
            Toast.makeText(this, R.string.credit_item_error_procent, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(str_period)) {
            Toast.makeText(this, R.string.credit_item_error_period, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(str_summa_pay)) {
            Toast.makeText(this, R.string.credit_item_error_summa_pay, Toast.LENGTH_LONG).show();
            return;
        }

        String name = etName.getText().toString();
        summa = Float.parseFloat( str_summa );
        summa_pay = Float.parseFloat( str_summa_pay );
        procent = Float.parseFloat( str_procent );
        period = Integer.parseInt( str_period );

        Log.d("DMS", "SAVE Год = "+dateAndTime.get(Calendar.YEAR) + " Месяц "+  dateAndTime.get(Calendar.MONTH)+" День "+dateAndTime.get(Calendar.DAY_OF_MONTH));

        date = dateAndTime.getTimeInMillis();

        CREDIT cred = new CREDIT(name, date, summa);

        cred.SetParam(procent, period, summa_pay);

        if (credit_id>0) {
            cred._id = credit_id;
            db.credit_Update(cred);
        }
        else
            db.credit_Add(cred);

        Intent intent = new Intent();
        intent.putExtra("credit", cred);
        intent.putExtra("name", cred.name);
        setResult(RESULT_OK, intent);

        finish();

    }

}
