package com.example.jgill.myapplication;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by JGill on 15/11/14.
 */
public class AsyncTaskParser extends AsyncTask<String, String, String> {

    private static ArrayList<String> myItems = new ArrayList<String>();
    private static String url = "";

    public AsyncTaskParser(String url, ArrayList<String> myItems) {


    }

    @Override
    protected String doInBackground(String... params) {

        // HTTP Client that supports streaming uploads and downloads
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new BasicHttpParams());

        // Define that I want to use the POST method to grab data from
        // the provided URL
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json");
        // Web service used is defined
        //httppost.setHeader("Content-type", "application/json");

        // Used to read data from the URL
        InputStream inputStream = null;
        String jsonTextFromWorldBank = null;

        try {
            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();

            inputStream = httpEntity.getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            jsonTextFromWorldBank = bufferedReader.readLine();

        } catch (ClientProtocolException e) {
            System.out.println("d");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
            }
        }

        jsonTextFromWorldBank = jsonTextFromWorldBank.substring(jsonTextFromWorldBank.indexOf('['));
        jsonTextFromWorldBank = jsonTextFromWorldBank.substring(1, jsonTextFromWorldBank.length() - 2);

        return jsonTextFromWorldBank;
    }

    @Override
    protected void onPostExecute(String result) {

        String[] parts = result.split(",\\{");
        JSONObject jsonObjectCountry = null;
        
        try {
            for (String country : parts) {
                if (!country.startsWith("{")) {
                    jsonObjectCountry = new JSONObject("{" + country);
                    myItems.add(jsonObjectCountry.getString("name"));

                    //System.out.println(jsonObjectCountry.getString("name"));


                    // System.out.println("{"+country+"\n");
                } else {
                    jsonObjectCountry = new JSONObject(country);
                    //myItems.add(jsonObjectCountry.getString("name"));

                    //System.out.println(country+"\n");


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //createListView();

    }

    public static ArrayList<String> getMyItems() {
        return myItems;
    }
}
