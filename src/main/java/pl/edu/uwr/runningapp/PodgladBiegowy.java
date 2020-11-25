package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class PodgladBiegowy extends AppCompatActivity {
    TextView mRodzajTreninguBiegowy;
    TextView mDataTreninguBiegowy;
    TextView mDystansTreninguBiegowy;
    TextView mCzasTreninguBiegowy;
    TextView mTrescTreninguBiegowy;
    EditText mKomentarzBiegowy;
    Button mDodajKomentarzBiegowy;
    Button mPokazMapeBiegowy;
    Button mUsunBiegowy;

    String treningBiegowy;
    String rodzajBiegowy;
    String mDataTreninguSBiegowy;
    String mDystansTreninguSBiegowy;
    Float mDystansTreninguFBiegowy;
    String  mCzasTreninguSBiegowy;
    String mTrescTreninguSBiegowy;
    String mKomentarzSBiegowy;
    String mPoszczegolneOdcinkiBiegowy;
    String mSrednieTempoBiegowy;
    String mDodawanyKomentarzBiegowy;
    String mCaloscTresciBiegowy;
    Integer treningIBiegowy;

    String mSzer;
    String mDlug;
    String mSzerokosci[];
    String mDlugosci[];
    List<Location> savedLocation;

    String mWysokoscUp;
    String mWysokoscDown;

    Cursor CaloscTrening;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podglad_biegowy);
        getSupportActionBar().setTitle("Trening");

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null)
        {
            treningBiegowy =(String) b.get("Nr treningu");
            rodzajBiegowy = (String) b.get("RodzajTr");

        }

        mRodzajTreninguBiegowy = (TextView)findViewById(R.id.Rodzaj_podglad_textView_Biegowy);
        mDataTreninguBiegowy = (TextView)findViewById(R.id.Data_podglad_textView_Biegowy);
        mDystansTreninguBiegowy = (TextView)findViewById(R.id.Dystans_podglad_textView_Biegowy);
        mCzasTreninguBiegowy = (TextView)findViewById(R.id.Czas_podglad_textView_Biegowy);
        mTrescTreninguBiegowy = (TextView)findViewById(R.id.Tresc_treningu_textView_Biegowy);
        mKomentarzBiegowy = (EditText) findViewById(R.id.Komentarz_podglad_editText_Biegowy);
        mDodajKomentarzBiegowy = (Button) findViewById(R.id.Dodaj_komentarz_button_Biegowy);
        mPokazMapeBiegowy = (Button) findViewById(R.id.Pokaz_mapa_button_Biegowy);
        mUsunBiegowy = (Button) findViewById(R.id.Usun_trening_button_Biegowy);

        mTrescTreninguBiegowy.setMovementMethod(new ScrollingMovementMethod());
        mKomentarzBiegowy.setMovementMethod(new ScrollingMovementMethod());

        final DatabaseHelper mDBHelper = new DatabaseHelper(PodgladBiegowy.this);
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

        CaloscTrening = mDBHelper.queryPodgladTreningu(treningBiegowy);
        if (CaloscTrening.moveToFirst()) {
            do {
                mDataTreninguSBiegowy = CaloscTrening.getString(1);
                mDystansTreninguFBiegowy = CaloscTrening.getFloat(3);
                mDystansTreninguSBiegowy = mDystansTreninguFBiegowy.toString();
                mCzasTreninguSBiegowy = CaloscTrening.getString(4);
                mKomentarzSBiegowy = CaloscTrening.getString(5);
                mPoszczegolneOdcinkiBiegowy = CaloscTrening.getString(6);
                mSrednieTempoBiegowy = CaloscTrening.getString(7);
                mTrescTreninguSBiegowy = CaloscTrening.getString(9);
                mSzer = CaloscTrening.getString(10);
                mDlug = CaloscTrening.getString(11);
                mWysokoscUp = CaloscTrening.getString(12);
                mWysokoscDown = CaloscTrening.getString(13);
            } while (CaloscTrening.moveToNext());
        }
        mRodzajTreninguBiegowy.setText(rodzajBiegowy);
        mDataTreninguBiegowy.setText(mDataTreninguSBiegowy);
        mDystansTreninguBiegowy.setText("Dystans: " + mDystansTreninguSBiegowy);
        mCzasTreninguBiegowy.setText("Czas: " + mCzasTreninguSBiegowy);
        mCaloscTresciBiegowy ="Średnie tempo: "+mSrednieTempoBiegowy + "\n" + "Poszczególne odcinki: \n" + mPoszczegolneOdcinkiBiegowy + "\n" +  "Komentarz: \n"+mKomentarzSBiegowy + "\n";
        mTrescTreninguBiegowy.setText(mCaloscTresciBiegowy);

        mSzerokosci = mSzer.split(",");
        mDlugosci = mDlug.split(",");

        mDodajKomentarzBiegowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDodawanyKomentarzBiegowy = mKomentarzBiegowy.getText().toString();
                treningIBiegowy = Integer.parseInt(treningBiegowy);
                mDBHelper.updateKomentarz(mDodawanyKomentarzBiegowy,treningIBiegowy);
                Toast.makeText(getApplicationContext(), "Komentarz został dodany.", Toast.LENGTH_LONG).show();
            }});

        mPokazMapeBiegowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PodgladBiegowyApp podgladBiegowyApp = (PodgladBiegowyApp) getApplicationContext();
                savedLocation = podgladBiegowyApp.getmLocations();
                for (int i = 0; i<mSzerokosci.length; i++){
                    Location locationA = new Location("punkt A");
                    locationA.setLatitude(Double.valueOf(mSzerokosci[i]));
                    locationA.setLongitude(Double.valueOf(mDlugosci[i]));
                    savedLocation.add(locationA);
                }

                Intent intent = new Intent(PodgladBiegowy.this,MapsActivity.class);
                startActivity(intent);


            }});

        mUsunBiegowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PodgladBiegowy.this);
                builder.setTitle("Ostrzeżenie");
                builder.setMessage("Czy na pewno chcesz usunąć ten trening?");
                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.deleteTrening(treningBiegowy);
                        Intent intent = new Intent(PodgladBiegowy.this,Dziennik.class);
                        startActivity(intent);
                        mDBHelper.close();
                        finish();

                    }
                });
                builder.setNegativeButton("Nie", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }});
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,Dziennik.class);
        startActivity(intent);
        finish();
    }
}
