package com.akash.imdb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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
    String sqlstring;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        progressBar = findViewById(R.id.progressbar);

        final SQLiteDatabase myDatabase = openOrCreateDatabase("Movielistten", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS movies (moviename VARCHAR)");

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

        String url = null;
        try {
            JSONObject jsonObject = new JSONObject(id);
            name = jsonObject.getString("title");
            String length1 = jsonObject.getString("length");
            String year1 = jsonObject.getString("year");
            rating1 = jsonObject.getString("rating");
            String plot1 = jsonObject.getString("plot");
            url = jsonObject.getString("poster");
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

        ImageView imageview = findViewById(R.id.imageview);

        progressBar.setVisibility(View.VISIBLE);
        //Glide.with(NextActivity.this).load(url).into(imageview);
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageview);

        sqlstring = name + ": " + rating1;
        sa = findViewById(R.id.sa);

        sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = myDatabase.rawQuery("SELECT * FROM movies", null);
                int nameIndex = c.getColumnIndex("moviename");
                //int ratingIndex = c.getColumnIndex("rating");

                c.moveToFirst();

                boolean flag = true;
                while (!c.isAfterLast()) {
                    if (c.getString(nameIndex).equals(sqlstring)) {
                        flag = false;
                        Toast.makeText(getApplicationContext(), "Already exists in your watchlist!", Toast.LENGTH_LONG).show();
                    }
                    c.moveToNext();
                }

                if (flag) {
                    myDatabase.execSQL("INSERT INTO movies (moviename) VALUES ( '" + sqlstring + "' )");
                    Toast.makeText(getApplicationContext(), "Added to watchlist!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
