package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class TreningElementySzybkosci extends AppCompatActivity {
    EditText mDataES;
    EditText mCzasTreninguES;
    EditText mDystansES;
    EditText mTrescTreninguES;
    String mRodzajES;
    Button mZapiszES;
    Integer ilosc_treningow;
    Integer id_treningu;
    Cursor wTreningi;
    Integer ostatni_id;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_elementy_szybkosci);
        getSupportActionBar().setTitle("Trening Elementy Szybkości");
        mDataES = (EditText)findViewById(R.id.Data_editText4);
        mCzasTreninguES = (EditText)findViewById(R.id.Czas_trwania_editText4);
        mDystansES = (EditText)findViewById(R.id.Dystans_editText4);
        mTrescTreninguES = (EditText)findViewById(R.id.Tresc_treningu_editText4);
        mZapiszES = (Button) findViewById(R.id.Zapisz_button4);

        mTrescTreninguES.setMovementMethod(new ScrollingMovementMethod());

        final DatabaseHelper mDBHelper = new DatabaseHelper(TreningElementySzybkosci.this);
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

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDataES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TreningElementySzybkosci.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String date;
                        if (month<10) {
                            if (day<10) {
                                date = year + "-0" + month + "-0" + day;
                            }
                            else {
                                date = year + "-0" + month + "-" + day;
                            }
                        }
                        else {
                            if(day<10) {
                                date = year + "-" + month + "-0" + day;
                            }
                            else {
                                date = year + "-" + month + "-" + day;
                            }
                        }
                        mDataES.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        mZapiszES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataES = mDataES.getText().toString();
                String czasES = mCzasTreninguES.getText().toString();
                String dystansES = mDystansES.getText().toString();
                String trescES = mTrescTreninguES.getText().toString();
                mRodzajES = "Elementy Szybkości";
                String rodzajES = "Elementy Szybkości";
                Boolean poprawnosc = TRUE;

                if(dataES.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić datę treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(czasES.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić czas treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(dystansES.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić dystans.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(trescES.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić treść treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(poprawnosc == TRUE) {
                    Float dystansESF = Float.parseFloat(dystansES);
                    mDBHelper.dodajTreningElementySzybkosciW(id_treningu,dataES,rodzajES,czasES,dystansESF,trescES);
                    Toast.makeText(getApplicationContext(),"Trening szybkości został zapisany", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TreningElementySzybkosci.this,TreningUzupelniajacy.class);
                    startActivity(intent);
                    mDBHelper.close();
                    finish();



                }
                else {
                    Toast.makeText(getApplicationContext(),"Coś poszło nie tak. Spróbuj jeszcze raz.", Toast.LENGTH_LONG).show();
                }

            }});
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,TreningUzupelniajacy.class);
        startActivity(intent);
        finish();
    }
}
