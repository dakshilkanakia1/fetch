package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    ArrayList<String> nameslist;
    ArrayList<Integer> idslist;
    ArrayList<Integer> listidslist;
    ArrayAdapter<String> nameAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeNamesList();
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchData().start();
            }
        });
    }

    private void initializeNamesList() {
        nameslist = new ArrayList<>();
        idslist = new ArrayList<>();
        listidslist = new ArrayList<>();
        nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nameslist);
        binding.nameslist.setAdapter(nameAdapter);
    }

    class FetchData extends Thread {

        StringBuilder data = new StringBuilder();

        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                int responseCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        data.append(line);
                    }

                    Log.d(TAG, "Data fetched: " + data.toString());
                    parseJSONData(data.toString());
                } else {
                    showError("Failed to connect to the server. Response code: " + responseCode);
                    Log.e(TAG, "Response Code: " + responseCode);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                showError("Invalid URL format.");
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error reading data from server.");
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                showError("Error parsing JSON data.");
                Log.e(TAG, "JSONException: " + e.getMessage());
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "IOException while closing BufferedReader: " + e.getMessage());
                    }
                }
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    nameAdapter.notifyDataSetChanged();
                }
            });
        }

        private void parseJSONData(String jsonData) throws JSONException {
            JSONArray users = new JSONArray(jsonData);
            nameslist.clear();
            idslist.clear();
            listidslist.clear();
            for (int i = 0; i < users.length(); i++) {
                JSONObject names = users.getJSONObject(i);
                String name = names.optString("name", "No Name");
                int id = names.optInt("id", -1);
                int listId = names.optInt("listId", -1);

                // Skip entries with empty or null names
                if (name != null && !name.trim().isEmpty()) {
                    nameslist.add(name);
                    idslist.add(id);
                    listidslist.add(listId);
                }
            }
        }

        private void showError(final String message) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    // Show error message to the user, e.g., using a Toast
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
