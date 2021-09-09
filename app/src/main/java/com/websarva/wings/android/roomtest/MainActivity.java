package com.websarva.wings.android.roomtest;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private int _cocktailId = -1;
    private String _cocktailName = "";
    private String note = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvCocktail = findViewById(R.id.lvCocktail);
        Button btnSave = findViewById(R.id.btnSave);
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());

        lvCocktail.setOnItemClickListener(new ListItemClickListener(db.cocktailDao()));
        btnSave.setOnClickListener(new ButtonClickListener(db.cocktailDao()));
    }

    private class ButtonClickListener implements View.OnClickListener {
        CocktailDao cocktailDao;

        private ButtonClickListener(CocktailDao cocktailDao) {
            this.cocktailDao = cocktailDao;
        }

        @Override
        public void onClick(View v) {
            EditText etNote = findViewById(R.id.etNote);
            note = etNote.getText().toString();

            AsyncUpdate(cocktailDao);
        }
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        CocktailDao cocktailDao;

        private ListItemClickListener(CocktailDao cocktailDao) {
            this.cocktailDao = cocktailDao;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _cocktailId = position;
            _cocktailName = (String) parent.getItemAtPosition(position);
            TextView tvCocktailName = findViewById(R.id.tvCocktailName);
            tvCocktailName.setText(_cocktailName);
            EditText etNote = findViewById(R.id.etNote);
            etNote.setText("");

            Button btnSave = findViewById(R.id.btnSave);
            btnSave.setEnabled(true);

            AsyncSelect(cocktailDao);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @UiThread
    private void AsyncSelect(CocktailDao cocktailDao) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);

        BackgroundTaskSelect backgroundTaskSelect = new BackgroundTaskSelect(handler, cocktailDao);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundTaskSelect);
    };

    private class BackgroundTaskSelect implements Runnable {
        private final Handler _handler;
        private final CocktailDao _cocktailDao;

        public BackgroundTaskSelect(Handler handler, CocktailDao cocktailDao) {
            _handler = handler;
            _cocktailDao = cocktailDao;
        }

        @WorkerThread
        @Override
        public void run() {
            //非同期処理
            //Log.d("a", Integer.toString(_cocktailId));
            Cocktail cocktail = _cocktailDao.findById(_cocktailId);
            note = "";
            if (cocktail.id == _cocktailId) {
                note = cocktail.note;
            }
            //Log.d("a", note);

            SelectPostExector selectPostExector = new SelectPostExector(cocktail.note);
            _handler.post(selectPostExector);
        }
    }

    private class SelectPostExector implements Runnable {
        private String _note;

        public SelectPostExector(String note2) {
            _note = note2;
        }

        @UiThread
        @Override
        public void run() {
            EditText etNote = findViewById(R.id.etNote);
            etNote.setText(_note);
        }
    }

    @UiThread
    private void AsyncUpdate(CocktailDao cocktailDao) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);

        BackgroundTaskUpdate backgroundTaskUpdate = new BackgroundTaskUpdate(handler, cocktailDao);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundTaskUpdate);
    };

    private class BackgroundTaskUpdate implements Runnable {
        private final Handler _handler;
        private final CocktailDao _cocktailDao;

        public BackgroundTaskUpdate(Handler handler, CocktailDao cocktailDao) {
            _handler = handler;
            _cocktailDao = cocktailDao;
        }

        @WorkerThread
        @Override
        public void run() {
            //非同期処理
            _cocktailDao.delete(_cocktailId);
            _cocktailDao.insert(_cocktailId, _cocktailName, note);

            UpdatePostExector updatePostExector = new UpdatePostExector();
            _handler.post(updatePostExector);
        }
    }

    private class UpdatePostExector implements Runnable {
        @UiThread
        @Override
        public void run() {
            EditText etNote = findViewById(R.id.etNote);
            etNote.setText("");

            TextView tvCocktailName = findViewById(R.id.tvCocktailName);
            tvCocktailName.setText(getString(R.string.tv_name));

            Button btnSave = findViewById(R.id.btnSave);
            btnSave.setEnabled(false);
        }
    }
}
