package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Map<String, List<Item>> branchesByBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_list);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String delivery = intent.getStringExtra("delivery");
        String itemsJson = intent.getStringExtra("items");

        List<Item> items = new Gson().fromJson(itemsJson, new TypeToken<List<Item>>(){}.getType());
        branchesByBranch = groupItemsByBranch(items);

       /* BranchListAdapter adapter = new BranchListAdapter(new ArrayList<>(branchesByBranch.keySet()), branch -> {
            Intent branchIntent = new Intent(BranchListActivity.this, ItemListActivity.class);
          //  branchIntent.putExtra("branch", branch);
           // branchIntent.putExtra("items", new Gson().toJson(branchesByBranch.get(branch)));
            startActivity(branchIntent);
        });
        recyclerView.setAdapter(adapter);*/
    }

    private Map<String, List<Item>> groupItemsByBranch(List<Item> items) {
        Map<String, List<Item>> branchMap = new HashMap<>();
        for (Item item : items) {
            String branch = item.getBranch();
            if (!branchMap.containsKey(branch)) {
                branchMap.put(branch, new ArrayList<>());
            }
            branchMap.get(branch).add(item);
        }
        return branchMap;
    }
}
