package com.akash.imdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NextActivity extends AppCompatActivity {

    TextView title,plot,rating,ratingvotes,length,cast,sa;

    RatingBar rb;
    String name;
    String rating1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        final SQLiteDatabase myDatabase = openOrCreateDatabase("Movielistseven",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS movies (moviename VARCHAR, rating VARCHAR)");



        rb = findViewById(R.id.rb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.imdblogo1);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        title = findViewById(R.id.title);
        plot = findViewById(R.id.plot);
        rating = findViewById(R.id.rating);
        ratingvotes = findViewById(R.id.ratingVotes);
        length = findViewById(R.id.length);
        cast = findViewById(R.id.cast);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        //index = extras.getInt("size",-1);

        try {
            JSONObject jsonObject = new JSONObject(id);
            name = jsonObject.getString("title");
            String length1 = jsonObject.getString("length");
            String year1 = jsonObject.getString("year");
            rating1 = jsonObject.getString("rating");
            String plot1 = jsonObject.getString("plot");
            String ratingVotes1 = jsonObject.getString("rating_votes");

            JSONArray jsonArray = jsonObject.getJSONArray("cast");
            String cast1 = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                cast1 += "*" + jsonArray.getJSONObject(i).getString("actor") + "   as   " + jsonArray.getJSONObject(i).getString("character") + "\n\n";
            }

            title.setText(name);
            plot.setText("Plot: " + plot1);
            length.setText("Length: " + length1);
            rating.setText("Rating: " + rating1);
            ratingvotes.setText("Rating Votes: " + ratingVotes1);
            cast.setText(cast1);


            rb.setRating(Float.parseFloat(rating1));

            Toast.makeText(getApplicationContext(), rating1, Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

        sa = findViewById(R.id.sa);

        sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    myDatabase.execSQL("INSERT INTO movies (moviename, rating) VALUES ( '"+name+"','"+rating1+"' )");
                    Toast.makeText(getApplicationContext(),"Added to watchlist!",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }


                Cursor c = myDatabase.rawQuery("SELECT * FROM movies", null);
                int nameIndex = c.getColumnIndex("moviename");
                int ratingIndex = c.getColumnIndex("rating");

                c.moveToFirst();

                while (!c.isAfterLast()){
                    Log.i("name",c.getString(nameIndex));
                    Log.i("rating",c.getString(ratingIndex));
                    c.moveToNext();
                }
            }
        });

    }
}
