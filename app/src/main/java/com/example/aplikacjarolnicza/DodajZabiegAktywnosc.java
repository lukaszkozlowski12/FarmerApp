package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DodajZabiegAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_zabieg_aktywnosc);
        final String nazwaP= getIntent().getStringExtra(NAZWA_POLA);
        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola: "+nazwaP);



        ((Button)findViewById(R.id.btnNawozenie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();// zakończenie bieżącej aktywności
                // tworzenei obiektu intencji, przejscie do tworzenia nawożenia
                Intent nawozenie = new Intent(DodajZabiegAktywnosc.this,NawozenieAktywnosc.class);
               // umieszczenei w obiekcie intencji nazwy pola do którego będą dodawane zabiegi
                nawozenie.putExtra(NAZWA_POLA,nazwaP);
               // start aktywności
                 startActivity(nawozenie);
            }
        });


        // PRYSKANIE przycisk obsługa
        ((Button)findViewById(R.id.btnPryskanie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // zakończenie bieżącej aktywności
                // tworzenei obiektu intencji, przejscie do tworzenia nawożenia
                Intent pryskanie = new Intent(DodajZabiegAktywnosc.this,
                        PryskanieAktywnosc.class);
                // umieszczenei w obiekcie intencji nazwy pola
                pryskanie.putExtra(NAZWA_POLA,nazwaP);
                // start aktywności
                startActivity(pryskanie);

            }
        });

        ((Button)findViewById(R.id.btnZbior)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
                Intent zbior = new Intent(getApplicationContext(),ZbiorAktywnosc.class);
                zbior.putExtra(NAZWA_POLA,nazwaP);
                startActivity(zbior);


            }
        });

((Button)findViewById(R.id.btnUprawa)).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        finish();
        Intent uprawa = new Intent(getApplicationContext(),UprawaAktywnosc.class);

        uprawa.putExtra(NAZWA_POLA,nazwaP);
        startActivity(uprawa);




    }
});


    }
}
