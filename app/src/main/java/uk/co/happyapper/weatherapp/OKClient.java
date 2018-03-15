package uk.co.happyapper.weatherapp;

import android.util.Log;

import okhttp3.OkHttpClient;

/**
 * Created by marksheekey on 14/03/2018.
 */

public class OKClient {

    private static OkHttpClient client = client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();



    public static OkHttpClient getInstance() {
        if (client == null){
            Log.i("OKHTTPCLIENT","create");
            client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build();

        }else{
            Log.i("OKHTTPCLIENT","aready got");
        }
        return client;
    }

    private OKClient() {

    }

}
