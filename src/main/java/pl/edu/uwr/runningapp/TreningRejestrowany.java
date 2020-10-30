package pl.edu.uwr.runningapp;
import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class TreningRejestrowany extends AppCompatActivity{
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;

    TextView mDystans;
    Button mStart;
    Button mStop;
    Chronometer mStoper;
    private boolean isResume;
    Handler handler;
    Double szerA;
    Double dlugA;
    Double szerB;
    Double dlugB;
    Double checkSzer;
    Double roznicaSzer;
    Double roznicaDlug;
    float dystansAB;
    double dystansCalkowity;
    float speed;
    Double wysokosc;
    long tMiliSec = 0L;
    long tStart = 0L;
    long tBuff = 0L;
    long tUpdate = 0L;
    int sec;
    int min;
    int miliSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_rejestrowany);
        mDystans = (TextView)findViewById(R.id.dystans_km_textView);
        mStart = (Button)findViewById(R.id.start_button);
        mStop = (Button)findViewById(R.id.stop_button);
        mStoper = (Chronometer)findViewById(R.id.czas_biegu_stoper_Chronometr);
        handler = new Handler();



        dystansCalkowity = 0;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        //locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);




        locationCallBack = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateGPS();
            }
        };
        


        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstGPS();
                if(!isResume){
                    tStart= SystemClock.uptimeMillis();
                    handler.postDelayed(runnable,0);
                    mStoper.start();
                    isResume = true;
                }

                startLocationUpdates();

            }});

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isResume) {
                    tBuff += tMiliSec;
                    handler.removeCallbacks(runnable);
                    mStoper.stop();
                    isResume = false;
                }
                stopLocationUpdates();


            }});


    }

    private void stopLocationUpdates() {

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        mStop.setEnabled(false);
        mStart.setEnabled(true);
    }


    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());
        updateGPS();
        mStop.setEnabled(true);
        mStart.setEnabled(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    firstGPS();
                    updateGPS();
                }
                else {
                    Toast.makeText(this,"Need permission",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }


    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TreningRejestrowany.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    szerA = szerB;
                    dlugA = dlugB;
                    szerB = location.getLatitude();
                    dlugB = location.getLongitude();
                    Location locationA = new Location("punkt A");
                    Location locationB = new Location("punkt B");
                    locationA.setLatitude(szerA);
                    locationA.setLongitude(dlugA);
                    locationB.setLatitude(szerB);
                    locationB.setLongitude(dlugB);


                    dystansAB = (locationA.distanceTo(locationB))/1000;

                    dystansCalkowity = dystansCalkowity + Math.round(dystansAB * 100.0) / 100.0;;

                    speed = location.getSpeed();
                    wysokosc = location.getAltitude();
                    Log.i("dziala","Szerokosc A: " + szerA);
                    Log.i("dziala","Szerokosc B: "+ szerB);

                    updateUIValue(dystansCalkowity);


                }
            });
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    private void firstGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TreningRejestrowany.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    szerB = location.getLatitude();
                    dlugB = location.getLongitude();
                }
            });
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }



    private void updateUIValue(double fdystansCalkowity){
        mDystans.setText(String.valueOf(fdystansCalkowity));
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMiliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff+ tMiliSec;
            sec = (int) (tUpdate/1000);
            min = sec/60;
            sec = sec%60;
            miliSec =(int) (tUpdate%100);
            mStoper.setText(String.format("%02d",min)+":"+String.format("%02d",sec) + ":"+String.format("%02d",miliSec));
            handler.postDelayed(this,60);
        }
    };
}
