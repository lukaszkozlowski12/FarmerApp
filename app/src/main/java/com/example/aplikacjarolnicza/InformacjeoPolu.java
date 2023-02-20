package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InformacjeoPolu extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    TextView zabiegi,notatki,zagrozenia,nrewidencyjny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacjeo_polu);
        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        System.out.println("\n NAzwa pola : "+nazwaPola);
        // dostep do bazy
        dbHelper= new BazaDanych(this);

        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola: "+nazwaPola);
        // powierzchnia.setText("Powierzchnia: "+Float.toString(mDBHelper.getPowierzchniaByName(nazwaP))+" ha");
        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola))+"ha");
        ((TextView)findViewById(R.id.nrEwidencyjnytv)).setText("Nr działki: "+dbHelper.getNrEwidencyjnyByName(nazwaPola));
        zabiegi= (TextView)findViewById(R.id.zabiegiView);
        notatki= (TextView)findViewById(R.id.notatkiView);
        zagrozenia=(TextView)findViewById(R.id.zagrozeniaView);

         int idpola = dbHelper.getIDbyName_tabelaPole(nazwaPola);

         String tekst;
         tekst=dbHelper.getNawozenieBy_IDpola(idpola);
         tekst+=dbHelper.getOpryskiBy_IDpola(idpola);
         tekst+=dbHelper.getUprawaBy_IDpola(idpola);
         tekst+=dbHelper.getZbiorBy_IDpola(idpola)+"\n";
        System.out.println("\nrozmiar tekstu "+tekst.length());
         if(tekst.isEmpty() || tekst.length()<=1) {
         tekst="Nie zastosowano.";
         }zabiegi.setText(tekst);

         tekst=dbHelper.getZagrozenieBy_IDpola(idpola);
        if(tekst.equals("") || tekst.isEmpty()) {
            tekst="Brak.";
        }
         zagrozenia.setText(tekst);
         tekst= dbHelper.getNotatkaBy_IDpola(idpola);
        if(tekst.equals("") || tekst.isEmpty()) {
            tekst="Brak.";
        }
            notatki.setText(tekst);



      ((Button)findViewById(R.id.btnZobaczObszar)).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // tworzenie obiektu intencji do uruchomienia nowej aktywnosci
        Intent obszar = new Intent(getApplicationContext(),ZobaczObszar.class);
        // umieszczenie w oniekcie intencji nazwy pola
        obszar.putExtra(NAZWA_POLA,nazwaPola);
        startActivity(obszar);   // uruchomienie aktywnosci z mapą
        finish();// zakończenie poprzedniej
    }
});










    }
}
