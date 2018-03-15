package uk.co.happyapper.weatherapp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityInterface {

    private TextInputEditText mEntry;
    private LinearLayout mInfo;
    private TextView mDescription;
    private TextView mTownName;
    private ImageView mImage;
    private TextView mTemp;
    private TextView mSpeed;
    private TextView mDirection;
    private  Processor process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEntry = (TextInputEditText) findViewById(R.id.town_input);
        mInfo = (LinearLayout) findViewById(R.id.current_info);
        mInfo.setVisibility(View.GONE);
        mTownName = (TextView) findViewById(R.id.name);
        mDescription = (TextView) findViewById(R.id.description);
        mImage = (ImageView) findViewById(R.id.image);
        mTemp = (TextView) findViewById(R.id.temp);
        mSpeed = (TextView) findViewById(R.id.speed);
        mDirection = (TextView) findViewById(R.id.direction);

        process = new Processor(MainActivity.this);

        //watch for changes in entry text
        mEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //lets not bother with less than 3 characters
                mEntry.setError(null);
                mInfo.setVisibility(View.GONE);
                if (s.length() >= 3) {
                    process.getTowns(s.toString());
                }

            }
        });



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //normally for this sort of thing i've used location services, but always fancied trying this API out.
            getHere();


        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }



    }

    private void getHere(){

        GoogleApiClient client = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Awareness.API)
                .build();
        client.connect();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Awareness.SnapshotApi.getPlaces(client).setResultCallback(new ResultCallback<PlacesResult>() {
                @Override
                public void onResult(@NonNull PlacesResult placesResult) {
                    if (!placesResult.getStatus().isSuccess()) {
                        return;
                    }
                    List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();
                    PlaceLikelihood p = placeLikelihoodList.get(0);
                    process.getWeatherByLocation(p.getPlace().getLatLng());
                }
            });

        }
    }

    @Override
    public void gotTown(WeatherFromAPI wft) {
        mInfo.setVisibility(View.VISIBLE);
        mTownName.setText(wft.name);
        mDescription.setText(wft.weather.get(0).description);
        mDirection.setText(wft.wind.deg+"");
        mSpeed.setText(wft.wind.speed+"");
        mTemp.setText(wft.main.temp+"");
        mImage.setImageBitmap(BitmapFactory.decodeFile(wft.weather.get(0).icon));
    }

    @Override
    public void setError(String text) {
        mEntry.setError(text);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        if(grantResults==null){
            return;
        }



        if(requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }

            }
        }

        getHere();

    }

}
