package com.example.allancontaret.restory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    ListView mListView;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setTitle(R.string.favorites_title);
        mListView = (ListView) findViewById(R.id.listFavorite);

        settings = getSharedPreferences("MyPrefsFile", 0);
        Map<String, ?> allEntries = settings.getAll();
        List<String> values = new ArrayList<String>();
        for(Map.Entry<String,?> entry : allEntries.entrySet()){
            values.add(entry.getValue().toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        mListView.setAdapter(adapter);
    }
}
