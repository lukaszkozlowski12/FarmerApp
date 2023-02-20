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

import com.google.android.gms.common.util.ScopeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PryskanieAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    TextView poleNazwa;
    EditText nazwaOprysku,dawkaOprysku,przyczyna;
    RadioGroup radioGroupDawka, radioGroupRodzaj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pryskanie_aktywnosc);
        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);

        // dostep do bazy
        dbHelper= new BazaDanych(this);
         poleNazwa=(TextView)findViewById(R.id.nazwaPolaView);
        poleNazwa.setText("Nazwa pola: "+nazwaPola);

         nazwaOprysku=(EditText)findViewById(R.id.nazwaopryskuEdit);
         dawkaOprysku=(EditText)findViewById(R.id.dawkaEdit);
         przyczyna=(EditText)findViewById(R.id.przyczynaEdit);
      radioGroupDawka= (RadioGroup)findViewById(R.id.radioGroupdawka);
      radioGroupRodzaj=(RadioGroup)findViewById(R.id.radioGroupRodzaj);


        radioGroupRodzaj.clearCheck();
        radioGroupDawka.clearCheck();

        radioGroupDawka.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);

                Toast.makeText(PryskanieAktywnosc.this,
                        radioButton.getText(),
                        Toast.LENGTH_SHORT)
                        .show();

            }
        });
        radioGroupRodzaj.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);

                Toast.makeText(PryskanieAktywnosc.this,
                        radioButton.getText(),
                        Toast.LENGTH_SHORT)
                        .show();

            }
        });

        ((Button)findViewById(R.id.btndodajPryskanie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


if(!nazwaOprysku.getText().toString().isEmpty() && !dawkaOprysku.getText().toString().isEmpty() && !przyczyna.getText().toString().isEmpty() && !sprawdzRadio(radioGroupDawka).contains("brak") && !sprawdzRadio(radioGroupRodzaj).contains("brak") ) {
     // połączenie z bazą
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    // pobranie klucza głównego id_pola z tabeli pole
    int idPola = dbHelper.getIDbyName_tabelaPole(nazwaPola);
      // Tworzenie mapy nazwa ->wartość
    ContentValues val1 = new ContentValues();
    val1.put("id_pola", idPola);
    val1.put("nazwa_oprysku", nazwaOprysku.getText().toString());
       // sprawdzenie pola radio
    if (sprawdzRadio(radioGroupDawka).equals("kg/ha")) {
        // jeśli zaznaczono kg to ustaw pole z kg na 1
        val1.put("dawka", Float.parseFloat(dawkaOprysku.getText().toString()));
        val1.put("dawka_kg", 1);
        val1.put("dawka_litry", 0);
    } else {
        // jeśli zaznaczono litry to flaga(kolumna) dawka_litry wynosi 1
        val1.put("dawka", Float.parseFloat(dawkaOprysku.getText().toString()));
        val1.put("dawka_litry", 1);
        val1.put("dawka_kg", 0);     }
    val1.put("rodzaj_srodka", sprawdzRadio(radioGroupRodzaj));
    val1.put("cel_stosowania", przyczyna.getText().toString());
    val1.put("id_user", 1);
    val1.put("id_pola", idPola);
    val1.put("data_wykonania", getDateTime());
    // umieszczenie danych
    db.insert("opryski", null, val1);
    db.close();
    finish();  // zakończenie
    // tworzenie aktywności z tworzenie mzabiegu
    Intent i = new Intent(getApplicationContext(), PoleAktywnosc.class);
     //umieszczenei w obiekcie intencji nazwy danego pola
    i.putExtra("nazwa", nazwaPola);
    startActivity(i);

}else {

    Toast.makeText(getApplicationContext(),
            "Wprowadź wszystkie dane..",
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
