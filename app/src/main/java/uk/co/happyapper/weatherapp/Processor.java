package uk.co.happyapper.weatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by marksheekey on 14/03/2018.
 */

public class Processor {

    private Context mContext;
    private ActivityInterface activity;

    public Processor(Context context){
        mContext = context;
        activity = (ActivityInterface) context;

    }

    public void getTowns(String searchText){
        new getTownsFromApi().execute(searchText);
    }

    public void getWeatherByLocation(LatLng coords){
        new getTownsFromApiWithCoords().execute(coords);
    }

    private class getTownsFromApi extends AsyncTask<String,Void,ArrayList<WeatherFromAPI>> {

        @Override
        protected ArrayList<WeatherFromAPI> doInBackground(String... params) {
            Data data = new Data(mContext);
            try {
                return data.searchForTown(params[0]);
            }catch(Exception e){
                Log.i("Data",e.getMessage()+"");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherFromAPI> towns) {
            super.onPostExecute(towns);

            if(towns ==null ){
                return;
            }

            if(towns.size()>0) {
                activity.gotTown(towns.get(0));
            }
            }
        }
    private class getTownsFromApiWithCoords extends AsyncTask<LatLng,Void,ArrayList<WeatherFromAPI>> {

        @Override
        protected ArrayList<WeatherFromAPI> doInBackground(LatLng... params) {
            Data data = new Data(mContext);
            try {
                return data.searchForTownByCoords(params[0]);
            }catch(Exception e){
                Log.i("Data",e.getMessage()+"");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherFromAPI> towns) {
            super.onPostExecute(towns);

            if(towns ==null ){
                return;
            }

            if(towns.size()>0) {
                activity.gotTown(towns.get(0));
            }
        }
    }



}
