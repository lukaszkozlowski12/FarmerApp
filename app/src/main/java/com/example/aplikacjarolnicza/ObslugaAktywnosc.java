package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ObslugaAktywnosc extends AppCompatActivity {
ArrayList<String> listaPol= new ArrayList<String>();
TextView liczbaPol;
    private BazaDanych mDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obsluga_aktywnosc);
        mDBHelper = new BazaDanych(this);
       liczbaPol= ((TextView)findViewById(R.id.liczbapolTextView));
       int liczba= mDBHelper.LiczbaPol();
        liczbaPol.setText(Integer.toString(liczba));


        // oprogramowanie klikania w przycisk
        ((Button)findViewById(R.id.btnDodajPole)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   // utworzenie intencji , która kieruje na aktywność tworzenia pola
                Intent intent = new Intent(getApplicationContext(), DodawaniePola.class);
                startActivity(intent);

            }
        });
    }


    public ArrayList<String> odczytajNazwy(){


        try {



            final SQLiteOpenHelper dbHelper = new BazaDanych(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nazwa"};

           // String warunek = BazaDanych.KOLEJNOSC + " = ?";
           // String[] nazwaWarunku = {"1"};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                  "pole",
                    kolumny,
                    null,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                double szer= cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LAT));
                double dl = cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LNG)) ;
          String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
              listaPol.add(name);

            } cursor.close();
            System.out.println("\nPola: "+listaPol);

            db.close();


        } catch (SQLException e) {
            Toast.makeText(ObslugaAktywnosc.this,"Exception",
                    Toast.LENGTH_LONG).show();
        }






   return listaPol; }


}
