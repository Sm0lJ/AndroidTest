package com.example.test;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button fetchButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchButton = findViewById(R.id.fetchButton);
        resultTextView = findViewById(R.id.resultTextView);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientId = "test-client-id";  // Replace with your client ID
                String clientSecret = "testClientSecret";  // Replace with your client secret
                new FetchDataTask().execute(clientId, clientSecret);
            }
        });
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            ApiService apiService = new ApiService();
            return apiService.getExportScanningData(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                resultTextView.setText(result);
            } else {
                resultTextView.setText("Failed to fetch data.");
            }
        }
    }
}
