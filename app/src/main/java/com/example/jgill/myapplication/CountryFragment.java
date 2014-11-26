package com.example.jgill.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
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

public class CountryFragment extends Fragment {

    private TextView titleView;
    private TextView regionView;
    private TextView incomeLevelView;
    private TextView capitalCityView;
    private TextView longitudeView;
    private TextView latitudeView;

    private Button openPopulationBtn;

    private static Country countryObject;
    private ArrayList<Integer> populationArrayList = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryObject = getActivity().getIntent().getParcelableExtra("countryObject");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_country, container, false);

        titleView = (TextView) v.findViewById(R.id.Title);
        regionView = (TextView) v.findViewById(R.id.region);
        incomeLevelView = (TextView) v.findViewById(R.id.incomeLevel);
        capitalCityView = (TextView) v.findViewById(R.id.capitalCity);
        longitudeView = (TextView) v.findViewById(R.id.longitude);
        latitudeView = (TextView) v.findViewById(R.id.latitude);
        openPopulationBtn = (Button) v.findViewById(R.id.population);

        titleView.setText("Country: " + countryObject.getName());
        regionView.setText("Region: " + countryObject.getRegion());
        incomeLevelView.setText("IncomeLevel: " + countryObject.getIncomeLevel());
        capitalCityView.setText("Capital City: " + countryObject.getCapitalCity());
        longitudeView.setText("Longitude: " + countryObject.getLongitude());
        latitudeView.setText("Latitude : " + countryObject.getLatitude());

        openPopulationBtn = (Button) v.findViewById(R.id.population);
        setupBtn();

        return v;
    }

    private void setupBtn() {
        openPopulationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new MyAsyncTask().execute();
                Intent intent = new Intent(getActivity().getApplicationContext(), SimpleXYPLOTActivity.class);

                intent.putExtra("countryObject", countryObject);
                startActivity(intent);
            }
        });
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {
        
        @Override
        protected String doInBackground(String... params) {
            // HTTP Client that supports streaming uploads and downloads
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new BasicHttpParams());

            ///
            //define the Url for the task// need to Create a queryBUilder for the Urls
            String url = "http://api.worldbank.org/countries/" + countryObject.getId() + "/indicators/SP.POP.TOTL?date=1960:2009&format=json";

            ////

            InputStream inputStream = null;

            String jsonTextFromWorldBank = null;
            try {

                HttpPost httpPost = new HttpPost(url);

                httpPost.setHeader("Content-type", "application/json");
                //  System.out.println("dd" + countryObject.getId());
                // System.out.println(url + "kkk");
                HttpResponse httpResponse = defaultHttpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                inputStream = httpEntity.getContent();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                jsonTextFromWorldBank = bufferedReader.readLine();


                //     System.out.print(jsonTextFromWorldBank + "1234567");
                Log.d("urls", url);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

//            getActivity().getActionBar().setTitle(jsonTextFromWorldBank);

            return jsonTextFromWorldBank;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("resString", result);
            ArrayList<Integer> populationArrayList = new ArrayList<Integer>();
            result = result.substring(result.indexOf('['));
            result = result.substring(1, result.length() - 2);
            String[] parts = result.split(",\\{");
            JSONObject jsonObjectPopulationData = null;
            try {
                int index = 49;
                for (String population : parts) {

                    if (!population.startsWith("{")) {

                        population = "{" + population;

                    } else {
                        population = population.substring(result.indexOf('[') + 1);
                    }

                    jsonObjectPopulationData = new JSONObject(population);
                    int value = Integer.parseInt(jsonObjectPopulationData.getString("value"));
                    populationArrayList.add(value);
                    // series1Numbers60[index] = value;
                    //JSONObject

                    index--;

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            //  createListView();


        }

    }
}
