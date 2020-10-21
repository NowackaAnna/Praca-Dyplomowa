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

public class TreningSprawnosciowy extends AppCompatActivity {
    EditText mDataSpr;
    EditText mCzasTreninguSpr;
    EditText mTrescTreninguSpr;
    String mRodzajSpr;
    Button mZapiszSpr;
    Integer ilosc_treningow;
    Integer id_treningu;
    Cursor wTreningi;
    Integer ostatni_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_sprawnosciowy);
        getSupportActionBar().setTitle("Trening Sprawnościowy");
        mDataSpr = (EditText)findViewById(R.id.Data_editText);
        mCzasTreninguSpr = (EditText)findViewById(R.id.Czas_trwania_editText);
        mTrescTreninguSpr = (EditText)findViewById(R.id.Tresc_treningu_editText);
        mZapiszSpr = (Button) findViewById(R.id.Zapisz_button);


        final DatabaseHelper mDBHelper = new DatabaseHelper(TreningSprawnosciowy.this);
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

        mZapiszSpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataSpr = mDataSpr.getText().toString();
                String czasSpr = mCzasTreninguSpr.getText().toString();
                String trescSpr = mTrescTreninguSpr.getText().toString();
                String rodzajSpr = "Sprawnościowy";
                Boolean poprawnosc = TRUE;

                if(dataSpr.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić datę treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(czasSpr.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić czas treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(trescSpr.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić treść treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(poprawnosc == TRUE) {

                    mDBHelper.dodajTreningSprawnosciowyW(id_treningu,dataSpr, rodzajSpr,czasSpr,trescSpr);
                    //ilosc_treningow = mDBHelper.countWszystkieTreningi("treningiwszystkie",null,null,null,null,null,null);
                    Toast.makeText(getApplicationContext(),"Trening sprawnościowy został zapisany", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),ilosc_treningow.toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TreningSprawnosciowy.this,TreningUzupelniajacy.class);
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
