package com.example.jgill.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JGill on 15/11/14.
 */
public class Country implements Parcelable {

    private String name = "";
    private String id = "";
    private String iso2Code = "";
    private String region = "";
    private String incomeLevel = "";
    private String capitalCity = "";
    private String longitude = "";
    private String latitude = "";
    private int imageLocation=0;
    public Country(String jsonText) {
        super();
        try {
            JSONObject jsonObjectCountry = new JSONObject(jsonText);
            
            name = jsonObjectCountry.getString("name");
            JSONObject regionObject = jsonObjectCountry.getJSONObject("region");
            region = regionObject.getString("value");
            //System.out.println(region);

            id = jsonObjectCountry.getString("id");

            iso2Code=jsonObjectCountry.getString("iso2Code");

            JSONObject incomeLevelObject = jsonObjectCountry.getJSONObject("incomeLevel");
            incomeLevel = incomeLevelObject.getString("value");
            //System.out.println(incomeLevel);

            capitalCity = jsonObjectCountry.getString("capitalCity");
            //System.out.println(capitalCity);

            longitude = jsonObjectCountry.getString("longitude");
            //System.out.println(longitude);

            latitude = jsonObjectCountry.getString("latitude");
            //  System.out.println(latitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(region);
        dest.writeString(incomeLevel);
        dest.writeString(capitalCity);
        dest.writeString(longitude);
        dest.writeString(latitude);

    }

    private Country(Parcel parcelSent) {
        this.name = parcelSent.readString();
        this.id = parcelSent.readString();
        this.region = parcelSent.readString();
        this.incomeLevel = parcelSent.readString();
        this.capitalCity = parcelSent.readString();
        this.longitude = parcelSent.readString();
        this.latitude = parcelSent.readString();
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {

        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
    
    //below are GETTERS AND SETTERS for Country may need to add country code

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getIso2Code() {return iso2Code; }

    public void setImageLocation(int imageLocation) {
        this.imageLocation = imageLocation;
    }

    public int getImageLocation() {return imageLocation;}

    public String getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    }

    public String getIncomeLevel() {
        return incomeLevel;
    }

    public void setIncomeLevel(String incomeLevel) {
        this.incomeLevel = incomeLevel;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


}
