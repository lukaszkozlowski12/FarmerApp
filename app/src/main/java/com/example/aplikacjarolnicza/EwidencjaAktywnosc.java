package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EwidencjaAktywnosc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ewidencja_aktywnosc);


        ((Button)findViewById(R.id.btnListaPol)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent listaPol = new Intent(getApplicationContext(),ListaPolAktywnosc.class);
                   startActivity(listaPol);



            }
        });

        ((Button)findViewById(R.id.btnZestawienieNawozenia)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent zestawienieNawozenia = new Intent(getApplicationContext(),ZestawienieNawozenieAktywnosc.class);
                startActivity(zestawienieNawozenia);
                finish();

            }
        });

        ((Button)findViewById(R.id.btnZestawieniePryskania)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent zestawieniePryskania= new Intent(getApplicationContext(),ZestawieniePryskanieAktywnosc.class);
                startActivity(zestawieniePryskania);
                finish();


            }
        });


        ((Button)findViewById(R.id.btnDzialkiNaMapie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent dzialki = new Intent(getApplicationContext(),DzialkiNaMapieAktywnosc.class);
                startActivity(dzialki);



            }
        });

        ((Button)findViewById(R.id.btnPlanAzotowy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gleba = new Intent(getApplicationContext(),SzacowanieGlebyAktywnosc.class);
                startActivity(gleba);
                finish();
            }
        });



    }
}
