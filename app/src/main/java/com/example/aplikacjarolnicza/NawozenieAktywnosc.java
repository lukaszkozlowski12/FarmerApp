package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NawozenieAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    RadioGroup radioGroupDawka,radioGroupSkladnik;
    TextView nazwaPolaView;
    EditText dawkaEdit,skladnikEdit,nazwaNawozuEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nawozenie_aktywnosc);

        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        nazwaPolaView= (TextView)findViewById(R.id.nazwaPolaView);
        nazwaPolaView.setText("Nazwa pola: "+nazwaPola);

        dawkaEdit=(EditText)findViewById(R.id.dawkaEdit);
        skladnikEdit=(EditText)findViewById(R.id.skladnikEdit);
        nazwaNawozuEdit=(EditText)findViewById(R.id.nazwaNawozuEdit);


          // dostep do bazy
        dbHelper= new BazaDanych(this);
        final SQLiteOpenHelper helper = new BazaDanych(this);


        radioGroupDawka= (RadioGroup)findViewById(R.id.radioGroupDawka);
        radioGroupSkladnik=(RadioGroup)findViewById(R.id.radioGroupSkladnik);

        radioGroupSkladnik.clearCheck();
radioGroupDawka.clearCheck();

        radioGroupDawka.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);

              /*  Toast.makeText(NawozenieAktywnosc.this,
                        radioButton.getText(),
                        Toast.LENGTH_SHORT)
                        .show();*/

            }
        });
        radioGroupSkladnik.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);

              /*  Toast.makeText(NawozenieAktywnosc.this,
                        radioButton.getText(),
                        Toast.LENGTH_SHORT)
                        .show();*/

            }
        });







        ((Button)findViewById(R.id.btnDodajNawozenie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!nazwaNawozuEdit.getText().toString().isEmpty() && !dawkaEdit.getText().toString().isEmpty() && !skladnikEdit.getText().toString().isEmpty()) {
                    // połączenie z bazą
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    // pobranie id pola
                    int idPola = dbHelper.getIDbyName_tabelaPole(nazwaPola);
                    // tworzenie mapy typu nazwa(kolumny) -> wartość
                    ContentValues val1 = new ContentValues();
                    val1.put("id_pola", idPola);
                    val1.put("nazwa_nawozu", nazwaNawozuEdit.getText().toString());
                    // sprawdzenie czy zaznaczono kg/ha
                    if (sprawdzRadio(radioGroupDawka).equals("kg/ha")) {
                        val1.put("dawka", Float.parseFloat(dawkaEdit.getText().toString()));
                        val1.put("dawka_kg", 1);
                        val1.put("dawka_litry", 0);

                    } else { // jeśli wybrano litry:
                        System.out.println("radio spr=" + sprawdzRadio(radioGroupDawka));
                        val1.put("dawka", Float.parseFloat(dawkaEdit.getText().toString()));
                        val1.put("dawka_litry", 1);
                        val1.put("dawka_kg", 0);
                    }

                    val1.put("substancja", sprawdzRadio(radioGroupSkladnik));
                    val1.put("zawartosc_procentowa", Float.parseFloat(skladnikEdit.getText().toString()));
                    val1.put("id_pola", idPola);
                    val1.put("id_user", 1);
                    val1.put("data_wykonania", getDateTime());

                      // wstawienie danych do bazy
                    long idd = db.insert("nawozy", null, val1);
                        // zakonczenie polaczenia
                    db.close();
                    finish(); // zakończenei aktywności
                    // przejście do aktywności z tworzeniem zabiegow
                    Intent i = new Intent(getApplicationContext(), PoleAktywnosc.class);
                    // umieszczenie w obiekcie intencji nazwy pola
                    i.putExtra("nazwa", nazwaPola);
                   // uruchomienie aktywnosci
                    startActivity(i);
                } else {

                    Toast.makeText(NawozenieAktywnosc.this,
                            "Wprowadź wszystkie dane..",
                            Toast.LENGTH_SHORT)
                            .show();

                }
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
        String radioTekst=null;
        // sprawdzenie id zaznaczonego elementu
        int zaznaczony= radioGroup.getCheckedRadioButtonId();
        // jeśli nie jest zaznaczony to wynosi -1
        if(zaznaczony== -1){
            Toast.makeText(NawozenieAktywnosc.this,
                    "Nie zaznaczono",
                    Toast.LENGTH_SHORT).show();
        } else
        {
            RadioButton radioButton= (RadioButton)findViewById(zaznaczony);
            Toast.makeText(NawozenieAktywnosc.this,
                    radioButton.getText(),
                    Toast.LENGTH_SHORT)
                    .show();
            radioTekst=radioButton.getText().toString();

        }
        return radioTekst;}
}
