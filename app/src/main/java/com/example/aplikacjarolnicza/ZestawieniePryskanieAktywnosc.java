package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ZestawieniePryskanieAktywnosc extends AppCompatActivity {
    Intent intent;
    CsvPryskanie csvPryskanie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zestawienie_pryskanie_aktywnosc);


        BazaDanych db = new BazaDanych(this);
        ArrayList<HashMap<String, String>> listaPryskania = db.PobierzListeOpryskow();

        ListView listView1 = (ListView)findViewById(R.id.polaListaOpryski);
        ListAdapter adapter = new SimpleAdapter(ZestawieniePryskanieAktywnosc.this,listaPryskania,R.layout.wyglad_listy_pryskania,new String[]{"nazwa_pola","nazwa_oprysku","dawka","data_wykonania"}, new int[]{R.id.nazwa_pola,R.id.nazwa_oprysku,R.id.dawka,R.id.data_wykonania});



        System.out.println("\n Opryski "+listaPryskania.toString());
        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                TextView nazwaOprysku = view.findViewById(R.id.nazwa_oprysku);
                TextView data = view.findViewById(R.id.data_wykonania);
                TextView nazwaPola= view.findViewById(R.id.nazwa_pola);
                TextView dawka = view.findViewById(R.id.dawka);




                System.out.println("\n Nazwa= "+nazwaOprysku.getText().toString());
                String value=parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();


                String [] wartosci = value.split(",", 5);
                for (String word : wartosci)
                    System.out.println("\nword="+word);
                System.out.println("String [1] ="+wartosci[1]);

                String tekst = wartosci[1];
                wartosci=tekst.split("=",2);
                System.out.println("\nwartosci= "+wartosci[1]);
                int id_oprysk= Integer.parseInt(wartosci[1]);
               System.out.println("\nid_opryskuU === "+id_oprysk);




                System.out.println("\nItem "+value);



                AlertDialogEdytujPryskanie(nazwaPola.getText().toString(),nazwaOprysku.getText().toString(),dawka.getText().toString(),data.getText().toString(),Integer.toString(id_oprysk));


            }
        });





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


            return;
        }

csvPryskanie = new CsvPryskanie();




        ((Button)findViewById(R.id.btnWroc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent wroc = new Intent(getApplicationContext(),EwidencjaAktywnosc.class);
                startActivity(wroc);


            }
        });



        ((Button)findViewById(R.id.btnCsvPryskanie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { // próba utworzenia pliku
                    csvPryskanie.utworzCsvPryskanie(getApplicationContext());
                    // komunikat
                    Toast.makeText(getApplicationContext(), "Utworzono plik!",
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) { // jeśli wystąpi błąd to komunikat
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error, plik CSV pryskanie!!",
                            Toast.LENGTH_LONG).show();
                }




            }
        });

    }

       public void AlertDialogEdytujPryskanie(final String nazwapola,
                                           final String nazwaoprysku,
                                           final String dawka, final String data,
                                              final String idoprysk) {
        // załadowanie budowniczego okna dialogowego
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edycja danych"); // określenie tytułu
        builder.setMessage("Czy chcesz edytować ten zabieg?"); // komunikat
        // dodanie i obsługa przycisku Tak
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // tworzenie obiektu intencji, który umożliwi przejście do panelu edycji
                Intent punkty = new Intent(getApplicationContext(),EdytujPryskanie.class);
                //umieszczenie danych zabiegu w obiekcie intencji
                punkty.putExtra("nazwa_pola",nazwapola);
                punkty.putExtra("nazwa_oprysku",nazwaoprysku);
                punkty.putExtra("dawka",dawka);
                punkty.putExtra("data_wykonania",data);
                punkty.putExtra("id_oprysk",idoprysk);
                startActivity(punkty);// start aktywności
                finish(); // zakończenie poprzedniej aktywności
            }
        });
        // dodanie przycisku Anuluj
        builder.setNegativeButton("Anuluj", null);
        // stworzenie i wyświetlenie okna dialogowego
        AlertDialog dialog = builder.create();
        dialog.show(); }}
