package com.example.allancontaret.restory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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
        final RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        // la liste des restos
        final List<Restaurant> restaurants;
        restaurants = new ArrayList<>();

        // le rvadapoter reliant la vu à la liste des restos
        final RVAdapter adapter = new RVAdapter(restaurants);
        rv.setAdapter(adapter);

        // on verifie si il y a une connexion internet
        if (isNetworkAvailable()) {
            /**** Scroll infini avec pages ******/
            scrollListener = new EndlessRecyclerViewScrollListener(llm) {
                @Override
                public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                    // ouverture d'un thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getRestaurants(restaurants, adapter, (page+1));
                            Log.i("page", ""+(page+1));
                        }
                    }).start();
                }
            };
            rv.addOnScrollListener(scrollListener);
            getRestaurants(restaurants, adapter, 1);
        } else {
            Toast.makeText(getApplicationContext(), "Pas de connexion internet !", Toast.LENGTH_LONG).show();
            /*Intent intent = new Intent(main.this, no_connection.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getRestaurants(final List<Restaurant> restaurants, final RVAdapter adapter, int page) {

        // recupération des restos via l'url - Nous utilisons Volley pour les requêtes !!
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

                        //page traitement
                        /*SONObject responsePage = response.getJSONObject("page");
                        if (response.getJSONObject("page").optInt("next", 0)==0) {
                            Toast.makeText(getApplicationContext(), "Dernière page !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Page n°"+responsePage.getInt("current")+"/"+responsePage.getInt("total"), Toast.LENGTH_SHORT);
                            toast.setGravity(0, 0, 0);
                            toast.show();
                        }*/

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