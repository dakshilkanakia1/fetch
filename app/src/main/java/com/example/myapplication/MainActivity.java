package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

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
import java.util.ArrayList;
import android.os.*;
import java.net.URL;
import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<String> nameslist;
    ArrayList<Integer> idslist;
    ArrayList<Integer> listidslist;
    ArrayAdapter<String> nameAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializenameslist();
        binding.button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new fetchData().start();
            }
        });

    }

    private void initializenameslist() {
        nameslist = new ArrayList<>();
        idslist = new ArrayList<>();
        listidslist = new ArrayList<>();
        nameAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nameslist);
        binding.nameslist.setAdapter(nameAdapter);
    }

    class fetchData extends Thread{

        String data = "";



        @Override
        public void run() {


            mainHandler.post(new Runnable(){
                @Override
                public void run(){
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            try {
                URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
                // Proceed with your code to handle the URL connection and data fetching
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null){

                    data = data + line;
                }

                if (!data.isEmpty()){
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray users = jsonObject.getJSONArray(data);
                    nameslist.clear();
                    idslist.clear();
                    listidslist.clear();
                    for (int i=0;i<users.length();i++){

                        JSONObject names = users.getJSONObject(i);
                        String name = names.getString("name");
                        nameslist.add(name);
                        Integer id = names.getInt("id");
                        idslist.add(id);
                        Integer listid = names.getInt("listId");
                        listidslist.add(listid);
                    }

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                // Handle the exception
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            mainHandler.post(new Runnable(){
               @Override
               public void run(){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    nameAdapter.notifyDataSetChanged();
               }
            });
        }

    }
}
