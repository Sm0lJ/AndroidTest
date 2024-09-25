package com.example.test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnScannerButtonClickListener {

    private List<Item> items;
    private Map<String, List<Item>> branchesByDelivery;

    // Variables for employeeId and workPlaceId
    private int employeeId;
    private int workPlaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SSLUtils.trustAllCertificates();

        // Fetch data from the server
       // String url = "https://10.0.2.2:7083/api/desktop/ExportScanning";
        String url = "http://10.0.2.5:80/api/desktop/ExportScanning";
        String clientId = "test-client-id";
        String clientSecret = "testClientSecret";
        new FetchDataTask().execute(url, clientId, clientSecret);

        // Load the initial fragment (DeliveryFragment)
        loadFragment(new DeliveryFragment());


        // Initialize default values for employeeId and workPlaceId (can be updated later)
        employeeId = 0;
        workPlaceId = 0;
    }
    public void onScanButtonClick() {
        // Handle the scanner button click
        // You can start the scanner activity or perform other actions here
        Toast.makeText(this, "Scanner button clicked!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
        startActivity(intent);

       /* Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
        intent.putExtra("employeeId", employeeId);  // Pass the employee ID
        intent.putExtra("workPlaceId", workPlaceId);  // Pass the workplace ID
        startActivityForResult(intent, SCAN_REQUEST_CODE); // Use a request code to identify the result
    */
    }
    private static final int SCAN_REQUEST_CODE = 100; // Define a constant for the request code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            String employee = data.getStringExtra("employee");
            String workplace = data.getStringExtra("workplace");
            updateEmployeeWorkplaceUI(employee, workplace);
        }
    }
    public void updateEmployeeWorkplaceUI(String employee, String workplace) {
        // Find the UI elements on the main screen
        TextView employeeTextView = findViewById(R.id.employeeTextView); // Make sure you have these in your layout
        TextView workplaceTextView = findViewById(R.id.workplaceTextView);

        // Update the TextViews with the employee and workplace
        employeeTextView.setText("Employee: " + employee);
        workplaceTextView.setText("Workplace: " + workplace);
    }

    // This method will be called when you need to pass the employee and workplace to the QRCodeScannerActivity
    public void startQRCodeScanner() {
        Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
        intent.putExtra("employeeId", employeeId);  // Pass the employee ID
        intent.putExtra("workPlaceId", workPlaceId);  // Pass the workplace ID
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
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

        @Override
        protected void onPostExecute(List<Item> resultItems) {
            items = resultItems;
            branchesByDelivery = groupItemsByDelivery(items);

            DeliveryFragment deliveryFragment = DeliveryFragment.newInstance(branchesByDelivery);
            loadFragment(deliveryFragment);
        }

        private List<Item> sendGetRequest(String urlString, String clientId, String clientSecret) throws Exception {
            URL url = new URL(urlString);
           // HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
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

        private Map<String, List<Item>> groupItemsByDelivery(List<Item> items) {
            Map<String, List<Item>> deliveryMap = new HashMap<>();
            for (Item item : items) {
                String delivery = item.getDelivery();
                if (!deliveryMap.containsKey(delivery)) {
                    deliveryMap.put(delivery, new java.util.ArrayList<>());
                }
                deliveryMap.get(delivery).add(item);
            }
            return deliveryMap;
        }
    }

    public Map<String, List<Item>> getBranchesByDelivery() {
        return branchesByDelivery;
    }

    // Method to update employeeId and workPlaceId (this can be called when employee/workplace is selected)
    public void setEmployeeAndWorkplace(int employeeId, int workPlaceId) {
        this.employeeId = employeeId;
        this.workPlaceId = workPlaceId;

        // Update the UI to show selected employee and workplace
        updateEmployeeWorkplaceUI(String.valueOf(employeeId), String.valueOf(workPlaceId));
    }
}
