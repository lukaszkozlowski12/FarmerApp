package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PoleAktywnosc extends AppCompatActivity {
 public static final  String NAZWA_POLA="nazwaPola";

    TextView powierzchnia;
    TextView nazwaPola;
    Button btnDodajZabieg, btnDodajNotatke, btnZobaczZabigi,btnDodajZagrozenie,btnInfoPole;
    private BazaDanych mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pole_aktywnosc);
        Toolbar toolbar = findViewById(R.id.toolbar);
       //setSupportActionBar(toolbar);


    powierzchnia= (TextView)findViewById(R.id.powierzchniaView);
    nazwaPola= (TextView)findViewById(R.id.nazwaPolaView);
    btnDodajZabieg= (Button)findViewById(R.id.btnZabieg);
    btnDodajNotatke=(Button)findViewById(R.id.btnDodajNotatke);
    btnDodajZagrozenie=(Button)findViewById(R.id.btnDodajZagrozenie);
    btnInfoPole=(Button)findViewById(R.id.brnInfoPole);
    btnZobaczZabigi=(Button)findViewById(R.id.btnZobaczZabieg);
        mDBHelper = new BazaDanych(this);

        // pobranie nazwy pola z intencji
        final String nazwaP= getIntent().getStringExtra("nazwa");


    powierzchnia.setText("Powierzchnia: "+Float.toString(mDBHelper.getPowierzchniaByName(nazwaP))+" ha");
    nazwaPola.setText(nazwaP);


    btnDodajZabieg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent zabieg = new Intent(PoleAktywnosc.this,DodajZabiegAktywnosc.class);
            zabieg.putExtra(NAZWA_POLA,nazwaP);
            startActivity(zabieg);




        }
    });

        // notatka
        ((Button)findViewById(R.id.btnDodajNotatke)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent notatka = new Intent(PoleAktywnosc.this,NotatkaAktywnosc.class);
                notatka.putExtra(NAZWA_POLA,nazwaP);
                startActivity(notatka);

            }
        });

        // zagrozenie

        ((Button)findViewById(R.id.btnDodajZagrozenie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent zagrozenie = new Intent(getApplicationContext(),ZagrozenieAktywnosc.class);
                zagrozenie.putExtra(NAZWA_POLA,nazwaP);
                startActivity(zagrozenie);


            }
        });

        //zobacz zabiegi
        ((Button)findViewById(R.id.btnZobaczZabieg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zobaczZabieg = new Intent(getApplicationContext(),ZobaczZabiegiAktywnosc.class);
                zobaczZabieg.putExtra(NAZWA_POLA,nazwaP);

                startActivity(zobaczZabieg);


            }
        });

        // Informacje o polu

        ((Button)findViewById(R.id.brnInfoPole)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent infoPole = new Intent(getApplicationContext(),InformacjeoPolu.class);
                infoPole.putExtra(NAZWA_POLA,nazwaP);
                startActivity(infoPole);






            }
        });











    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pole, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action1_usunPole) {


            Toast.makeText(getApplicationContext(), "Usuwanie pola",
                    Toast.LENGTH_LONG).show();

            openDialog(nazwaPola.getText().toString());


        }

        return super.onOptionsItemSelected(item);
    }


    public void openDialog(final String name) {
        String[] dialogOpcje = {"Usuń pole","Anuluj"};
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(PoleAktywnosc.this, R.style.AlertDialogStyl) );
        alertDialogBuilder.setTitle("Pole: "+name+" - czy chcesz usunąć?").setItems(dialogOpcje, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:  { Toast.makeText(getApplicationContext(), "Usuwanie pola",
                            Toast.LENGTH_LONG).show();
            usunPole(name);
            finish();
            Intent intent = new Intent(getApplicationContext(),PoleAktywnosc.class);
            startActivity(intent);

                    }break;

                    case 1: {Toast.makeText(PoleAktywnosc.this, "Anulowano",
                            Toast.LENGTH_LONG).show(); }break;
                }
            }
        });  // return alertDialogBuilder.create();
               /* alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(getApplicationContext(),"You clicked yes  button",Toast.LENGTH_LONG).show();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });*/

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void usunPole(String nazwapola){



        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int idPola = mDBHelper.getIDbyName_tabelaPole(nazwapola);
        ContentValues val1 = new ContentValues();
        val1.put("posiadanie",0);




        String [] args= {Integer.toString(idPola)};
        long idd = db.update("pole", val1,"id_pola=?",args);

        db.close();
    }

}
