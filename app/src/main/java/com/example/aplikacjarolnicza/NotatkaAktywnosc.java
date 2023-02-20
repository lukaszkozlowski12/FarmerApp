package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotatkaAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    EditText trescNotatki;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notatka_aktywnosc);

        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        // dostep do bazy
        dbHelper= new BazaDanych(this);
        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola:"+nazwaPola);
       // powierzchnia.setText("Powierzchnia: "+Float.toString(mDBHelper.getPowierzchniaByName(nazwaP))+" ha");
        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola))+"ha");
         trescNotatki= (EditText)findViewById(R.id.trescNotatkiEdit);



        ((Button)findViewById(R.id.btnDodajNotatke)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase(); // połaczenie z bazą
                int idPola= dbHelper.getIDbyName_tabelaPole(nazwaPola);// pobranie id pola
               // tworzenie mapy : nazwa-wartosc
                ContentValues val1 = new ContentValues();
                val1.put("id_pola",idPola); // do id_pola przypisano pobrane id pola
               // to pola tresc umieczono zawartosc pola tekstowego trescNotatki
                val1.put("tresc",trescNotatki.getText().toString());
                val1.put("id_user",1);
                // wprowadzenie aktualnej daty
                val1.put("data_wykonania",getDateTime());
                //wykonanie wprowadzenia danych do tabeli notatki
                db.insert("notatki",null,val1);

                db.close();// zamknięcie połączenia z bazą danych
                finish(); // zakończenie katywności
                // przekierowanie do innej aktywnosci
               Intent i = new Intent(getApplicationContext(),PoleAktywnosc.class);
               i.putExtra("nazwa",nazwaPola);// umieszczenie nazwy pola
                startActivity(i); // start aktywnosci

            }
        });



    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
