package com.example.test;

import android.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiService {

    public String getExportScanningData(String clientId, String clientSecret) {
      //  String apiUrl = "http://10.0.2.5/api/desktop/ExportScanning";
        String apiUrl = "https://localhost:7083/api/desktop/ExportScanning";

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // Create the URL and open a connection
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the basic authentication header
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.encodeToString(auth.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // Set timeouts (optional)
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000); // 10 seconds

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the input stream
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else {
                // Handle non-OK responses
                System.out.println("HTTP error code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the BufferedReader and HttpURLConnection
            try {
                if (reader != null) {
                    reader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
