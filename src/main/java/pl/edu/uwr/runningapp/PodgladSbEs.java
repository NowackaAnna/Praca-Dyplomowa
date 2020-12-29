package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PodgladSbEs extends AppCompatActivity {

    TextView mDystansTreninguSE;
    TextView mCzasTreninguSE;
    TextView mTrescTreninguSE;
    EditText mKomentarzSE;
    Button mDodajKomentarzSE;
    Button mUsunSE;

    String treningSE;
    String rodzajSE;
    String mDataTreninguSSE;
    String mDystansTreninguSSE;
    Float mDystansTreninguFSE;
    String  mCzasTreninguSSE;
    String mCalkowityCzas;
    String mTrescTreninguSSE;
    String mKomentarzSSE;
    String mDodawanyKomentarzSE;
    String mCaloscTresciSE;
    Integer treningISE;

    Cursor CaloscTrening;
    Cursor CaloscTreningu2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podglad_sb_es);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null)
        {
            treningSE =(String) b.get("Nr treningu");
            rodzajSE = (String) b.get("RodzajTr");

        }

        mDystansTreninguSE = (TextView)findViewById(R.id.Dystans_podglad_textViewSE);
        mCzasTreninguSE = (TextView)findViewById(R.id.Czas_podglad_textViewSE);
        mTrescTreninguSE = (TextView)findViewById(R.id.Tresc_treningu_textViewSE);
        mKomentarzSE = (EditText) findViewById(R.id.Komentarz_podglad_editTextSE);
        mDodajKomentarzSE = (Button) findViewById(R.id.Dodaj_komentarz_buttonSE);
        mUsunSE = (Button) findViewById(R.id.Usun_trening_buttonSE);

        mTrescTreninguSE.setMovementMethod(new ScrollingMovementMethod());
        mKomentarzSE.setMovementMethod(new ScrollingMovementMethod());

        final DatabaseHelper mDBHelper = new DatabaseHelper(PodgladSbEs.this);
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

        CaloscTrening = mDBHelper.queryPodgladTreningu(treningSE);
        if (CaloscTrening.moveToFirst()) {
            do {
                mDataTreninguSSE = CaloscTrening.getString(1);
                mDystansTreninguFSE = CaloscTrening.getFloat(3);
                mDystansTreninguSSE = mDystansTreninguFSE.toString();
                mCzasTreninguSSE = CaloscTrening.getString(4);
                mKomentarzSSE = CaloscTrening.getString(5);
                mTrescTreninguSSE = CaloscTrening.getString(9);
            } while (CaloscTrening.moveToNext());
        }
        if(rodzajSE.equals("Elementy Szybkości")){
            getSupportActionBar().setTitle(mDataTreninguSSE+": Szybkość");
        }
        else {
            getSupportActionBar().setTitle(mDataTreninguSSE + ": S.Biegowa");
        }

        mDystansTreninguSE.setText("Dystans:\n" + mDystansTreninguSSE + " m");
        mCalkowityCzas = "Czas:\n" + mCzasTreninguSSE + " min";
        mCzasTreninguSE.setText(mCalkowityCzas);
        if(mKomentarzSSE.equals("")){
            mCaloscTresciSE = mTrescTreninguSSE;
        }
        else{
            mCaloscTresciSE = mTrescTreninguSSE+ "\n" + "Komentarz: \n"+mKomentarzSSE + "\n";
        }

        mTrescTreninguSE.setText(mCaloscTresciSE);

        mDodajKomentarzSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDodawanyKomentarzSE = mKomentarzSE.getText().toString();
                if (mDodawanyKomentarzSE.equals("")){
                    Toast.makeText(getApplicationContext(), "Dodawany komentarz nie może być pusty.", Toast.LENGTH_LONG).show();
                }
                else {
                    mKomentarzSSE = mKomentarzSSE + "\n" + mDodawanyKomentarzSE;
                    treningISE = Integer.parseInt(treningSE);
                    mDBHelper.updateKomentarz(mKomentarzSSE, treningISE);
                    Toast.makeText(getApplicationContext(), "Komentarz został dodany.", Toast.LENGTH_LONG).show();
                    CaloscTreningu2 = mDBHelper.queryPodgladTreningu(treningSE);
                    if (CaloscTreningu2.moveToFirst()) {
                        do {
                            mKomentarzSSE = CaloscTreningu2.getString(5);
                            mTrescTreninguSSE = CaloscTreningu2.getString(9);
                        } while (CaloscTreningu2.moveToNext());
                    }
                    mCaloscTresciSE = mTrescTreninguSSE+ "\n" + "Komentarz:"+mKomentarzSSE;
                    mTrescTreninguSE.setText(mCaloscTresciSE);
                    mKomentarzSE.setText("");
                }
            }});
        mUsunSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PodgladSbEs.this);
                builder.setTitle("Ostrzeżenie");
                builder.setMessage("Czy na pewno chcesz usunąć ten trening?");
                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.deleteTrening(treningSE);
                        Intent intent = new Intent(PodgladSbEs.this,Dziennik.class);
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
