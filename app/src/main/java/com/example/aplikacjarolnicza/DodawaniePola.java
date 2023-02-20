package com.example.aplikacjarolnicza;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DodawaniePola extends AppCompatActivity {
RadioGroup radioGroup;
    public static final int KOD_OBSZAR=10;
  EditText nazwaPole, nrewidencyjny;
  EditText powierzchnia, planAzotowy;
    private BazaDanych bazaHelper;
    List<LatLng> latLngList = new ArrayList<>(); //  Lista przeznaczona na przechowywanie lokalizacji punktów na mapi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodawanie_pola);



        nazwaPole= ((EditText)findViewById(R.id.nazwaPolaView));
        powierzchnia=((EditText)findViewById(R.id.powierzchniaView));
 planAzotowy=((EditText)findViewById(R.id.planAzotowy));
 nrewidencyjny= ((EditText)findViewById(R.id.nrEwidencyjny));


// DOSTEP DO BAZY:
        final SQLiteOpenHelper dbHelper = new BazaDanych(this);
        bazaHelper= new BazaDanych(this);

      radioGroup= ((RadioGroup)findViewById(R.id.radioGroup));
    //reset:
      radioGroup.clearCheck();

      radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(RadioGroup group, int checkedId) {
              RadioButton radioButton = (RadioButton)group.findViewById(checkedId);

             /* Toast.makeText(DodawaniePola.this,
                      radioButton.getText(),
                      Toast.LENGTH_SHORT)
                      .show();*/

          }
      });

        ((Button)findViewById(R.id.btnZaznaczObszar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),ZaznaczObszar.class);
                startActivityForResult(i,KOD_OBSZAR);
            }
        });


        // ZAPISZ POLE
        ((Button)findViewById(R.id.btnZapiszPole)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Double szerokosc = null;
                Double dlugosc = null;
                String klasa_gleby = sprawdzRadio();
                System.out.println("\nradio= " + klasa_gleby);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (!nazwaPole.getText().toString().isEmpty() && !powierzchnia.getText().toString().isEmpty() && !planAzotowy.getText().toString().isEmpty() && !nrewidencyjny.getText().toString().isEmpty())
                {
if(!bazaHelper.czyIstniejeNazwaPola(1,nazwaPole.getText().toString())) {

        if (latLngList.size() > 0) {


            ContentValues val1 = new ContentValues();
            // tworzenie mapy wartości, gdzie nazwy kolumn są kluczami
            val1.put("nazwa", nazwaPole.getText().toString());
            val1.put("powierzchnia", Double.parseDouble(powierzchnia.getText().toString()));
            val1.put("klasa_gleby", klasa_gleby);
            val1.put("id_user", 1);
            val1.put("punkty_na_mapie", 1);
            val1.put("plan_azotowy", Double.parseDouble(planAzotowy.getText().toString()));
            val1.put("nr_ewidencyjny",nrewidencyjny.getText().toString());
            val1.put("posiadanie",1);
           // wstawienie wiersza z danymi do bazy
            long idd = db.insert("pole", null, val1);

            //int liczba = bazaHelper.getIDbyName_tabelaPole(nazwaPole.getText().toString());
            ContentValues val2 = new ContentValues();
            val2.put("nazwa", nazwaPole.getText().toString());
            val2.put("id_user", 1);
            val2.put("id_pola", idd);
            long iddObszar = db.insert("obszar", null, val2);


            for (int i = 0; i < latLngList.size(); i++) {
                dlugosc = latLngList.get(i).longitude;
                szerokosc = latLngList.get(i).latitude;
                // zapis do bazy:
                try {

                    ContentValues values = new ContentValues();
                    values.put(BazaDanych.KOLEJNOSC, i + 1);
                    values.put(BazaDanych.ID_POLA, idd);
                    values.put(BazaDanych.LAT, szerokosc);
                    values.put(BazaDanych.LNG, dlugosc);
                    values.put(BazaDanych.NAZWA_pola, nazwaPole.getText().toString());
                    values.put(BazaDanych.ID_OBSZAR, iddObszar);

                    long id = db.insert(BazaDanych.TABEL_NAME, null, values);


                } catch (SQLException e) {
                    Toast.makeText(DodawaniePola.this, "BŁĄD !! database..",
                            Toast.LENGTH_LONG).show();


                }


            }   // koniec for()

            Toast.makeText(DodawaniePola.this, "Dodano pole!",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), ObslugaAktywnosc.class);
            startActivity(i);
            finish();
        } else {
            System.out.println("\nBrak punktow na mapie");
            ContentValues val1 = new ContentValues();

            val1.put("nazwa", nazwaPole.getText().toString());
            val1.put("powierzchnia", Double.parseDouble(powierzchnia.getText().toString()));
            val1.put("klasa_gleby", klasa_gleby);
            val1.put("id_user", 1);
            val1.put("plan_azotowy", Double.parseDouble(planAzotowy.getText().toString()));
            val1.put("punkty_na_mapie", 0);
            val1.put("nr_ewidencyjny",nrewidencyjny.getText().toString());
            val1.put("posiadanie",1);
            long idd = db.insert("pole", null, val1);

            Toast.makeText(DodawaniePola.this, "Dodano pole!",
                    Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(), ObslugaAktywnosc.class);

            startActivity(i);
            finish();

        }   // koniec ifa ze sprawdzaniem czy sa pkty
    db.close();




} else {  // podana nazwa pola juz istnieje
    Toast.makeText(DodawaniePola.this, "Podana nazwa pola juz istnieje!",
            Toast.LENGTH_LONG).show();


}
            }else { Toast.makeText(DodawaniePola.this, "Podaj wszystkie dane!",
                        Toast.LENGTH_LONG).show();}
            }

        });// konec onClick


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode== KOD_OBSZAR){

           latLngList=(ArrayList<LatLng>)data.getSerializableExtra(ZaznaczObszar.RESULT_POINT);
            System.out.println("\n****\nOdebranaLista= "+latLngList);


         //   System.out.println("\nMoj double  "+d);

   // latLngList=(ArrayList<LatLng>)getIntent().getSerializableExtra("latLangList");
           // ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
         //   System.out.println("\n moja lista = "+latLngList);
        }



    }

    public String sprawdzRadio(){
         String radioTekst=null;
        int zaznaczony= radioGroup.getCheckedRadioButtonId();
        if(zaznaczony== -1){
            Toast.makeText(DodawaniePola.this,
                    "Nie zaznaczono",
                    Toast.LENGTH_SHORT)
                    .show();

        }
        else
        {

            RadioButton radioButton= (RadioButton)findViewById(zaznaczony);
           /* Toast.makeText(DodawaniePola.this,
                    radioButton.getText(),
                    Toast.LENGTH_SHORT)
                    .show();*/
            radioTekst=radioButton.getText().toString();

        }

return radioTekst;}




}
