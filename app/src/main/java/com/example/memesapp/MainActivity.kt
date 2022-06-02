package com.example.memesapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
//import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    String memeUrl=null;
    //Drawable resource1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.progressbar);
        imageView=findViewById(R.id.memeImageView);
        loadMeme();
    }
    private void loadMeme()
    {
        progressBar.setVisibility(View.VISIBLE);

        String url ="https://meme-api.herokuapp.com/gimme";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            memeUrl=response.getString("url");
                            Glide.with(MainActivity.this).
                                    load(memeUrl).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onResourceReady(Drawable resource,
                                                               Object model,
                                                               Target<Drawable> target,
                                                               DataSource dataSource,
                                                               boolean isFirstResource) {
                                    //resource1=resource;
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e,
                                                            Object model, Target target,
                                                            boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                            }).into(imageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error loading image",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    public void shareMeme(View view) {
        if(memeUrl==null)
        {
            Toast.makeText(this,"Please wait for image to load ...",Toast.LENGTH_SHORT)
                    .show();
        }
        else
            {
                //Uri uri=Uri.parse("drawable1");
                Intent intent= new Intent(Intent.ACTION_SEND);
                //intent.setType("image/gif,text/plain");
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Hey , check out this meme "+memeUrl);
                //intent.putExtra(Intent.EXTRA_STREAM,uri);
                Intent chooserTarget=Intent.createChooser(intent,"Share this meme using ");
                startActivity(chooserTarget);
            }
    }

    public void nextMeme(View view) {
        loadMeme();
    }



}