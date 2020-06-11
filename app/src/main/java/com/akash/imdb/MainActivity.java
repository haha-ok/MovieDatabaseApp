package com.akash.imdb;

import androidx.annotation.NonNull;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.layout.simple_expandable_list_item_1;
import static com.akash.imdb.R.layout.gold_text;

public class MainActivity extends AppCompatActivity {

    EditText e1;
    TextView wl,er;
    Button b1;
    int listsize=0;
    ListView b2;
    ProgressBar pb;
    String myresponse;
    SQLiteDatabase myDatabase;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> data;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        er = findViewById(R.id.error);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.imdblogo1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        pb = findViewById(R.id.pb);
        b2 = findViewById(R.id.watchlist);
        TextView sa = findViewById(R.id.sa);

        e1 = findViewById(R.id.et);
        b1 = findViewById(R.id.b1);

        wl = findViewById(R.id.wl);


        myDatabase = openOrCreateDatabase("Movielistten",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS movies (moviename VARCHAR)");
        data = new ArrayList<String>();
        Collections.reverse(data);
        listsize = data.size();

        Cursor c = myDatabase.rawQuery("SELECT * FROM movies",null);
        String data1 = "";
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            data1 = c.getString(0);
            data.add(0, data1);
            c.moveToNext();
        }

        arrayAdapter = new ArrayAdapter<String>(this, gold_text,data);
        b2.setAdapter(arrayAdapter);

        if(data.size()!=0){
            wl.setText("Watchlist");
            wl.setAlpha(1);
            b2.setAlpha(1);
            er.setAlpha(0);
        }
        else{
            b2.setAlpha(0);
            wl.setAlpha(0);
            er.setAlpha(1);
        }

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(e1.getText().toString())) {
                        pb.setAlpha(1);

                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(e1.getWindowToken(), 0);

                        OkHttpClient client = new OkHttpClient();


                        Request request = new Request.Builder()
                                .url("https://imdb-internet-movie-database-unofficial.p.rapidapi.com/film/" + e1.getText().toString())
                                .get()
                                .addHeader("x-rapidapi-host", "imdb-internet-movie-database-unofficial.p.rapidapi.com")
                                .addHeader("x-rapidapi-key", "d7f95df88amsh9698c12c76ecc33p1ca7ddjsn6d17893839b2")
                                .build();


                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                //e1.setHint("Error!");
                                pb.setAlpha(0);
                                Toast.makeText(getApplicationContext(), "Try again!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                if (response.isSuccessful()) {


                                    myresponse = response.body().string();
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                                            intent.putExtra("id", myresponse);
                                            Log.i("log: ", myresponse);
                                            pb.setAlpha(0);
                                            e1.setText("");
                                            startActivity(intent);
                                        }
                                    });

                                }
                            }
                        });
                    }else{
                        e1.setError("Enter a title!");
                    }
                }
            });

       b2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
               final int itemToDelete = i;
               final String selectedFromList = (String) b2.getItemAtPosition(i);
               //Toast.makeText(getApplicationContext(),selectedFromList,Toast.LENGTH_LONG).show();
               new AlertDialog.Builder(MainActivity.this)
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .setTitle("Are you sure?")
                       .setMessage("Do you want to remove it from your watchlist?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int i) {


                               myDatabase.execSQL("DELETE FROM movies WHERE moviename = '"+selectedFromList+"'");
                               data.remove(itemToDelete);
                               Toast.makeText(getApplicationContext(),"Removed!",Toast.LENGTH_LONG).show();
                               arrayAdapter.notifyDataSetChanged();

                               if(data.size()!=0){
                                   wl.setText("Watchlist");
                                   er.setAlpha(0);
                                   b2.setAlpha(1);
                                   wl.setAlpha(1);
                               }
                               else{
                                   wl.setAlpha(0);
                                   b2.setAlpha(0);
                                   er.setAlpha(1);
                               }
                           }
                       })
                       .setNegativeButton("No", null)
                       .show();
               return false;
           }
       });

    }

    @Override
    public void onResume(){
        super.onResume();
        data.clear();
        myDatabase = openOrCreateDatabase("Movielistten",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS movies (moviename VARCHAR)");
        data = new ArrayList<String>();
        Collections.reverse(data);

        Cursor c = myDatabase.rawQuery("SELECT * FROM movies",null);
        String data1 = "";
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            data1 = c.getString(0);
            data.add(0, data1);
            c.moveToNext();
        }

        arrayAdapter = new ArrayAdapter<String>(this, simple_expandable_list_item_1,data);
        b2.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        if(data.size()!=0){
            wl.setText("Watchlist");
            wl.setAlpha(1);
            b2.setAlpha(1);
            er.setAlpha(0);
        }
        else{
            wl.setAlpha(0);
            b2.setAlpha(0);
            er.setAlpha(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.clear_watchlist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.clear){
            if(data.size()!=0) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Remove watchlist?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.clear();
                                myDatabase.execSQL("DELETE FROM movies");
                                arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Watchlist removed!", Toast.LENGTH_LONG).show();
                                if (data.size() != 0) {
                                    wl.setText("Watchlist");
                                    er.setAlpha(0);
                                    wl.setAlpha(1);
                                    b2.setAlpha(1);
                                } else {
                                    b2.setAlpha(0);
                                    wl.setAlpha(0);
                                    er.setAlpha(1);
                                }

                            }
                        }).setNegativeButton("No", null).show();
            }else {
                Toast.makeText(getApplicationContext(),"Watchlist is empty!",Toast.LENGTH_LONG).show();
            }
            return true;
        }else{
            return false;
        }
    }
}
