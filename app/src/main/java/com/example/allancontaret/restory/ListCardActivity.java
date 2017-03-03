package com.example.allancontaret.restory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListCardActivity extends AppCompatActivity {
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

        // recupération des restos via l'url - Nous utilisons Volley !!
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://api.gregoirejoncour.xyz/restaurant-clone";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        restaurants.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject restaurantObject = response.getJSONObject(i);
                            JSONObject location = restaurantObject.getJSONObject("location");

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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}