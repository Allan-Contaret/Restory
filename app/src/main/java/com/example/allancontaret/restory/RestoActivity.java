package com.example.allancontaret.restory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RestoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);

        Intent intent = getIntent();
        if (intent.hasExtra("MyClass"))
        {
            final Restaurant myExtra= (Restaurant) intent.getSerializableExtra("MyClass");
            Log.i("intent", String.valueOf(myExtra.name));

            //textView
            TextView textView = new TextView(this);
            textView.setTextSize(40);
            textView.setText(myExtra.description);

            setContentView(R.layout.activity_resto);

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_resto);

            layout.addView(textView,0);
        }
    }
}
