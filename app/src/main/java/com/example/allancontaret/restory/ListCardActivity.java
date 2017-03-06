package com.example.allancontaret.restory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListCardActivity extends AppCompatActivity {
    private EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_card);

        // view formant la liste de cards
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        // la liste des restos
        final List<Restaurant> restaurants;
        restaurants = new ArrayList<>();

        // le rvadapoter reliant la vu à la liste des restos
        final RVAdapter adapter = new RVAdapter(restaurants);
        rv.setAdapter(adapter);



        /**** Scroll infini avec pages ******/
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                //loadNextDataFromApi(page);
                getRestaurants(restaurants, adapter, (page+1));
                Log.i("page", ""+(page+1));
            }
        };
        // Adds the scroll listener to RecyclerView
        rv.addOnScrollListener(scrollListener);
        getRestaurants(restaurants, adapter, 1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principale, menu);
        return true;
    }

    private void getRestaurants(final List<Restaurant> restaurants, final RVAdapter adapter, int page) {
        // recupération des restos via l'url - Nous utilisons Volley !!
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://api.gregoirejoncour.xyz/restaurants/"+page+"?key=da39a3ee5e6b4b0d3255bfef95601890afd80709";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.length() > 0) {
                    try {
                        JSONArray responseRestaurants = response.getJSONArray("restaurants");
                        for (int i = 0; i < responseRestaurants.length(); i++) {
                            JSONObject restaurantObject = responseRestaurants.getJSONObject(i);
                            JSONObject location = restaurantObject.getJSONObject("location");
                            Log.i("restaurantData", restaurantObject.toString());

                            Restaurant restaurant = new Restaurant();
                            restaurant.name = restaurantObject.getString("name");
                            if (!location.isNull("address")) {
                                restaurant.address = location.getString("address") + " - " + location.getString("city").toUpperCase() + " - " + location.getInt("postal_code");
                            }

                            restaurant.img = R.drawable.drole;
                            restaurant.image = restaurantObject.getString("image");
                            restaurants.add(restaurant);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        } );
        requestQueue.add(jsonObjectRequest);
    }
}