package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ListaPolAktywnosc extends AppCompatActivity {
    ArrayList<String> listaPol= new ArrayList<String>();
    TextView liczbaPol;
    private BazaDanych mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_pol_aktywnosc);


        mDBHelper = new BazaDanych(this);
        liczbaPol= ((TextView)findViewById(R.id.liczbaPolView));
        int liczba= mDBHelper.LiczbaPol();

        liczbaPol.setText(Integer.toString(liczba));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

            System.out.println("\nO   COOO CHODZI sprawdzenie czy jest blad tj czy sa all uprawnienia ");
            return;
        }





    }




    public ArrayList<String> odczytajNazwy(){

        //  ArrayList<String> s = new ArrayList<String>();
        try {


            //  SQLiteOpenHelper dbHelper;
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
                System.out.println("\n----Nazwy pol==== "+name);






            } cursor.close();
            System.out.println("\nPola: "+listaPol);

            db.close();





        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(),"EXCEPTION:SET",
                    Toast.LENGTH_LONG).show();
        }






        return listaPol; }
}
