package uk.co.happyapper.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marksheekey on 14/03/2018.
 */

public class Data {

    Context mContext;
    String mBaseUrl = "api.openweathermap.org/data/2.5/weather";
    String APIKEY="3b76fbe3e4a9e0846b5c5e77a3426a10";

    public Data(Context context){
        mContext =context;
    }

    public ArrayList<WeatherFromAPI> searchForTown(String find){
        APIResponse r = restGET("?q="+find);
        if(r.ok){
            ArrayList<WeatherFromAPI> towns = new ArrayList<>();
            Gson gson = new Gson();
            WeatherFromAPI wfa = gson.fromJson(r.message,WeatherFromAPI.class);
            if(wfa.weather.get(0).icon!=null){

                //replace icon name with full path to downloaded image
                wfa.weather.get(0).icon = getIcon(wfa.weather.get(0).icon).message;
            }
            towns.add(wfa);
            return towns;
        }else{
            return null;
        }

    }

    public ArrayList<WeatherFromAPI> searchForTownByCoords(LatLng coords){

        String lat = coords.latitude+"";
        String lng = coords.longitude+"";

        APIResponse r = restGET("?lat="+lat+"&lon="+lng);
        if(r.ok){
            ArrayList<WeatherFromAPI> towns = new ArrayList<>();
            Gson gson = new Gson();
            WeatherFromAPI wfa = gson.fromJson(r.message,WeatherFromAPI.class);
            if(wfa.weather.get(0).icon!=null){

                //replace icon name with full path to downloaded image
                wfa.weather.get(0).icon = getIcon(wfa.weather.get(0).icon).message;
            }
            towns.add(wfa);
            return towns;
        }else{
            return null;
        }

    }

    private APIResponse restGET(String endpoint){

        APIResponse apiResponse = new APIResponse();
        apiResponse.ok = true;
        apiResponse.code = 0;
        OkHttpClient client = OKClient.getInstance();
        String url = "http://"+mBaseUrl+endpoint+"&appid="+APIKEY+"&units=metric";
        Log.i("restGET",url);
        Request request;  request = new Request.Builder()
                    .url(url)
                    .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            apiResponse.ok = true;
            apiResponse.message = body;
            apiResponse.code = response.code();
            //not authorised
            if(apiResponse.code >=400 && apiResponse.code<=599){
                apiResponse.ok = false;
            }

            Log.i("restGET",apiResponse.message+"");

            return apiResponse;
        } catch (IOException e) {
            Log.i("Data",e.getMessage()+"");
            e.printStackTrace();
            apiResponse.ok = false;
            apiResponse.code = 666;
            apiResponse.message = "Exception in GET";
            return apiResponse;

        }


    }

    private APIResponse getIcon(String icon){
        APIResponse r = new APIResponse();
        OkHttpClient client = OKClient.getInstance();
        String url = "http://openweathermap.org/img/w/"+icon+".png";
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        Bitmap mIcon11 = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            r.ok=false;
            r.message=null;
            return r;
        }
        if (response.isSuccessful()) {
            try {
                mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                r.ok=false;
                r.message=null;
                return r;
            }

        }

        r.ok=true;
        r.message= SaveImage(mIcon11,icon);
        return r;

    }

    private String SaveImage(Bitmap finalBitmap,String name) {

        String root = mContext.getExternalCacheDir().getAbsolutePath();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        File file = new File (myDir, name);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

}
