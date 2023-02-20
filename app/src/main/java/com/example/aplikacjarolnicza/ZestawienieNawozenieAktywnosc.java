package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.ListFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

public class ZestawienieNawozenieAktywnosc extends AppCompatActivity  {
Intent intent;

    CsvNawozenie csvPlikNawoz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zestawienie_nawozenie_aktywnosc);

      BazaDanych db = new BazaDanych(this);
        ArrayList<HashMap<String, String>> listaNawozenia = db.PobierzListeNawozenia();
        csvPlikNawoz = new CsvNawozenie();

//        System.out.println("\n Lista NNNN "+listaNawozenia.get(1));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            return;
        }

    //    FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton2);



       // System.out.println("\nAdapter get "+adapter.getItem(1));




     final ListView listView = (ListView)findViewById(R.id.polaListaNawoz);
        ListAdapter adapter = new SimpleAdapter(ZestawienieNawozenieAktywnosc.this,listaNawozenia,R.layout.wyglad_listy_nawozenia,new String[]{"nazwa_pola","nazwa_nawozu","dawka","data_wykonania"}, new int[]{R.id.nazwa_pola,R.id.nazwa_nawozu,R.id.dawka,R.id.data_wykonania});



        listView.setAdapter(adapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                TextView nazwaNawozu = view.findViewById(R.id.nazwa_nawozu);
                TextView data = view.findViewById(R.id.data_wykonania);
                TextView nazwaPola= view.findViewById(R.id.nazwa_pola);
                TextView dawka = view.findViewById(R.id.dawka);


              //  System.out.println("\n DAtaaaaaaaaa"+data.getText().toString());

                String value=adapterView.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();

                String [] wartosci = value.split(",", 5);
                for (String word : wartosci)
                    System.out.println("\n"+word);
                System.out.println("String [3] ="+wartosci[3]);

                String tekst = wartosci[3];
                wartosci=tekst.split("=",2);
                int id_nawoz= Integer.parseInt(wartosci[1]);
                System.out.println("\nid_nawozUU === "+id_nawoz);




                System.out.println("\nItem "+value);
                AlertDialogEdytujNawozenie(nazwaPola.getText().toString(),nazwaNawozu.getText().toString(),dawka.getText().toString(),data.getText().toString(),Integer.toString(id_nawoz));



            }
        });








        ((Button)findViewById(R.id.btnWroc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent wroc = new Intent(getApplicationContext(),EwidencjaAktywnosc.class);
                startActivity(wroc);


            }
        });

        ((Button)findViewById(R.id.btnCsvNawoz)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    csvPlikNawoz.utworzCsvNawoz(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Utworzono plik!",
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {  // jeśli wystąpił błąd
                    e.printStackTrace();
                    // komunikat:
                    Toast.makeText(getApplicationContext(), "BLAD , plik nawożenie CSV!!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }






    public void AlertDialogEdytujNawozenie(final String nazwapola, final String nazwanawozu, final String dawka, final String data, final String id_nawoz) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edycja danych"); // tytuł okna dialogowego
        builder.setMessage("Czy chcesz edytować ten zabieg?");
        // dodanie i obsługa przycisku Tak
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent punkty = new Intent(getApplicationContext(),EdytujNawozenie.class);
               // umieszczenie w intencji danych zabiegu
                punkty.putExtra("nazwa_pola",nazwapola);
                punkty.putExtra("nazwa_nawozu",nazwanawozu);
                punkty.putExtra("dawka",dawka);
                punkty.putExtra("data_wykonania",data);
                punkty.putExtra("id_nawoz",id_nawoz);
                startActivity(punkty);
                finish();

            }
        });
        // dodanie przycisku anuluj
        builder.setNegativeButton("Anuluj", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
