package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class TreningSilowy extends AppCompatActivity {
    EditText mDataSily;
    EditText mCzasTreninguSily;
    EditText mObciazenieSily;
    EditText mTrescTreninguSily;
    String mRodzajSily;
    Button mZapiszSily;
    Integer ilosc_treningow;
    Integer id_treningu;
    Cursor wTreningi;
    Integer ostatni_id;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_silowy);
        getSupportActionBar().setTitle("Trening Siłowy");
        mDataSily = (EditText)findViewById(R.id.Data_editText2);
        mCzasTreninguSily = (EditText)findViewById(R.id.Czas_trwania_editText2);
        mObciazenieSily = (EditText)findViewById(R.id.Obciazenie_laczne_editText2);
        mTrescTreninguSily = (EditText)findViewById(R.id.Tresc_treningu_editText2);
        mZapiszSily = (Button) findViewById(R.id.Zapisz_button2);


        final DatabaseHelper mDBHelper = new DatabaseHelper(TreningSilowy.this);
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

        mDataSily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TreningSilowy.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String date;
                        if (month<10) {
                            if (day<10) {
                                date = "0"+day + "-0" + month + "-" + year;
                            }
                            else {
                                date = day + "-0" + month + "-" + year;
                            }
                        }
                        else {
                            if(day<10) {
                                date = "0"+day + "-" + month + "-" + year;
                            }
                            else {
                                date = day + "-" + month + "-" + year;
                            }
                        }
                        mDataSily.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        mZapiszSily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dataSily = mDataSily.getText().toString();
                String czasSily = mCzasTreninguSily.getText().toString();
                String obciazenieSily = mObciazenieSily.getText().toString();
                String trescSily = mTrescTreninguSily.getText().toString();
                String rodzajSila = "Siłowy";
                Boolean poprawnosc = TRUE;

                if(dataSily.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić datę treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(czasSily.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić czas treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(obciazenieSily.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić obciążenie łączne.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(trescSily.equals("")){
                    Toast.makeText(getApplicationContext(), "Proszę uzupełnić treść treningu.", Toast.LENGTH_LONG).show();
                    poprawnosc = FALSE;
                }
                if(poprawnosc == TRUE) {
                    Integer obciazenieSilyI = Integer.parseInt(obciazenieSily);
                    mDBHelper.dodajTreningSilowyW(id_treningu,dataSily,rodzajSila,czasSily,obciazenieSilyI,trescSily);
                    Toast.makeText(getApplicationContext(),"Trening siłowy został zapisany", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TreningSilowy.this,TreningUzupelniajacy.class);
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
