package com.example.test;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String branch = getIntent().getStringExtra("branch");
        String itemsJson = getIntent().getStringExtra("items");

        List<Item> items = new Gson().fromJson(itemsJson, new TypeToken<List<Item>>(){}.getType());
       // ItemListAdapter adapter = new ItemListAdapter(items);
        //recyclerView.setAdapter(adapter);
    }
}
