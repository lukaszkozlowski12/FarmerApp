package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UprawaAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    RadioGroup radioGroupUprawa;
    EditText uwagiEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uprawa_aktywnosc);

        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        // dostep do bazy
        dbHelper= new BazaDanych(this);
        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola:"+nazwaPola);
        // powierzchnia.setText("Powierzchnia: "+Float.toString(mDBHelper.getPowierzchniaByName(nazwaP))+" ha");
        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola))+"ha");

        radioGroupUprawa= (RadioGroup)findViewById(R.id.radioGroupUprawa);
        radioGroupUprawa.clearCheck();

        uwagiEdit=(EditText)findViewById(R.id.uwagiEdit);


        ((Button)findViewById(R.id.btnDodajUprawe)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sprawdzenie czy cos zaznaczono
                if(!sprawdzRadio(radioGroupUprawa).contains("brak")) {
                    // połączenie z baza
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    // porbanie id pola (klucza głównego)
                    int idPola = dbHelper.getIDbyName_tabelaPole(nazwaPola);
                    // utworzenie mapy : nazwa->wartosc
                    ContentValues val1 = new ContentValues();
                   // do id_pola przypisano pobrane id danego pola
                    val1.put("id_pola", idPola);
                    // do nazwy_czynnosci przypisano zaznaczona czynnosc
                    val1.put("nazwa_czynnosci", sprawdzRadio(radioGroupUprawa));
                    val1.put("id_user", 1);
                    // do uwagi wprowadzono zawartosc pola wwagiEdit
                    val1.put("uwagi", uwagiEdit.getText().toString());
                    //wprowadzenie daty wykonania
                    val1.put("data_wykonania", getDateTime());
                    // wykonanie polecenia wprowadzajacego dane
                    db.insert("uprawa", null, val1);
                    db.close(); // koniec połączenia
                    finish(); // koniec aktywnosci, a pod spodem wystartowanie nowej
                    Intent i = new Intent(getApplicationContext(), PoleAktywnosc.class);
                    i.putExtra("nazwa", nazwaPola);
                    startActivity(i);
                } else { // jeśli nic nie zaznaczono to komunikat
                    Toast.makeText(getApplicationContext(), "Wprowadź wszystkie dane",
                            Toast.LENGTH_SHORT).show(); }
            }
        });







    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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
}
