package com.example.android.recipefinder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends ActionBarActivity {
    private ArrayList<String> ids = new ArrayList<>();
    private HashMap<String, Integer> recipeMap = new HashMap<>();
    private View buttonView;
    private View textView;
    private View progressView;
    private Context c;
    ListView recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recipeList = (ListView) findViewById(R.id.recipesList);
        buttonView = findViewById(R.id.saveFavs);
        textView = findViewById(R.id.textView);
        progressView = findViewById(R.id.progress_view);

        Intent i = getIntent();
        String search = i.getStringExtra("search");

        c = this;

        showProgress(true);
        new RequestTask().execute("http://api.pearson.com/kitchen-manager/v1/recipes?name-contains=" + search);
    }

    private class RequestTask extends AsyncTask<String, String, String> {

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
            JsonDecoder decoder = new JsonDecoder(result);
            ids = decoder.getSearchResults();

            int index = 0;
            for(String id : ids) {
                recipeMap.put(id, index);
                index++;
            }


            //important
            ArrayAdapter<String> recipesAdapter =
                    new ArrayAdapter<>(c, android.R.layout.simple_list_item_multiple_choice, ids);
            recipeList.setAdapter(recipesAdapter);
            for(String t : recipeMap.keySet()){
                if(LoginActivity.favorites.contains(t)){
                    recipeList.setItemChecked(recipeMap.get(t), true);
                }
            }

            showProgress(false);
        }
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

            recipeList.setVisibility(show ? View.GONE : View.VISIBLE);
            recipeList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recipeList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            buttonView.setVisibility(show ? View.GONE : View.VISIBLE);
            buttonView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    buttonView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            textView.setVisibility(show ? View.GONE : View.VISIBLE);
            textView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            recipeList.setVisibility(show ? View.GONE : View.VISIBLE);
            buttonView.setVisibility(show ? View.GONE : View.VISIBLE);
            textView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void save(View view) {
        for (String t : recipeMap.keySet()) {
            if (recipeList.isItemChecked(recipeMap.get(t))) {
                addRecipe(t);
            } else if (!recipeList.isItemChecked(recipeMap.get(t))) {
                removeRecipe(t);
            }
        }
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    //add trainee to program
    private void addRecipe(String t) {
        if(!LoginActivity.favorites.contains(t)){

            LoginActivity.favorites.add(t);

            LoginActivity.user.put("Favorites", LoginActivity.join(LoginActivity.favorites));
            LoginActivity.user.saveInBackground();
        }
    }

    //remove trainee from program
    private void removeRecipe(String t) {
        if(LoginActivity.favorites.contains(t)){
            LoginActivity.favorites.remove(t);
            LoginActivity.user.put("Favorites", LoginActivity.join(LoginActivity.favorites));
            LoginActivity.user.saveInBackground();
        }
    }
}
