package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ZobaczZabiegiAktywnosc extends AppCompatActivity {
   TextView zabiegi;
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zobacz_zabiegi_aktywnosc);
    zabiegi = (TextView)findViewById(R.id.zabiegiTextView);
        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        System.out.println("\n NAzwa pola : "+nazwaPola);
        // dostep do bazy
        dbHelper= new BazaDanych(this);

        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola:"+nazwaPola);
        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola))+"ha");

        String t="Nawożenie:\n";
        int idPola=dbHelper.getIDbyName_tabelaPole(nazwaPola);
        t+= dbHelper.getNawozenieBy_IDpola(idPola);
        t+="\nOpryski:\n";
        t+=dbHelper.getOpryskiBy_IDpola(idPola);
        zabiegi.setText(t);


    }
}
