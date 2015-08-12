package com.example.android.recipefinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class HomeActivity extends ActionBarActivity {

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_home);

        ListView listView = (ListView) findViewById(R.id.listView);
        final String[] favoriteIds = LoginActivity.join(LoginActivity.favorites).split(",");
        final String[] favoriteNames = LoginActivity.join(LoginActivity.favorites).replace("-", " ").split(",");

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, favoriteNames);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(), RecipeActivity.class);
                i.putExtra("id", favoriteIds[position]);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void search (View view){
        Intent i = new Intent(this, SearchActivity.class);
        EditText name_field = (EditText) findViewById(R.id.search_entry);
        EditText cuisine_field = (EditText) findViewById(R.id.cuisine);
        EditText ing_field = (EditText) findViewById(R.id.ingredients);

        String search = "http://api.pearson.com/kitchen-manager/v1/recipes?";

        if(name_field.getText().toString().isEmpty() && cuisine_field.getText().toString().isEmpty() &&
                ing_field.getText().toString().isEmpty()){
            Toast.makeText(HomeActivity.this, "At least one field must be filled in!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!name_field.getText().toString().isEmpty()){
            search += ("name-contains=" + name_field.getText().toString().replace(" ", "%20"));
        }

        if(!cuisine_field.getText().toString().isEmpty() && Character.isUpperCase(cuisine_field.getText().toString().charAt(0))){
            search += ("&cuisine=" + cuisine_field.getText().toString());
        }
        else if(!cuisine_field.getText().toString().isEmpty() && Character.isLowerCase(cuisine_field.getText().toString().charAt(0))){
            Toast.makeText(HomeActivity.this, "Cuisine must be capitalized!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!ing_field.getText().toString().isEmpty()){
            search += ("&ingredients-any=" + ing_field.getText().toString());
        }

        search += "&limit=50";
        Log.d("stuff", search);
        i.putExtra("search", search);
        startActivity(i);
    }

    public void showAdvanced (View view){
        EditText cuisine = (EditText) findViewById(R.id.cuisine);
        EditText ingredients = (EditText) findViewById(R.id.ingredients);
        Button b = (Button) findViewById(R.id.show);

        if(b.getText().toString().equals("Show Advanced")){
            b.setText("Hide Advanced");
        }
        else{
            b.setText("Show Advanced");
        }

        if(cuisine.getVisibility() == View.VISIBLE){
            cuisine.setVisibility(View.GONE);
        }
        else{
            cuisine.setVisibility(View.VISIBLE);
        }

        if(ingredients.getVisibility() == View.VISIBLE){
            ingredients.setVisibility(View.GONE);
        }
        else{
            ingredients.setVisibility(View.VISIBLE);
        }
    }
}
