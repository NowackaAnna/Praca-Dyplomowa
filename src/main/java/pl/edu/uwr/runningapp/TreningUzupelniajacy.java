package pl.edu.uwr.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TreningUzupelniajacy extends AppCompatActivity {

    private Button buttonTreningSprawnosciowy;
    private Button buttonTreningSilowy;
    private Button buttonTreningSilaBiegowa;
    private Button buttonTreningElementySzybkosci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_uzupelniajacy);

        buttonTreningSprawnosciowy = (Button) findViewById(R.id.trening_sprawnosciowy_button);
        buttonTreningSilowy = (Button) findViewById(R.id.trening_silowy_button);
        buttonTreningSilaBiegowa = (Button)findViewById(R.id.trening_sila_biegowa_button);
        buttonTreningElementySzybkosci = (Button)findViewById(R.id.elementy_szybkosci_button);

        buttonTreningSprawnosciowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dodajTreningSprawnosciowy();
                //finish();

            }
        });
        buttonTreningSilowy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dodajTreningSilowy();
                //finish();

            }
        });
        buttonTreningSilaBiegowa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dodajTreningSilaBiegowa();
                //finish();

            }
        });
        buttonTreningElementySzybkosci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dodajTreningElementySzybkosci();
                //finish();

            }
        });
    }

    public void dodajTreningSprawnosciowy() {
        Intent intent = new Intent(this,TreningSprawnosciowy.class);
        startActivity(intent);
    }
    public void dodajTreningSilowy() {
        Intent intent = new Intent(this,TreningSilowy.class);
        startActivity(intent);
    }
    public void dodajTreningSilaBiegowa() {
        Intent intent = new Intent(this,TreningSilaBiegowa.class);
        startActivity(intent);
    }
    public void dodajTreningElementySzybkosci() {
        Intent intent = new Intent(this,TreningElementySzybkosci.class);
        startActivity(intent);
    }
}
