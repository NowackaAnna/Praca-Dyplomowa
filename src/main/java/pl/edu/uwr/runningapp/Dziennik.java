package pl.edu.uwr.runningapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Dziennik extends AppCompatActivity {

    ListView listView;
    String mRodzaje[];
    String mDaty[];
    String mDystanse[];
    String mCzasy[];
    Integer mIndeksy[];
    Integer ilosc_treningowU;
    Integer ilosc_treningowB;
    Integer ilosc_treningow;
    Integer pozycja_w_tab;
    int images[];
    Cursor TreningiUzupelniajace;
    Cursor WszystkieTreningi;
    Float fDystans;
    String tDystans;
    String indeksPodglad;
    String rodzajPodglad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dziennik);
        getSupportActionBar().setTitle("Dziennik Treningowy");
        final DatabaseHelper mDBHelper = new DatabaseHelper(Dziennik.this);
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
        //Toast.makeText(getApplicationContext(),ilosc_treningow.toString(), Toast.LENGTH_LONG).show();
        if(ilosc_treningow == 0){
            mRodzaje = new String[1];
            mDaty = new String[1];
            mDystanse = new String[1];
            mCzasy = new String[1];
            mIndeksy = new Integer[1];
            images = new int[1];

            mRodzaje[0] = "Brak zapisanych treningów";
            mDaty[0] = "0";
            mDystanse[0] = "";
            mCzasy[0]="0";
            //mIndeksy[0] = -11001;
            images[0] = R.drawable.emptyp;

        }
        else {
            mRodzaje = new String[ilosc_treningow];
            mDaty = new String[ilosc_treningow];
            mDystanse = new String[ilosc_treningow];
            mCzasy = new String[ilosc_treningow];
            mIndeksy = new Integer[ilosc_treningow];
            images = new int[ilosc_treningow];

            pozycja_w_tab = 0;


            //WszystkieTreningi = mDBHelper.queryWszystkieTreningii("treningiwszystkie", null, null, null, null, null, null);
            WszystkieTreningi = mDBHelper.queryWszystkieTreningi2();
            if (WszystkieTreningi.moveToFirst()) {
                do {
                    mIndeksy[pozycja_w_tab] = WszystkieTreningi.getInt(0);
                    mDaty[pozycja_w_tab] = WszystkieTreningi.getString(1);
                    mRodzaje[pozycja_w_tab] = WszystkieTreningi.getString(2);
                    fDystans = WszystkieTreningi.getFloat(3);
                    tDystans = fDystans.toString();
                    mDystanse[pozycja_w_tab] = tDystans;
                    mCzasy[pozycja_w_tab] = WszystkieTreningi.getString(4);
                    pozycja_w_tab = pozycja_w_tab + 1;
                } while (WszystkieTreningi.moveToNext());
            }
            for (int i = 0; i < ilosc_treningow; i++) {
                if (mRodzaje[i].equals("Sprawnościowy")) {
                    images[i] = R.drawable.sprawnoscp;
                }
                if (mRodzaje[i].equals("Siłowy")) {
                    images[i] = R.drawable.silowaniap;
                }
                if (mRodzaje[i].equals("Siła Biegowa")) {
                    images[i] = R.drawable.silabiegowap;
                }
                if (mRodzaje[i].equals("Elementy Szybkości")) {
                    images[i] = R.drawable.szybkoscp;
                }
                if (mRodzaje[i].equals("Bieganie")){
                    images[i] = R.drawable.bieganiep;
                }
            }
            Log.i("dziala", mRodzaje[0].toString());
        }

        //Toast.makeText(getApplicationContext(),mRodzaje[0].toString(), Toast.LENGTH_LONG).show();

        listView = findViewById(R.id.listView);
        DziennikAdapter adapter = new DziennikAdapter(this, mRodzaje,mDaty,mDystanse,mCzasy, images);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                indeksPodglad = mIndeksy[i].toString();
                rodzajPodglad = mRodzaje[i];

                if(rodzajPodglad.equals("Sprawnościowy")){
                    Log.i("dziala",rodzajPodglad);
                    pokazTreningSprawnosciowy();
                }
                if(rodzajPodglad.equals("Siłowy")){
                    pokazTreningSilowy();
                }
                if(rodzajPodglad.equals("Siła Biegowa")){
                    pokazTreningSE();
                }
                if(rodzajPodglad.equals("Elementy Szybkości")){
                    pokazTreningSE();
                }
                if(rodzajPodglad.equals("Bieganie")){
                    pokazTreningBiegowy();
                }
                mDBHelper.close();
                finish();


            }
        });
    }

    class DziennikAdapter extends ArrayAdapter<String>{
        Context context;
        String rRodzaje[];
        String rDaty[];
        String rDystanse[];
        String rCzasy[];
        int rImages[];

        DziennikAdapter(Context c, String rodzaje[], String daty[], String dystanse[], String czasy[], int imgs[]){
            super(c, R.layout.row_trening, R.id.textViewRodzaj, rodzaje);
            this.rRodzaje = rodzaje;
            this.rDaty = daty;
            this.rDystanse = dystanse;
            this.rCzasy = czasy;
            this.rImages = imgs;

        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_trening,parent,false);
            ImageView image = row.findViewById(R.id.image);
            TextView mRodzajTreningu = row.findViewById(R.id.textViewRodzaj);
            TextView mDataTreningu = row.findViewById(R.id.textViewData);
            TextView mDystansTreningu = row.findViewById(R.id.textViewDystans);
            TextView mCzasTreningu = row.findViewById(R.id.textViewCzas);

            image.setImageResource(rImages[position]);
            mRodzajTreningu.setText(rRodzaje[position]);
            mDataTreningu.setText(rDaty[position]);
            mDystansTreningu.setText(rDystanse[position]);
            mCzasTreningu.setText("Czas: "+rCzasy[position]);

            return row;

        }

    }
    public void pokazTrening() {
        Intent intent = new Intent(this,PodgladSprawnosciowy.class);
        intent.putExtra("Nr treningu",indeksPodglad);
        intent.putExtra("RodzajTr",rodzajPodglad);
        startActivity(intent);
    }
    public void pokazTreningBiegowy() {
        Intent intent = new Intent(this,PodgladBiegowy.class);
        intent.putExtra("Nr treningu",indeksPodglad);
        intent.putExtra("RodzajTr",rodzajPodglad);
        startActivity(intent);
    }
    public void pokazTreningSprawnosciowy() {
        Intent intent = new Intent(this, PodgladSprawnosciowy.class);
        intent.putExtra("Nr treningu",indeksPodglad);
        intent.putExtra("RodzajTr",rodzajPodglad);
        startActivity(intent);
    }
    public void pokazTreningSilowy() {
        Intent intent = new Intent(Dziennik.this, PodgladSilowy.class);
        intent.putExtra("Nr treningu",indeksPodglad);
        intent.putExtra("RodzajTr",rodzajPodglad);
        startActivity(intent);
    }
    public void pokazTreningSE() {
        Intent intent = new Intent(this,PodgladSbEs.class);
        intent.putExtra("Nr treningu",indeksPodglad);
        intent.putExtra("RodzajTr",rodzajPodglad);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
