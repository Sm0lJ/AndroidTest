package com.example.test;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;

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

public class FirstListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Map<String, List<Item>> groupedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_list);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

      //  String url = "https://10.0.2.2:7083/api/desktop/ExportScanning";
        String url = "http://10.0.2.5:80/api/desktop/ExportScanning";
        String clientId = "test-client-id";
        String clientSecret = "testClientSecret";
        new FetchDataTask().execute(url, clientId, clientSecret);
    }

    private class FetchDataTask extends AsyncTask<String, Void, List<Item>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

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

        @Override
        protected void onPostExecute(List<Item> items) {
            progressBar.setVisibility(View.GONE);
            groupedItems = groupItemsByDelivery(items);
          /*  DeliveryListAdapter adapter = new DeliveryListAdapter(new ArrayList<>(groupedItems.keySet()), delivery -> {
                Intent intent = new Intent(FirstListActivity.this, BranchListActivity.class);
                intent.putExtra("delivery", delivery);
                intent.putExtra("items", new Gson().toJson(groupedItems.get(delivery)));
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);*/
        }

        private List<Item> sendGetRequest(String urlString, String clientId, String clientSecret) throws Exception {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String credentials = clientId + ":" + clientSecret;
            String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
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
                class ResponseWrapper {
                    List<Item> items;
                }
                ResponseWrapper response = gson.fromJson(content.toString(), ResponseWrapper.class);
                return response.items;
            } else {
                return Collections.emptyList();
            }
        }

        private Map<String, List<Item>> groupItemsByDelivery(List<Item> items) {
            Map<String, List<Item>> deliveryMap = new HashMap<>();
            for (Item item : items) {
                String delivery = item.getDelivery();
                if (!deliveryMap.containsKey(delivery)) {
                    deliveryMap.put(delivery, new ArrayList<>());
                }
                deliveryMap.get(delivery).add(item);
            }
            return deliveryMap;
        }
    }
}
