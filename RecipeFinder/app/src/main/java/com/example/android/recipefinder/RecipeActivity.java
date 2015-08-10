package com.example.android.recipefinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class RecipeActivity extends ActionBarActivity {
    private String id = null;
    private View recipeView;
    private View progressView;
    private TextView nameView;
    private TextView cuisineView;
    private TextView methodView;
    private TextView servesView;
    private TextView ingsView;
    private TextView directionsView;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent i = getIntent();
        id = i.getStringExtra("id");

        nameView = (TextView) findViewById(R.id.name_body);
        cuisineView = (TextView) findViewById(R.id.cuisine_body);
        methodView = (TextView) findViewById(R.id.method_body);
        servesView = (TextView) findViewById(R.id.serves_body);
        ingsView = (TextView) findViewById(R.id.ingredients_body);
        directionsView = (TextView) findViewById(R.id.directions_body);
        imgView = (ImageView) findViewById(R.id.image_body);
        
        Button saveButton = (Button) findViewById(R.id.save_button);
        final CheckBox saveCheckbox = (CheckBox) findViewById(R.id.save_check);

        if(LoginActivity.favorites.contains(id)){
            saveCheckbox.setChecked(true);
        }

        final SaveCallback mSaveCallback = new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(RecipeActivity.this, "Recipe saved!", Toast.LENGTH_SHORT).show();
            }
        };

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveCheckbox.isChecked()){
                    if(LoginActivity.favorites.isEmpty()){
                        LoginActivity.favorites = id;
                    }
                    else{
                        LoginActivity.favorites = LoginActivity.favorites + "," + id;
                    }
                }
                else{
                    if(LoginActivity.favorites.contains("," + id)){
                        LoginActivity.favorites = LoginActivity.favorites.replace("," + id, "");
                    }
                    else if(LoginActivity.favorites.contains(id + ",")){
                        LoginActivity.favorites = LoginActivity.favorites.replace(id + "," , "");
                    }
                    else{
                        LoginActivity.favorites = LoginActivity.favorites.replace(id , "");
                    }
                }
                LoginActivity.user.put("Favorites", LoginActivity.favorites);
                LoginActivity.user.saveInBackground(mSaveCallback);
            }
        });

        recipeView = findViewById(R.id.recipe_view);
        progressView = findViewById(R.id.progress_view);
        showProgress(true);
        new RequestTask().execute("https://api.pearson.com/kitchen-manager/v1/recipes/" + id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            recipeView.setVisibility(show ? View.GONE : View.VISIBLE);
            recipeView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recipeView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            recipeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class RequestTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            JsonDecoder decode = new JsonDecoder(result);
            nameView.setText(decode.getName());
            cuisineView.setText(decode.getCuisine());
            methodView.setText(decode.getMethod());
            servesView.setText(decode.getServes());
            ingsView.setText(decode.getIngredients());
            directionsView.setText(decode.getDirections());
            new DownloadImageTask(imgView)
                    .execute(decode.getImageURL());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            showProgress(false);
        }
    }
}
