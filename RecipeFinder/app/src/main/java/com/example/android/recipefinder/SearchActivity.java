package com.example.android.recipefinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends Activity {
    private ArrayList<String> ids = new ArrayList<>();
    private HashMap<String, Integer> recipeMap = new HashMap<>();
    private View buttonView;
    private View textView;
    ListView recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recipeList = (ListView) findViewById(R.id.recipesList);
        buttonView = findViewById(R.id.saveFavs);
        textView = findViewById(R.id.textView);

        Intent i = getIntent();
        String id = i.getStringExtra("id");

        //move all below to onpostexecute

        int index = 0;
        for(String result : ids) {
            recipeMap.put(result, index);
            index++;
        }

        ArrayAdapter<String> recipesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                        ids);
        recipeList.setAdapter(recipesAdapter);
        for (String result : ids) {
            if (LoginActivity.favorites.contains(result)) {
                recipeList.setItemChecked(recipeMap.get(result), true);
            }
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
        LoginActivity.favorites = LoginActivity.favorites + "," + t;
        LoginActivity.user.put("Favorites", LoginActivity.favorites);
        LoginActivity.user.saveInBackground();
    }

    //remove trainee from program
    private void removeRecipe(String t) {
        if(LoginActivity.favorites.contains("," + t)){
            LoginActivity.favorites = LoginActivity.favorites.replace(","+t , "");
        }
        else{
            LoginActivity.favorites = LoginActivity.favorites.replace(t , "");
        }
        LoginActivity.user.put("Favorites", LoginActivity.favorites);
        LoginActivity.user.saveInBackground();
    }
}
