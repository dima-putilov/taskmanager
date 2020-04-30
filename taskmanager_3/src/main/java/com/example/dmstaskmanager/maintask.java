package com.example.dmstaskmanager;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;


import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class maintask extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    ListView lvTask;
    DB db;
    SimpleCursorAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ========= Work with data base ==============
        // открываем подключение к БД
        db = new DB(this);
        db.open();

        // автоформирование задач
        db.autoTask();

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.CL_TASK_NAME, DB.CL_TASK_DATE, DB.CL_TASK_SUMMA  };
        int[] to = new int[] { R.id.tvName, R.id.tvDate, R.id.tvSumma };

        // создаем адаптер и настраиваем список
        //scAdapter = new SimpleCursorAdapter(this, R.layout.item_task, null, from, to, 0);
        scAdapter = new NewCursorAdapter(this, R.layout.item_task, null, from, to, 0);
        lvTask = (ListView) findViewById(R.id.lvTask);
        lvTask.setAdapter(scAdapter);


        // добавляем контекстное меню к списку
        registerForContextMenu(lvTask);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);

        // =======================



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int curCnt =  scAdapter.getCount()+1;
                double curSumma = 500.65;
                long date = (new java.util.Date()).getTime();


                // добавляем запись
                TASK pTask = new TASK( 1 , "Task " + curCnt, date, curSumma);
                db.addRec(pTask);
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();


                Snackbar.make(view, "Task added №"+curCnt, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
            Cursor cursor = db.getAllData();
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
        getMenuInflater().inflate(R.menu.menu_maintask, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_deleteAll) {

            db.deleteAll();
            getSupportLoaderManager().getLoader(0).forceLoad();

            return true;
        }

        if (id == R.id.action_flats) {

            Intent intent = new Intent(this, Flat.class);
            startActivity(intent);

        }

        if (id == R.id.action_credit) {

            Intent intent = new Intent(this, CreditActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
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
            String name = _cursor.getString(_cursor.getColumnIndex(DB.CL_TASK_NAME));
            long date = _cursor.getLong(_cursor.getColumnIndex(DB.CL_TASK_DATE));
            Double summa = Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_TASK_SUMMA)));

            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView tvSumma = (TextView) view.findViewById(R.id.tvSumma);

            tvName.setText(name);

            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy 'г.'");
            tvDate.setText(dateFormat.format(date));

            tvSumma.setText(summa.toString());
        }

        //сoздаёт новую view для хранения данных на которую указывает курсор
        @Override
        public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layout, parent, false);
            return view;
        }

    }


}
