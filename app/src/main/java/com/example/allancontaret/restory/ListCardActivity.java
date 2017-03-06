package com.example.allancontaret.restory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
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
    public static final String NO_CO_TEXT = "Pas de connexion internet !";
    private EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_card);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        /*String FILENAME = "hello_file";
        String string = "hello world!";*/


        /*try(FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE)){
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*try (FileInputStream fis = openFileInput(FILENAME)) {
            System.out.println("Total file size to read (in bytes) : "+ fis.available());
            int content;
            while ((content = fis.read()) != -1) {
                // convert to char and display it
                System.out.print((char) content);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // view formant la liste de cards
        final RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        // la liste des restos
        final List<Restaurant> restaurants;
        restaurants = new ArrayList<>();

        // le rvadapoter reliant la vu à la liste des restos
        final RVAdapter adapter = new RVAdapter(restaurants);
        rv.setAdapter(adapter);

        /**** Scroll infini avec pages ******/
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                if (isNetworkAvailable()) {
                    // ouverture d'un thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getRestaurants(restaurants, adapter, (page + 1));
                            Log.i("page", "" + (page + 1));
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), NO_CO_TEXT, Toast.LENGTH_LONG).show();
                }
            }
        };
        rv.addOnScrollListener(scrollListener);

        // on verifie si il y a une connexion internet
        if (isNetworkAvailable()) {
            getRestaurants(restaurants, adapter, 1);
        } else {
            Toast.makeText(getApplicationContext(), NO_CO_TEXT, Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.i("dd", "onRefresh called from SwipeRefreshLayout");
                    if (isNetworkAvailable()) {
                        scrollListener.resetState();
                        rv.swapAdapter(adapter,false);
                        getRestaurants(restaurants, adapter, 1);
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), NO_CO_TEXT, Toast.LENGTH_LONG).show();
                    }
                }
            }
        );

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getRestaurants(final List<Restaurant> restaurants, final RVAdapter adapter, int page) {
        final List<Restaurant> resto = new ArrayList<>();
        // recupération des restos via l'url - Nous utilisons Volley pour les requêtes !!
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://api.gregoirejoncour.xyz/restaurants/" + page + "?key=da39a3ee5e6b4b0d3255bfef95601890afd80709";

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
                            restaurant.id = restaurantObject.getInt("id");
                            restaurant.description = restaurantObject.getString("description");
                            if (!location.isNull("address")) {
                                restaurant.address = location.getString("address") + " - " + location.getString("city").toUpperCase() + " - " + location.getInt("postal_code");
                            }

                            restaurant.img = R.drawable.drole;
                            restaurant.image = restaurantObject.getString("image");
                            resto.add(restaurant);
                        }
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            restaurants.clear();
                        }
                        restaurants.addAll(resto);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
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
        });
        requestQueue.add(jsonObjectRequest);
    }



    public static int LONG_PRESS_TIME = 500; // Time in miliseconds

    final Handler _handler = new Handler();
    Runnable _longPressed = new Runnable() {
        public void run() {
            Log.i("info","LongPress");
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                _handler.postDelayed(_longPressed, LONG_PRESS_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
                _handler.removeCallbacks(_longPressed);
                break;
            case MotionEvent.ACTION_UP:
                _handler.removeCallbacks(_longPressed);
                break;
        }
        return true;
    }
}