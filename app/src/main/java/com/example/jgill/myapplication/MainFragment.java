package com.example.jgill.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    // setup variables
    private static String url = "http://api.worldbank.org/countries?format=json&&per_page=1000";

    // setup String ArrayList For ListView
    private static ArrayList<String> myItems = new ArrayList<String>();

    // setup Country ArrayList to Pass through to next Activity;
    private static ArrayList<Country> countryArrayList = new ArrayList<Country>();

    private ListView listView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private GridView gridView;
    private GridViewAdapter customGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //   listView = (ListView) v.findViewById(R.id.listView);

        getActivity().setTitle("World Bank Project");
        getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(204,0,0)));

        gridView = (GridView) v.findViewById(R.id.gridView);





        ///
        //mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        //  mRecyclerView.setItemAnimator(new RadItemAnimator);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //  mLayoutManager = new LinearLayoutManager(getActivity());

        //mRecyclerView.setLayoutManager(mLayoutManager);
        //  mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        //create card view called after





        ///
        new MyAsyncTask().execute();
        //   registerTapsOnLists();// sets up action listener for the ListView
        //   registerTapsOnRegisterView();
        return v;
    }



    private class MyAsyncTask extends AsyncTask<String, String, String> {
         /*
            AsyncTask is a background Task that calls a response from a url
         */
    /*
    doInBackground called when AsyncTask.execute() called.

    here an InputStream is created to receive input from the Url Stream
    then this String is used as the return String variable for doInBackground

    @param params
    @return jsonTextFromWorldBank String received from url
     */

        @Override
        protected String doInBackground(String... params) {

            InputStream inputStream = null;

            String jsonTextFromWorldBank = null;
            try {
                inputStream = new URL(url).openStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                jsonTextFromWorldBank = bufferedReader.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception e) {
                }
            }


            Log.d("Url Response", jsonTextFromWorldBank);// System.out.println()  android version
            return jsonTextFromWorldBank;
        }

        /*
         onPostExecute called when after doInBackground called.
        
           @param result String received from doInBackGround
        
           This method converts the Result json String from the url and converts into a usable Json format
        
          */
        @Override
        protected void onPostExecute(String result) {
            JSONArray jsonArray= null;
            try {
                jsonArray = new JSONArray(result);
                JSONArray jsonArrayData= jsonArray.getJSONArray(1);

                for(int i=0;i<jsonArrayData.length();i++){
                    JSONObject jsonObjectCountry = new JSONObject(jsonArrayData.get(i).toString());

                    if(!jsonObjectCountry.getString("capitalCity").equals("")){

                        Country countryObject = new Country(jsonArrayData.get(i).toString());
                        //then add to Countries ArrayList and ListView ArrayList
                        Log.d("canda",countryObject.getIso2Code()+"  COuntry=  "+countryObject.getName());
                        countryObject.setImageLocation(nameConverter(countryObject.getIso2Code().toLowerCase()));

                        countryObject.setImageLocation(R.drawable.ca);

                        countryArrayList.add(countryObject);
                        myItems.add(countryObject.toString());
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            createGridView();

            //after ArrayList is created ListView Is Updated
        }

    }

    /*
    createListView Updates List with the newest values of the arrayList
    */
    private int nameConverter (String countryName) {
        int intId = 0;

       // String editedCountryName = countryName.replaceAll(" ","_").toLowerCase();
        //System.out.println(countryName);
        intId = getActivity().getResources().getIdentifier(countryName, "drawable", getActivity().getPackageName());
        Log.d("aaa",intId+"");

        if(intId != 0) {
            return intId;
        } else {
            return R.drawable.canada;
        }
    }

    private void createGridView() {

        customGridAdapter = new GridViewAdapter(getActivity(), countryArrayList);
        gridView.setAdapter(customGridAdapter);
    }


    private void createListView() {


        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.country_textview, myItems);
        //  listView.setAdapter(adapter);
    }

    private void registerTapsOnRegisterView() {

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
    }

    /*
    registerTapsOnLists creates setOnItemClickListener for the ListView and creates intent to go to next activity
    and sends the Country Object in the Intent
    */
    private void registerTapsOnLists() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get text view
                TextView textView = (TextView) view;
                String message = "You Clicked " + textView.getText().toString();

                //create intent to open activity
                Intent intent = new Intent(getActivity().getApplicationContext(), CountryActivity.class);

                intent.putExtra("countryObject", countryArrayList.get(position));
                startActivity(intent);

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
