package com.akash.imdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Watchlist extends AppCompatActivity {
    ListView lv;
    TextView tv;
    SQLiteDatabase myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        lv = findViewById(R.id.lv);
        tv = findViewById(R.id.tv);

        myDatabase = openOrCreateDatabase("Movielistseven",MODE_PRIVATE,null);

        Cursor c = myDatabase.rawQuery("SELECT * FROM movies",null);
        String data = "";
        while (c.moveToNext())
        {
            data = data+c.getString(0)+"         "+c.getString(1) +"\n";
        }

        tv.setText(data);

    }
}
