package pl.edu.uwr.runningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class MainActivity extends AppCompatActivity {

    private Button buttonTreningBiegowy;
    private Button buttonTreningUzupelniajacy;
    private Button buttonDziennik;

    public String table_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTreningBiegowy = (Button) findViewById(R.id.Rozpocznij_trening_button);
        buttonTreningUzupelniajacy = (Button) findViewById(R.id.Dodaj_trening_button);
        buttonDziennik = (Button)findViewById(R.id.Przeglądaj_dzienniczek_button);

        buttonTreningBiegowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //table_name = "Trening_biegowy";
                    rozpocznijTrening();
                    finish();
                }
                //finish();

            });

        buttonTreningUzupelniajacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dodajTrening();
                finish();

            }
        });

        buttonDziennik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otworzDziennik();
                finish();

            }
        });

    }
    public void rozpocznijTrening() {
        Intent intent = new Intent(this,TreningRejestrowany.class);
        //startActivityForResult();
        //intent.putExtra("Tabela",table_name);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String PackageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if(!pm.isIgnoringBatteryOptimizations(PackageName)){
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package: " + PackageName));
            }
        }
        startActivity(intent);

    }
    public void dodajTrening() {
        Intent intent = new Intent(this,TreningUzupelniajacy.class);
        startActivity(intent);

    }
    public void otworzDziennik() {
        Intent intent = new Intent(this,Dziennik.class);
        startActivity(intent);

    }


}
