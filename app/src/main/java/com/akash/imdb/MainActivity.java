package com.akash.imdb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.layout.simple_expandable_list_item_1;

public class MainActivity extends AppCompatActivity {

    EditText e1;
    TextView serch;
    Button b1;
    ListView b2;
    ProgressBar pb;
    String myresponse;
    SQLiteDatabase myDatabase;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.imdblogo1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        serch = findViewById(R.id.serch);
        pb = findViewById(R.id.pb);
        b2 = findViewById(R.id.watchlist);
        TextView sa = findViewById(R.id.sa);

        e1 = findViewById(R.id.et);
        b1 = findViewById(R.id.b1);



        myDatabase = openOrCreateDatabase("Movielistseven",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS movies (moviename VARCHAR, rating VARCHAR)");
        List<String> data = new ArrayList<String>();

        Cursor c = myDatabase.rawQuery("SELECT * FROM movies",null);
        String data1 = "";
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            data1 = c.getString(0)+":"+c.getString(1);
            data.add(data1);
            c.moveToNext();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, simple_expandable_list_item_1,data);
        b2.setAdapter(arrayAdapter);

        b2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (String) b2.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),selectedFromList,Toast.LENGTH_LONG).show();
            }
        });

       b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.setAlpha(1);
                serch.setAlpha(1);

                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(e1.getWindowToken(), 0);

                OkHttpClient client = new OkHttpClient();


                Request request = new Request.Builder()
                        .url("https://imdb-internet-movie-database-unofficial.p.rapidapi.com/film/"+e1.getText().toString())
                        .get()
                        .addHeader("x-rapidapi-host", "imdb-internet-movie-database-unofficial.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "d7f95df88amsh9698c12c76ecc33p1ca7ddjsn6d17893839b2")
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        if(response.isSuccessful()){


                            myresponse = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(getApplicationContext(),NextActivity.class);
                                    intent.putExtra("id",myresponse);
                                    pb.setAlpha(0);
                                    serch.setAlpha(0);
                                    e1.setText("");
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                });
            }
        });



       /*sa.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), Watchlist.class);
               startActivity(intent);
           }
       });*/
    }
}
