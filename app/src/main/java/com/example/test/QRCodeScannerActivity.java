package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QRCodeScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ScanOptions
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivity.class);  // Optional: for customizing the scanning UI

        // Start the scan using the ScanContract
       // handleSerialNumber("");
      //  qrCodeScannerLauncher.launch(options);
    }

    // Register for the scan result
    private final ActivityResultLauncher<ScanOptions> qrCodeScannerLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    String scannedData = result.getContents();

                    if (scannedData.startsWith("C")) {
                        // It's an employee with position, extract employee and workplace
                        handleEmployeeAndWorkplace(scannedData);
                    } else {
                        // It's a serial number, call method to post to server
                        handleSerialNumber(scannedData);
                    }
                } else {
                    Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
                }
            }
    );

    // Handle Employee and Workplace QR Code
    private void handleEmployeeAndWorkplace(String scannedData) {
        // Regex to extract both Z and P codes regardless of the order
        Pattern pattern = Pattern.compile("C-(Z\\d+|P\\d+)-(Z\\d+|P\\d+)");
        Matcher matcher = pattern.matcher(scannedData);

        if (matcher.matches()) {
            String first = matcher.group(1);
            String second = matcher.group(2);

            String employee = "";
            String workplace = "";

            if (first.startsWith("Z")) {
                employee = first.substring(1); // Remove "Z" to get the employee number
            } else if (first.startsWith("P")) {
                workplace = first.substring(1); // Remove "P" to get the workplace
            }

            if (second.startsWith("Z")) {
                employee = second.substring(1); // Remove "Z" to get the employee number
            } else if (second.startsWith("P")) {
                workplace = second.substring(1); // Remove "P" to get the workplace
            }

            // Pass the extracted employee and workplace to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("employee", employee);
            resultIntent.putExtra("workplace", workplace);
            setResult(RESULT_OK, resultIntent);
            finish(); // Close the QRScannerActivity and return to MainActivity

        } else {
            Toast.makeText(this, "Invalid QR code format for employee and workplace", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Serial Number QR Code
    private void handleSerialNumber(String serialNumber) {
        // Retrieve employee and workplace IDs from MainActivity
        int employeeId = getIntent().getIntExtra("employeeId", 0);
        int workPlaceId = getIntent().getIntExtra("workPlaceId", 0);

        emtz(serialNumber, employeeId, workPlaceId);
    }

    // Method to Post Serial Number to the Server
    private void emtz(String serialNumber, int employeeId, int workPlaceId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.5:80/api/desktop/WorkRecord");
               // URL url = new URL("https://10.0.2.2:7083/api/desktop/WorkRecord");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Add Basic Authentication header
                String clientId =  "test-client-id";
                String clientSecret = "testClientSecret";
                String auth = clientId + ":" + clientSecret;
                String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization", "Basic " + encodedAuth);

                // Set request properties
                urlConnection.setDoOutput(true);

                // Create JSON payload
                JSONObject jsonParam = new JSONObject();
               // jsonParam.put("serialNumber", serialNumber);
                jsonParam.put("serialNumber", serialNumber);
                jsonParam.put("weight", JSONObject.NULL);  // Keep weight null
                jsonParam.put("position", JSONObject.NULL); // Keep position null

                // Add employee object
                JSONObject employeeJson = new JSONObject();
              //  employeeJson.put("id", employeeId);
                employeeJson.put("id", employeeId);
                jsonParam.put("employee", employeeJson);

                // Add workplace object
                JSONObject workPlaceJson = new JSONObject();
            //    workPlaceJson.put("id", workPlaceId);
                workPlaceJson.put("id", workPlaceId);
                jsonParam.put("workPlace", workPlaceJson);

                // Send the request
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                // Check server response
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(QRCodeScannerActivity.this, "Serial number posted successfully", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(QRCodeScannerActivity.this, "Failed to post serial number", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(QRCodeScannerActivity.this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
