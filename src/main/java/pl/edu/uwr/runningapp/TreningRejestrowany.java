package pl.edu.uwr.runningapp;
import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class TreningRejestrowany extends AppCompatActivity{
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;

    TextView mDystans;
    TextView mLastLap;
    TextView mSrednie;
    TextView mAutoLap;
    Button mStart;
    Button mStop;
    Button mLap;
    Button mZapisz;
    Chronometer mStoper;
    private boolean isResume;
    private boolean jestReset;
    Handler handler;
    Double szerA = 0.0;
    Double dlugA = 0.0;
    Double szerB = 0.0;
    Double dlugB = 0.0;
    Double checkSzer;
    Double roznicaSzer;
    Double roznicaDlug;
    Double wysokosc = 0.0;
    String ostatniLapT;
    String caloscLap = "";
    String caloscAutoLap = "";
    String caloscLapT;
    String aktualnyLapT;
    String aktualnyAutoLapT;

    String aktualnyLapD;
    String LapStartowyT;
    float dystansAB = 0;
    double dystansCalkowity = 0.0;
    double dystansCalkowityTemp = 0.0;
    double dystansStartowy= 0.0;
    double ostatniLapD = 0.0;
    double dystansAutoLap = 0.0;
    double ostatniAutoLapD = 0.0;
    float speed = 0;
    long tMiliSec = 0L;
    long tStart = 0L;
    long tBuff = 0L;
    long tUpdate = 0L;
    long tDifference = 0L;
    long tDifferenceAuto = 0L;
    long lastLap = 0L;
    long lastAutoLap = 0L;
    double tSrednia = 0.0;
    int sec = 0;
    int min = 0;
    int miliSec = 0;
    int secLap = 0;
    int minLap = 0;
    int miliSecLap = 0;
    int secLapAuto = 0;
    int minLapAuto = 0;
    int miliSecLapAuto = 0;
    int secSrednia = 0;
    int minSreadnia = 0;
    int miliSecSrednia = 0;

    Integer ilosc_treningow;
    Integer id_treningu;
    Cursor wTreningi;
    Integer ostatni_id;
    String mRodzajB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_rejestrowany);
        mDystans = (TextView)findViewById(R.id.dystans_km_textView);
        mStart = (Button)findViewById(R.id.start_button);
        mStop = (Button)findViewById(R.id.stop_button);
        mLap = (Button)findViewById(R.id.lap_button);
        mZapisz = (Button)findViewById(R.id.zakoncz_i_zapisz_button);
        mStoper = (Chronometer)findViewById(R.id.czas_biegu_stoper_Chronometr);
        mLastLap = (TextView)findViewById(R.id.ostatni_lap_textView);
        mSrednie = (TextView)findViewById(R.id.średnie_tempo_wartość_textView);
        mAutoLap = (TextView)findViewById(R.id.Ostatni_km_wynik_textView);
        handler = new Handler();

        final DatabaseHelper mDBHelper = new DatabaseHelper(TreningRejestrowany.this);
        try{
            mDBHelper.createDataBase();
        }
        catch (IOException ioe){
            throw new Error("Unable to create database");
        }
        try{
            mDBHelper.openDataBase();
        }
        catch (SQLException sqle) {
            throw sqle;
        }
        ilosc_treningow = mDBHelper.countWszystkieTreningi("treningiwszystkie",null,null,null,null,null,null);
        if(ilosc_treningow == 0){
            id_treningu = 1;
        }
        else{
            wTreningi = mDBHelper.queryWszystkieTreningii("treningiwszystkie",null,null,null,null,null,null);
            if (wTreningi.moveToFirst()) {
                do {
                    ostatni_id = wTreningi.getInt(0);
                } while (wTreningi.moveToNext());
            }
            id_treningu = ostatni_id + 1;
        }
        Date dzisiaj = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final String dataTreningu = df.format(dzisiaj);
        //Log.i("dziala","Data: " + dataTreningu);





        dystansCalkowity = 0.0;
        dystansCalkowityTemp = 0.0;
        dystansStartowy = 0.0;
        dystansAutoLap = 0.0;
        ostatniAutoLapD = 0.0;
        LapStartowyT = "00:00:00";
        mDystans.setText("0.00 km");
        mAutoLap.setText("--:--");
        mSrednie.setText("--:--");
        jestReset = false;
        mLap.setText("LAP");
        mLap.setEnabled(false);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);




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
                jestReset = false;
                mLap.setEnabled(true);
                mLap.setText("LAP");
                mZapisz.setEnabled(false);
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
                jestReset = true;
                mLap.setText("RESET");
                stopLocationUpdates();
                mZapisz.setEnabled(true);


            }});

        mLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jestReset == Boolean.TRUE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TreningRejestrowany.this);
                    builder.setTitle("Ostrzeżenie");
                    builder.setMessage("Czy na pewno chcesz zresetować?");
                    builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dystansCalkowity = 0.0;
                            dystansCalkowityTemp = 0.0;
                            mDystans.setText("0.00 km");
                            tMiliSec = 0L;
                            tStart = 0L;
                            tBuff = 0L;
                            tUpdate = 0L;
                            sec = 0;
                            min = 0;
                            miliSec = 0;

                            ostatniLapD = 0.0;
                            ostatniAutoLapD = 0.0;
                            dystansStartowy = 0.0;
                            dystansAutoLap = 0.0;
                            aktualnyLapD = "";
                            aktualnyLapT="";
                            aktualnyAutoLapT = "";
                            tDifference = 0L;
                            tDifferenceAuto = 0L;
                            lastLap = 0L;
                            lastAutoLap = 0L;
                            caloscLapT = "";
                            caloscLap = "";

                            secLap = 0;
                            minLap = 0;
                            miliSecLap = 0;

                            secLapAuto = 0;
                            minLapAuto = 0;
                            miliSecLapAuto = 0;

                            tSrednia = 0.0;
                            secSrednia = 0;
                            minSreadnia = 0;
                            miliSecSrednia = 0;

                            mLastLap.setText("");
                            mStoper.setText("00:00:00");
                            mSrednie.setText("--:--");
                            mAutoLap.setText("--:--");

                            mZapisz.setEnabled(false);

                        }
                    });
                    builder.setNegativeButton("Nie", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else{
                    ostatniLapD = (dystansCalkowity - dystansStartowy);
                    dystansStartowy = dystansCalkowity;
                    aktualnyLapD = String.valueOf(ostatniLapD)+" km";

                    //aktualnyLapT = mStoper.getText().toString();

                    tDifference = tUpdate - lastLap;
                    lastLap = tUpdate;

                    secLap = (int) (tDifference/1000);
                    minLap = secLap/60;
                    secLap = secLap%60;
                    miliSecLap =(int) (tDifference%100);


                    caloscLapT = String.format("%02d",min)+":"+String.format("%02d",sec) + ":"+String.format("%02d",miliSec);

                    aktualnyLapT = String.format("%02d",minLap)+":"+String.format("%02d",secLap) + ":"+String.format("%02d",miliSecLap);

                    mLastLap.setText(aktualnyLapT + " - " + aktualnyLapD + " - " + caloscLapT);

                    caloscLap = caloscLap + aktualnyLapT + " - " + aktualnyLapD + "\n";


                    Toast.makeText(getApplicationContext(),aktualnyLapT+ " - " + aktualnyLapD, Toast.LENGTH_LONG).show();

                }


            }});

        mZapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String czasB = String.format("%02d",min)+":"+String.format("%02d",sec) + ":"+String.format("%02d",miliSec);
                String dystansB = String.valueOf(dystansCalkowity);
                String poszczegolneOdcinki = "Lapy: \n"+ caloscLap + "AutoLapy: \n"+ caloscAutoLap;
                String srednieTempo = String.format("%02d",minSreadnia)+":"+String.format("%02d",secSrednia);
                String komentarzB = "Wysokość n.p.m.: "+ String.valueOf(wysokosc);
                String rodzajB = "Bieganie";
                Boolean poprawnosc = TRUE;

                if(poprawnosc == TRUE) {
                    Float dystansBb = Float.parseFloat(dystansB);
                    mDBHelper.dodajTreningBiegowyW(id_treningu, dataTreningu,rodzajB,dystansBb,czasB,komentarzB,poszczegolneOdcinki,srednieTempo);
                    Toast.makeText(getApplicationContext(),"Wykonany trening został zapisany", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TreningRejestrowany.this,MainActivity.class);
                    startActivity(intent);
                    mDBHelper.close();
                    finish();



                }
                else {
                    Toast.makeText(getApplicationContext(),"Coś poszło nie tak. Spróbuj jeszcze raz.", Toast.LENGTH_LONG).show();
                }

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
                    //Log.i("dziala","Dystans1: " + dystansAB);

                    //dystansCalkowity = dystansCalkowity + Math.round(dystansAB * 100.0) / 100.0;
                    dystansCalkowityTemp = dystansCalkowityTemp + dystansAB;
                    //dystansCalkowity = dystansCalkowity + dystansAB;

                    dystansCalkowity = Math.round(dystansCalkowityTemp * 100.0) / 100.0;

                    Log.i("dziala","Dystans1: " + dystansCalkowityTemp);
                    Log.i("dziala","Dystans2: " + dystansCalkowity);

                    //if(location.hasSpeed()){
                      //  speed = location.getSpeed();
                   // }


                    wysokosc = location.getAltitude();
                    wysokosc = Math.round(wysokosc * 100.0) / 100.0;
                    Log.i("dziala","Szerokosc A: " + szerA);
                    Log.i("dziala","Szerokosc B: "+ szerB);

                    updateUIValue(dystansCalkowity);

                    if(dystansCalkowity >= 0.01){
                        //tSrednia = tUpdate/(Double.valueOf(dystansCalkowity).longValue());
                        tSrednia = ((min * 60) + sec)/dystansCalkowity;
                        secSrednia = (int) (tSrednia);
                        minSreadnia = secSrednia/60;
                        secSrednia = secSrednia%60;
                        mSrednie.setText(String.format("%02d",minSreadnia)+":"+String.format("%02d",secSrednia));}
                    else{
                        mSrednie.setText("--:--");
                    }

                    if(dystansAutoLap >= 1.0){
                        tDifferenceAuto = tUpdate - lastAutoLap;
                        lastAutoLap = tUpdate;

                        secLapAuto = (int) (tDifferenceAuto/1000);
                        minLapAuto = secLapAuto/60;
                        secLapAuto = secLapAuto%60;
                        miliSecLapAuto =(int) (tDifferenceAuto%100);

                        aktualnyAutoLapT = String.format("%02d",minLapAuto)+":"+String.format("%02d",secLapAuto) + ":"+String.format("%02d",miliSecLapAuto);
                        mAutoLap.setText(aktualnyAutoLapT);
                        caloscAutoLap = caloscAutoLap + aktualnyAutoLapT + " - " + dystansAutoLap + "\n";

                        ostatniAutoLapD = dystansCalkowityTemp;
                        dystansAutoLap = dystansCalkowityTemp - ostatniAutoLapD;

                    }
                    else {
                        dystansAutoLap = dystansCalkowityTemp - ostatniAutoLapD;
                    }


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
        mDystans.setText(String.valueOf(fdystansCalkowity)+" km");

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
