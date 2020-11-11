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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class PodgladTrening extends AppCompatActivity {

    TextView mRodzajTreningu;
    TextView mDataTreningu;
    TextView mDystansTreningu;
    TextView mCzasTreningu;
    TextView mTrescTreningu;
    EditText mKomentarz;
    Button mDodajKomentarz;
    Button mUsun;

    String trening;
    String rodzaj;
    String mDataTreninguS;
    String mDystansTreninguS;
    Float mDystansTreninguF;
    String mObciazenieTreninguS;
    Integer mObciazenieTreninguI;
    String  mCzasTreninguS;
    String mTrescTreninguS;
    String mKomentarzS;
    String mPoszczegolneOdcinki;
    String mSrednieTempo;
    String mDodawanyKomentarz;
    String mCaloscTresci;
    String mObciazenie;
    Integer treningI;

    Cursor CaloscTrening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podglad_trening);
        getSupportActionBar().setTitle("Trening");

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null)
        {
            trening =(String) b.get("Nr treningu");
            rodzaj = (String) b.get("RodzajTr");

        }

        mRodzajTreningu = (TextView)findViewById(R.id.Rodzaj_podglad_textView);
        mDataTreningu = (TextView)findViewById(R.id.Data_podglad_textView);
        mDystansTreningu = (TextView)findViewById(R.id.Dystans_podglad_textView);
        mCzasTreningu = (TextView)findViewById(R.id.Czas_podglad_textView);
        mTrescTreningu = (TextView)findViewById(R.id.Tresc_treningu_textView);
        mKomentarz = (EditText) findViewById(R.id.Komentarz_podglad_editText);
        mDodajKomentarz = (Button) findViewById(R.id.Dodaj_komentarz_button);
        mUsun = (Button) findViewById(R.id.Usun_trening_button);

        mTrescTreningu.setMovementMethod(new ScrollingMovementMethod());


        final DatabaseHelper mDBHelper = new DatabaseHelper(PodgladTrening.this);
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

        CaloscTrening = mDBHelper.queryPodgladTreningu(trening);

        if(rodzaj.equals("Bieganie")){
            if (CaloscTrening.moveToFirst()) {
                do {
                    mDataTreninguS = CaloscTrening.getString(1);
                    mDystansTreninguF = CaloscTrening.getFloat(3);
                    mDystansTreninguS = mDystansTreninguF.toString();
                    mCzasTreninguS = CaloscTrening.getString(4);
                    mKomentarzS = CaloscTrening.getString(5);
                    mPoszczegolneOdcinki = CaloscTrening.getString(6);
                    mSrednieTempo = CaloscTrening.getString(7);
                    mTrescTreninguS = CaloscTrening.getString(9);
                } while (CaloscTrening.moveToNext());
            }
            mRodzajTreningu.setText(rodzaj);
            mDataTreningu.setText(mDataTreninguS);
            mDystansTreningu.setText(mDystansTreninguS);
            mCzasTreningu.setText(mCzasTreninguS);
            mCaloscTresci ="Poszczególne odcinki: \n" + mPoszczegolneOdcinki + "\n" + "Średnie tempo: "+mSrednieTempo + "\n" + "Komentarz: \n"+mKomentarzS + "\n";
            mTrescTreningu.setText(mCaloscTresci);


        }
        if(rodzaj.equals("Sprawnościowy")){
            if (CaloscTrening.moveToFirst()) {
                do {
                    mDataTreninguS = CaloscTrening.getString(1);
                    mCzasTreninguS = CaloscTrening.getString(4);
                    mKomentarzS = CaloscTrening.getString(5);
                    mTrescTreninguS = CaloscTrening.getString(9);
                } while (CaloscTrening.moveToNext());
            }
            mRodzajTreningu.setText(rodzaj);
            mDataTreningu.setText(mDataTreninguS);
            mDystansTreningu.setText("");
            mCzasTreningu.setText(mCzasTreninguS);
            mCaloscTresci = mTrescTreninguS + "\n" + "Komentarz: \n"+mKomentarzS + "\n";
            mTrescTreningu.setText(mCaloscTresci);


        }
        if(rodzaj.equals("Siłowy")){
            if (CaloscTrening.moveToFirst()) {
                do {
                    mDataTreninguS = CaloscTrening.getString(1);
                    mCzasTreninguS = CaloscTrening.getString(4);
                    mKomentarzS = CaloscTrening.getString(5);
                    mObciazenieTreninguI = CaloscTrening.getInt(8);
                    mObciazenieTreninguS = mObciazenieTreninguI.toString();
                    mTrescTreninguS = CaloscTrening.getString(9);
                } while (CaloscTrening.moveToNext());
            }
            mRodzajTreningu.setText(rodzaj);
            mDataTreningu.setText(mDataTreninguS);
            mObciazenie = "Obciążenie: "+ mObciazenieTreninguS;
            mDystansTreningu.setText(mObciazenie);
            mCzasTreningu.setText(mCzasTreninguS);
            mCaloscTresci = mTrescTreninguS + "\n"  + "Komentarz: \n"+mKomentarzS + "\n";
            mTrescTreningu.setText(mCaloscTresci);


        }
        if(rodzaj.equals("Siła Biegowa")){
            if (CaloscTrening.moveToFirst()) {
                do {
                    mDataTreninguS = CaloscTrening.getString(1);
                    mDystansTreninguF = CaloscTrening.getFloat(3);
                    mDystansTreninguS = mDystansTreninguF.toString();
                    mCzasTreninguS = CaloscTrening.getString(4);
                    mKomentarzS = CaloscTrening.getString(5);
                    mTrescTreninguS = CaloscTrening.getString(9);
                } while (CaloscTrening.moveToNext());
            }
            mRodzajTreningu.setText(rodzaj);
            mDataTreningu.setText(mDataTreninguS);
            mDystansTreningu.setText(mDystansTreninguS);
            mCzasTreningu.setText(mCzasTreninguS);
            mCaloscTresci = mTrescTreninguS + "\n" + "Komentarz: \n"+mKomentarzS + "\n";
            mTrescTreningu.setText(mCaloscTresci);


        }
        if(rodzaj.equals("Elementy Szybkości")){
            if (CaloscTrening.moveToFirst()) {
                do {
                    mDataTreninguS = CaloscTrening.getString(1);
                    mDystansTreninguF = CaloscTrening.getFloat(3);
                    mDystansTreninguS = mDystansTreninguF.toString();
                    mCzasTreninguS = CaloscTrening.getString(4);
                    mKomentarzS = CaloscTrening.getString(5);
                    mTrescTreninguS = CaloscTrening.getString(9);
                } while (CaloscTrening.moveToNext());
            }
            mRodzajTreningu.setText(rodzaj);
            mDataTreningu.setText(mDataTreninguS);
            mDystansTreningu.setText(mDystansTreninguS);
            mCzasTreningu.setText(mCzasTreninguS);
            mCaloscTresci = mTrescTreninguS + "\n" + "Komentarz: \n"+mKomentarzS + "\n";
            mTrescTreningu.setText(mCaloscTresci);


        }

        mDodajKomentarz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDodawanyKomentarz = mKomentarz.getText().toString();
                treningI = Integer.parseInt(trening);
                mDBHelper.updateKomentarz(mDodawanyKomentarz,treningI);
                Toast.makeText(getApplicationContext(), "Komentarz został dodany.", Toast.LENGTH_LONG).show();
            }});
        mUsun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PodgladTrening.this);
                builder.setTitle("Ostrzeżenie");
                builder.setMessage("Czy na pewno chcesz usunąć ten trening?");
                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.deleteTrening(trening);
                        Intent intent = new Intent(PodgladTrening.this,Dziennik.class);
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
}
