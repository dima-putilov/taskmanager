package com.example.culculator;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    EditText edText1;
    EditText edText2;
    TextView edRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        edText1 = (EditText) findViewById(R.id.etNum1);
        edText2 = (EditText) findViewById(R.id.etNum2);
        edRes = (TextView) findViewById(R.id.tvResult);

        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        Button btnSub = (Button) findViewById(R.id.btnSub);
        Button btnMult = (Button) findViewById(R.id.btnMult);
        Button btnDiv = (Button) findViewById(R.id.btnDiv);

        // прописываем обработчик
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMult.setOnClickListener(this);
        btnDiv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        float v1 = Float.parseFloat(edText1.getText().toString());
        float v2 = Float.parseFloat(edText2.getText().toString());
        float res =0;
        String oper = "";

        switch(v.getId()) {
            case R.id.btnAdd:
                res = v1 + v2;
                oper="+";
                break;
            case R.id.btnSub:
                res = v1 - v2;
                oper="-";
                break;

            case R.id.btnMult:
                res = v1 * v2;
                oper="*";
                break;

            case R.id.btnDiv:
                res = v1 / v2;
                oper="/";
                break;

            default:break;
        }

        String txt = v1+" "+oper+" "+ v2 +"="+ res;
        edRes.setText(txt);

        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_quit) {
            finish();
        }else if (id == R.id.action_clear) {
            edText1.setText("");
            edText2.setText("");
        }

        return super.onOptionsItemSelected(item);
    }
}
