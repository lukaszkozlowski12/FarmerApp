package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


          // Obsługa kliknięcia w przycisk  OBSLUGA POLA
        ((Button)findViewById(R.id.btnObsluga)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent obslugaPola = new Intent(getApplicationContext(), ObslugaAktywnosc.class);
               startActivity(obslugaPola);
            }  });
         // Obsługa kliknięcia w przycisk Ewidencja gospodarstwa
        ((Button)findViewById(R.id.btnEwidencja)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // tworzenie obiektu intencji, który otworzy aktywność ewidencji
               Intent ewidencja = new Intent(getApplicationContext(),EwidencjaAktywnosc.class);
               startActivity(ewidencja); // wystartowanie aktywności
            } });

    }
}
