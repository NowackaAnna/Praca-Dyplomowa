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

public class PodgladSilowy extends AppCompatActivity {
    TextView mRodzajTreninguSilowy;
    TextView mDataTreninguSilowy;
    TextView mObciazenieTreninguSilowy;
    TextView mCzasTreninguSilowy;
    TextView mTrescTreninguSilowy;
    EditText mKomentarzSilowy;
    Button mDodajKomentarzSilowy;
    Button mUsunSilowy;

    String treningSilowy;
    String rodzajSilowy;
    String mDataTreninguSSilowy;
    String mObciazenieTreninguSSilowy;
    Integer mObciazenieTreninguISilowy;
    String  mCzasTreninguSSilowy;
    String mCalkowityCzasS;
    String mTrescTreninguSSilowy;
    String mKomentarzSSilowy;
    String mDodawanyKomentarzSilowy;
    String mCaloscTresciSilowy;
    String mObciazenieSilowy;
    Integer treningISilowy;

    Cursor CaloscTrening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podglad_silowy);
        getSupportActionBar().setTitle("Trening");

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null)
        {
            treningSilowy =(String) b.get("Nr treningu");
            rodzajSilowy = (String) b.get("RodzajTr");

        }

        mRodzajTreninguSilowy = (TextView)findViewById(R.id.Rodzaj_podglad_textView_Silowy);
        mDataTreninguSilowy = (TextView)findViewById(R.id.Data_podglad_textView_Silowy);
        mObciazenieTreninguSilowy = (TextView)findViewById(R.id.Obciazenie_podglad_textView_Silowy);
        mCzasTreninguSilowy = (TextView)findViewById(R.id.Czas_podglad_textView_Silowy);
        mTrescTreninguSilowy = (TextView)findViewById(R.id.Tresc_treningu_textView_Silowy);
        mKomentarzSilowy = (EditText) findViewById(R.id.Komentarz_podglad_editText_Silowy);
        mDodajKomentarzSilowy = (Button) findViewById(R.id.Dodaj_komentarz_button_Silowy);
        mUsunSilowy = (Button) findViewById(R.id.Usun_trening_button_Silowy);

        mTrescTreninguSilowy.setMovementMethod(new ScrollingMovementMethod());
        mKomentarzSilowy.setMovementMethod(new ScrollingMovementMethod());

        final DatabaseHelper mDBHelper = new DatabaseHelper(PodgladSilowy.this);
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

        CaloscTrening = mDBHelper.queryPodgladTreningu(treningSilowy);
        if (CaloscTrening.moveToFirst()) {
            do {
                mDataTreninguSSilowy = CaloscTrening.getString(1);
                mCzasTreninguSSilowy = CaloscTrening.getString(4);
                mKomentarzSSilowy = CaloscTrening.getString(5);
                mObciazenieTreninguISilowy = CaloscTrening.getInt(8);
                mObciazenieTreninguSSilowy = mObciazenieTreninguISilowy.toString();
                mTrescTreninguSSilowy = CaloscTrening.getString(9);
            } while (CaloscTrening.moveToNext());
        }
        mRodzajTreninguSilowy.setText(rodzajSilowy);
        mDataTreninguSilowy.setText(mDataTreninguSSilowy);
        mObciazenieSilowy = "Obciążenie: "+ mObciazenieTreninguSilowy;
        mObciazenieTreninguSilowy.setText(mObciazenieSilowy);
        mCalkowityCzasS = "Czas: " + mCzasTreninguSSilowy;
        mCzasTreninguSilowy.setText(mCalkowityCzasS);
        if(mKomentarzSSilowy.equals("")){
            mCaloscTresciSilowy = mTrescTreninguSSilowy;
        }
        else {
            mCaloscTresciSilowy = mTrescTreninguSSilowy+ "\n" + "Komentarz: \n"+mKomentarzSSilowy + "\n";
        }

        mTrescTreninguSilowy.setText(mCaloscTresciSilowy);

        mDodajKomentarzSilowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDodawanyKomentarzSilowy = mKomentarzSilowy.getText().toString();
                treningISilowy = Integer.parseInt(treningSilowy);
                mDBHelper.updateKomentarz(mDodawanyKomentarzSilowy,treningISilowy);
                Toast.makeText(getApplicationContext(), "Komentarz został dodany.", Toast.LENGTH_LONG).show();
            }});
        mUsunSilowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PodgladSilowy.this);
                builder.setTitle("Ostrzeżenie");
                builder.setMessage("Czy na pewno chcesz usunąć ten trening?");
                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.deleteTrening(treningSilowy);
                        Intent intent = new Intent(PodgladSilowy.this,Dziennik.class);
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
