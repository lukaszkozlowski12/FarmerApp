package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.stream.reader.LineReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SzacowanieGlebyAktywnosc extends AppCompatActivity {
    CsvPlik csvPlik;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.szacowanie_gleby_aktywnosc);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

            System.out.println("\nczy sa all uprawnienia ");
            return;
        }



        csvPlik = new CsvPlik();
        BazaDanych db = new BazaDanych(this);
         // pobranie danych z limitem oraz zestawieniem stanu azotu
        final ArrayList<HashMap<String, String>> listaDoszacowania = db.PobierzListedoSzacowania();
        // określenie listview
        ListView listView = (ListView) findViewById(R.id.listviewAzot);
        // utworzenie adaptera, który pośredniczy pomiedzy danymi a lista
        ListAdapter adapter = new SimpleAdapter(SzacowanieGlebyAktywnosc.this,
                listaDoszacowania, // wskazanie listy z danymi
                R.layout.wyglad_listy_plan_azotowy,  // ustawienie pliku z widokiem
                // pobranie z listy danych określonych hashmapa
                new String[]{"nazwa_pola", "plan", "sumaAzotu"},
                // wskazanie id w widoku
                new int[]{R.id.nazwaPolaazot, R.id.plan, R.id.sumaAzotu}

        ) {
            @Override // nadpisanei metody
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                // jeśli lista z dana pozycja zawiera słowo PLANOK oznacza to
                // że limit jest prawidłowy
                // zatem kolor zielony
                if(listaDoszacowania.get(position).toString().contains("PLANOK")){
                    v.setBackgroundColor(Color.GREEN);
                } else {// jeśli limit przekroczony
                    // ustawienie koloru elementu listy na czerwony
                    v.setBackgroundColor(Color.RED);}
                return v;
            }
        };


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearList);


        for (int i = 0; i < listaDoszacowania.size(); i++) {

            if (listaDoszacowania.get(i).containsValue("PLANOK")) {
                System.out.println("\n SPRAWDZAM STAN =" + listaDoszacowania.get(i).containsValue("PLANOK"));

                System.out.println("\nAdapter info " + adapter.getItem(i).toString().contains("PLANOK"));
                //  System.out.println("\nWez getView="+ adapter.getView(i,imageView,null).findViewWithTag("imageInfoAzot"));
                System.out.println("\nAdapter    " + adapter.getItem(i));
                 adapter.getView(i,linearLayout,null);
                // System.out.println("\n  Listview==: "+ adapter.getView(i,linearLayout,null).findViewWithTag("imageInfoAzot"));
//if(adapter.getView(i,imageView,null).findViewWithTag("imageInfoAzot")!=null){
                //listView.setBackgroundColor(Color.TRANSPARENT);

                //System.out.println("\n Kolor przed zmianie   "+ adapter.getView(i,plan,null).getSolidColor());
                 //adapter.getView(i,imageView,null).setBackgroundColor(Color.RED);

                //  System.out.println("\n Kolor po zmianie   "+ adapter.getView(i,plan,null).getSolidColor());
//}

                // }else ;
            }


            listView.setAdapter(adapter);



            ((Button) findViewById(R.id.btnWroc)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent powrot = new Intent(getApplicationContext(), EwidencjaAktywnosc.class);

                    startActivity(powrot);
                    finish();

                }
            });


            ((Button) findViewById(R.id.btnCsvPlanAzot)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try { // próba stworzenia pliku
                        csvPlik.utworzCsv(getApplicationContext());
                        // komunikat
                        Toast.makeText(getApplicationContext(), "Utworzono plik!",
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) { // jeśli wystąpi błąd
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "BLAD!!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });


        }


    }}
