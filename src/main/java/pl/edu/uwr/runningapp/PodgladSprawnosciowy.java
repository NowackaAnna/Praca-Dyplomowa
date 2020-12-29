package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PodgladSprawnosciowy extends AppCompatActivity {
    TextView mCzasTreninguSpr;
    TextView mTrescTreninguSpr;
    EditText mKomentarzSpr;
    Button mDodajKomentarzSpr;
    Button mUsunSpr;

    String treningSpr;
    String rodzajSpr;
    String mDataTreninguSSpr;
    String  mCzasTreninguSSpr;
    String mCalkowityCzas;
    String mTrescTreninguSSpr;
    String mKomentarzSSpr;
    String mDodawanyKomentarzSpr;
    String mCaloscTresciSpr;
    Integer treningISpr;

    Cursor CaloscTrening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podglad_sprawnosciowy);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null)
        {
            treningSpr = (String) b.get("Nr treningu");
            rodzajSpr = (String) b.get("RodzajTr");

        }
        Log.i("dziala",treningSpr);

        mCzasTreninguSpr = (TextView)findViewById(R.id.Czas_podglad_textView_Spr);
        mTrescTreninguSpr = (TextView)findViewById(R.id.Tresc_treningu_textView_Spr);
        mKomentarzSpr = (EditText) findViewById(R.id.Komentarz_podglad_editText_Spr);
        mDodajKomentarzSpr = (Button) findViewById(R.id.Dodaj_komentarz_button_Spr);
        mUsunSpr = (Button) findViewById(R.id.Usun_trening_button_Spr);

        mTrescTreninguSpr.setMovementMethod(new ScrollingMovementMethod());
        mKomentarzSpr.setMovementMethod(new ScrollingMovementMethod());

        final DatabaseHelper mDBHelper = new DatabaseHelper(PodgladSprawnosciowy.this);
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

        CaloscTrening = mDBHelper.queryPodgladTreningu(treningSpr);

        if (CaloscTrening.moveToFirst()) {
            do {
                mDataTreninguSSpr = CaloscTrening.getString(1);
                mCzasTreninguSSpr = CaloscTrening.getString(4);
                mKomentarzSSpr = CaloscTrening.getString(5);
                mTrescTreninguSSpr = CaloscTrening.getString(9);
            } while (CaloscTrening.moveToNext());
        }
        getSupportActionBar().setTitle(mDataTreninguSSpr + ": Sprawność");

        mCalkowityCzas = "Czas:\n" + mCzasTreninguSSpr+" min";
        mCzasTreninguSpr.setText(mCalkowityCzas);

        if(mKomentarzSSpr.equals("")){
            mCaloscTresciSpr = mTrescTreninguSSpr;
        }
        else {
            mCaloscTresciSpr = mTrescTreninguSSpr+ "\n" + "Komentarz: \n"+mKomentarzSSpr + "\n";
        }

        mTrescTreninguSpr.setText(mCaloscTresciSpr);

        mDodajKomentarzSpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDodawanyKomentarzSpr = mKomentarzSpr.getText().toString();
                treningISpr = Integer.parseInt(treningSpr);
                mDBHelper.updateKomentarz(mDodawanyKomentarzSpr,treningISpr);
                Toast.makeText(getApplicationContext(), "Komentarz został dodany.", Toast.LENGTH_LONG).show();
            }});
        mUsunSpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PodgladSprawnosciowy.this);
                builder.setTitle("Ostrzeżenie");
                builder.setMessage("Czy na pewno chcesz usunąć ten trening?");
                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.deleteTrening(treningSpr);
                        Intent intent = new Intent(PodgladSprawnosciowy.this,Dziennik.class);
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
