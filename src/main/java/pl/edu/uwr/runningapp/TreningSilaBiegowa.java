package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class TreningSilaBiegowa extends AppCompatActivity {
    EditText mDataSB;
    EditText mCzasTreninguSB;
    EditText mDystansSB;
    EditText mTrescTreninguSB;
    String mRodzajSB;
    Button mZapiszSB;
    Integer ilosc_treningow;
    Integer id_treningu;
    Cursor wTreningi;
    Integer ostatni_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_sila_biegowa);
        getSupportActionBar().setTitle("Trening Siła Biegowa");
        mDataSB = (EditText)findViewById(R.id.Data_editText3);
        mCzasTreninguSB = (EditText)findViewById(R.id.Czas_trwania_editText3);
        mDystansSB = (EditText)findViewById(R.id.Dystans_editText3);
        mTrescTreninguSB = (EditText)findViewById(R.id.Tresc_treningu_editText3);
        mZapiszSB = (Button) findViewById(R.id.Zapisz_button3);

        final DatabaseHelper mDBHelper = new DatabaseHelper(TreningSilaBiegowa.this);
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


        mZapiszSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataSB = mDataSB.getText().toString();
                String czasSB = mCzasTreninguSB.getText().toString();
                String dystansSB = mDystansSB.getText().toString();
                String trescSB = mTrescTreninguSB.getText().toString();
                mRodzajSB = "Siła Biegowa";
                String rodzajSB = "Siła Biegowa";
                Boolean poprawnosc = TRUE;

                if(dataSB.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić datę treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(czasSB.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić czas treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(dystansSB.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić dystans.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(trescSB.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić treść treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(poprawnosc == TRUE) {
                    Float dystansSBF = Float.parseFloat(dystansSB);
                    mDBHelper.dodajTreningSilaBiegowaW(id_treningu,dataSB,rodzajSB,czasSB,dystansSBF,trescSB);
                    Toast.makeText(getApplicationContext(),"Trening siły biegowej został zapisany", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TreningSilaBiegowa.this,TreningUzupelniajacy.class);
                    startActivity(intent);
                    mDBHelper.close();
                    finish();



                }
                else {
                    Toast.makeText(getApplicationContext(),"Coś poszło nie tak. Spróbuj jeszcze raz.", Toast.LENGTH_LONG).show();
                }

            }});
    }
}
