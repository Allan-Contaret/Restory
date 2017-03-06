package com.example.allancontaret.restory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class RestaurantActivity extends AppCompatActivity {
    Restaurant restaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

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
    }
}
