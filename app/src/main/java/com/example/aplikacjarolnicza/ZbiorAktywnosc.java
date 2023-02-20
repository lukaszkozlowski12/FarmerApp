package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ZbiorAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    EditText nazwaRosliny,plon;
    RadioGroup radioPlon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zbior_aktywnosc);

        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        // dostep do bazy
        dbHelper= new BazaDanych(this);
        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola: "+nazwaPola);

        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola)));
     nazwaRosliny= (EditText)findViewById(R.id.nazwaRoslinyEdit);
     plon= (EditText)findViewById(R.id.plonEdit);
radioPlon=(RadioGroup)findViewById(R.id.radioGroupPlon);


        ((Button)findViewById(R.id.btnZapiszZbior)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!nazwaRosliny.getText().toString().isEmpty() && !nazwaRosliny.getText().toString().equals("") &&  !plon.getText().toString().isEmpty() && !sprawdzRadio(radioPlon).contains("brak") ) {
                   // utworzenie połączenie z bazą danych
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    // pobranie id pola (klucza głównego)
                    int idPola = dbHelper.getIDbyName_tabelaPole(nazwaPola);
                    // tworzenie mapy: nazwa wartość
                    ContentValues val1 = new ContentValues();
                      // do id_pola zostaje przypisane id danego pola
                    val1.put("id_pola", idPola);
                    // do nazwa_rosliny zostaje przypisana wartość z pola nazwaRosliny
                    val1.put("nazwa_rosliny", nazwaRosliny.getText().toString());
                     // funkcja sprawdza zaznaczenie czy plon był w tonach czy w sztukach
                    if (sprawdzRadio(radioPlon).equals("ton/ha")) {
                        val1.put("plon", Float.parseFloat(plon.getText().toString()));
                        val1.put("plon_w_tonach", 1);  // jeśli w tonach to wartosc na 1
                        val1.put("plon_w_sztukach", 0);
                    } else {
                        val1.put("plon", Float.parseFloat(plon.getText().toString()));
                        val1.put("plon_w_tonach", 0);
                        // jesli w sztukach to wartosc na 1 (true)
                        val1.put("plon_w_sztukach", 1); }
                    val1.put("id_user", 1);
                    val1.put("data_wykonania", getDateTime()); // zapis aktualnej daty
                    // wprowadzenie danych
                    long id = db.insert("zbior", null, val1);
                    db.close(); // zakonczenie polączenia z baza
                    finish();  // koniec aktywnosci
                    // przekierowanie na aktywnosc wyboru zabiegu
                    Intent i = new Intent(getApplicationContext(), PoleAktywnosc.class);
                    // umieszczenie nazwy pola w obiekcie ontencji
                    i.putExtra("nazwa", nazwaPola);
                    startActivity(i);  // start aktywnosci


                } else {
                    Toast.makeText(getApplicationContext(),
                            "Wprowadz wszystkie dane ..",
                            Toast.LENGTH_SHORT)
                            .show();

                }


            }
        });


    }

    public String sprawdzRadio(RadioGroup radioGroup){
        String radioTekst="brak";
        int zaznaczony= radioGroup.getCheckedRadioButtonId();
        if(zaznaczony== -1){
            Toast.makeText(getApplicationContext(),
                    "Nie zaznaczono",
                    Toast.LENGTH_SHORT)
                    .show();

        }
        else
        {

            RadioButton radioButton= (RadioButton)findViewById(zaznaczony);

            radioTekst=radioButton.getText().toString();

        }

        return radioTekst;}
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
