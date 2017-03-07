package com.example.allancontaret.restory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class RestaurantActivity extends AppCompatActivity {
    Restaurant restaurant;
    Button fav;
    Button map;
    SharedPreferences settings;
    public static final String PREFS_RESTO = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        // shared datas
        settings = getSharedPreferences(PREFS_RESTO, 0);
        Intent intent = getIntent();
        if (intent.hasExtra("MyClass"))
        {
            restaurant  = (Restaurant) intent.getSerializableExtra("MyClass");
            Log.i("intent", String.valueOf(restaurant.name));
            setTitle(restaurant.name);
            TextView textRestoName = (TextView) findViewById(R.id.textRestoName);
            textRestoName.setText(restaurant.name);

            TextView textRestoDescription = (TextView) findViewById(R.id.textRestoDescription);
            textRestoDescription.setText(restaurant.description);

            Glide.with(getApplicationContext())
                    .load("http://api.gregoirejoncour.xyz/images/"+restaurant.image)
                    .fitCenter()
                    .into((ImageView) findViewById(R.id.imageViewRestaurant));
        }

        map = (Button) findViewById(R.id.buttonMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this, MyLocationActivity.class);
                intent.putExtra("ToMap", restaurant);
                startActivity(intent);
            }
        });

        fav = (Button) findViewById(R.id.buttonFavoris);
        favoriteButtonText(settings);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToFavorites(settings, restaurant.name, restaurant.id);
            }
        });
    }

    private void favoriteButtonText(SharedPreferences settings) {
        if (!settings.contains(String.valueOf(restaurant.id))){
            fav.setText(R.string.add_text_button_favoris);
        } else {
            fav.setText(R.string.rm_text_button_favoris);
        }
    }

    private void actionToFavorites(SharedPreferences settings, String keyName, int restaurantId) {
        String message;
        SharedPreferences.Editor editor = settings.edit();

        // verification si favoris existe
        if (!settings.contains(String.valueOf(restaurantId))){
            editor.putString(String.valueOf(restaurantId), keyName);
            message = getString(R.string.toast_add_favorite);
        } else {
            editor.remove(String.valueOf(restaurantId));
            message = getString(R.string.toast_rm_favorite);
        }

        // Commit the edits!
        editor.apply();
        favoriteButtonText(settings);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
