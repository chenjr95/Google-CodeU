package com.example.android.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class HomeActivity extends ActionBarActivity {

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_home);

        ListView listView = (ListView) findViewById(R.id.listView);
        String temp = LoginActivity.join(LoginActivity.favorites);
        final String[] favoriteIds = temp.split(",");

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, favoriteIds);
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
//        setContentView(R.layout.activity_home);
//        if (findViewById(R.id.recipe_detail_container) != null) {
//            // The detail container view will be present only in the large-screen layouts
//            // (res/layout-sw600dp). If this view is present, then the activity should be
//            // in two-pane mode.
//            mTwoPane = true;
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
//                        .commit();
//            }
//        } else {
//            mTwoPane = false;
//            getSupportActionBar().setElevation(0f);
//        }
//
//        HomeActivity forecastFragment = ((MainActivityFragment)
//                getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));

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
        TextView t = (TextView) findViewById(R.id.search_entry);
        i.putExtra("search", t.getText().toString());
        startActivity(i);
    }
}
