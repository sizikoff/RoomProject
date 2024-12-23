package com.amicus.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAdd,btnRead,btnClear;
    EditText etName,etEmail;

    final String LOG_TAG="myLogs";
    AppDatabase db;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd  = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnRead  = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);
        btnClear  = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);

        db = AppDatabase.getInstance(this);
        userDao = db.userDao();
    }

    @Override
    public void onClick(View v) {

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();


        switch (v.getId()){
            case R.id.btnAdd:
                Executors.newSingleThreadExecutor().execute(()->{
                    userDao.insert(new User(name,email));
                    Log.d(LOG_TAG,"---- User inserted "+ name+","+email);
                });
                break;
            case R.id.btnRead:
                Executors.newSingleThreadExecutor().execute(()->{
                    List<User> users = userDao.getAllUsers();
                    for (User user:users){
                        Log.d(LOG_TAG,"---- ID ="+ user.id+","+"Name = "+ user.name+", Email = "+ user.email);
                    }
                });
                break;
            case R.id.btnClear:
                Executors.newSingleThreadExecutor().execute(()->{
                    userDao.clearTable();
                    Log.d(LOG_TAG,"Table cleaned");
                });
                break;
        }
    }
}