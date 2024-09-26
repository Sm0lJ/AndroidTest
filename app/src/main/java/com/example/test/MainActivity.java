package com.example.test;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnScannerButtonClickListener, DeliveryAdapter.OnDeliveryClickListener, BranchAdapter.OnBranchClickListener {
   private RecyclerView recyclerView;
    private DeliveryAdapter deliveryAdapter;
    private BranchAdapter branchAdapter;
    private ItemAdapter itemAdapter;
    private List<Item> items; // All items
    private Map<String, List<Item>> branchesByDelivery; // Delivery -> Branches
    private Map<String, List<Item>> itemsByBranch; // Branch -> Items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SSLUtils.trustAllCertificates();

        recyclerView = findViewById(R.id.itemsOfSelectionRecyclerView); // Your RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        deliveryAdapter = new DeliveryAdapter(Collections.emptyList(), this); // Initialize delivery adapter
        branchAdapter = new BranchAdapter(Collections.emptyList(), this); // Initialize branch adapter
        itemAdapter = new ItemAdapter(Collections.emptyList()); // Initialize item adapter

        // Fetch data
        String url = "https://10.0.2.2:7083/api/desktop/ExportScanning";
        String clientId = "test-client-id";
        String clientSecret = "testClientSecret";
        new FetchDataTask().execute(url, clientId, clientSecret);

    }
    private class FetchDataTask extends AsyncTask<String, Void, List<Item>> {
        @Override
        protected List<Item> doInBackground(String... params) {
            String url = params[0];
            String clientId = params[1];
            String clientSecret = params[2];

            try {
                return sendGetRequest(url, clientId, clientSecret);
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }

        private List<Item> sendGetRequest(String urlString, String clientId, String clientSecret) throws Exception {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String credentials = clientId + ":" + clientSecret;
            String basicAuth = "Basic " + android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                ResponseWrapper response = gson.fromJson(content.toString(), ResponseWrapper.class);
                return response.items;
            } else {
                return Collections.emptyList();
            }
        }

    @Override
    protected void onPostExecute(List<Item> resultItems) {
        items = resultItems;
        branchesByDelivery = groupBranchesByDelivery(items);
        itemsByBranch = groupItemsByBranch(items);
        List<String> deliveryNames = new ArrayList<>(branchesByDelivery.keySet());
        deliveryAdapter.updateDeliveries(deliveryNames);
        recyclerView.setAdapter(deliveryAdapter); // Set Delivery Adapter to RecyclerView
    }
    }

    private Map<String, List<Item>> groupBranchesByDelivery(List<Item> items) {
        Map<String, List<Item>> map = new HashMap<>();
        for (Item item : items) {
            String delivery = item.getDelivery();
            if (!map.containsKey(delivery)) {
                map.put(delivery, new ArrayList<>());
            }
            map.get(delivery).add(item); // Assuming item also contains branch information
        }
        return map;
    }

    // Group items by branch
    private Map<String, List<Item>> groupItemsByBranch(List<Item> items) {
        Map<String, List<Item>> map = new HashMap<>();
        for (Item item : items) {
            String branch = item.getBranch();
            if (!map.containsKey(branch)) {
                map.put(branch, new ArrayList<>());
            }
            map.get(branch).add(item);
        }
        return map;
    }

    @Override
    public void onDeliveryClick(String deliveryName) {
        List<Item> branchesForDelivery = branchesByDelivery.get(deliveryName);
        branchAdapter.updateBranches(branchesForDelivery);
        recyclerView.setAdapter(branchAdapter); // Switch to branch adapter
    }

    @Override
    public void onBranchClick(Item branch) {
        List<Item> itemsForBranch = itemsByBranch.get(branch.getBranch());
        itemAdapter.updateItems(itemsForBranch);
        recyclerView.setAdapter(itemAdapter); // Switch to item adapter
    }

    @Override
    public void onScanButtonClick() {

    }
}