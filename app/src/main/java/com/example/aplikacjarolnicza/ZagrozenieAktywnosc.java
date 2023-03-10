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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ZagrozenieAktywnosc extends AppCompatActivity {
    public static final  String NAZWA_POLA="nazwaPola";
    private BazaDanych dbHelper;
    RadioGroup radioGroupZagrozenie;
    EditText inneZagrozenie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zagrozenie_aktywnosc);


        final String nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        // dostep do bazy
        dbHelper= new BazaDanych(this);

        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola:"+nazwaPola);
        // powierzchnia.setText("Powierzchnia: "+Float.toString(mDBHelper.getPowierzchniaByName(nazwaP))+" ha");
        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola))+"ha");
        inneZagrozenie= (EditText)findViewById(R.id.inneZagrozenie);




        radioGroupZagrozenie= (RadioGroup)findViewById(R.id.radioGroupZagrozenie);
radioGroupZagrozenie.clearCheck();
        




        ((Button)findViewById(R.id.btnDodajZagrozenie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
       //sprawdzenie czy wszystko zaznaczone
    if(!sprawdzRadio(radioGroupZagrozenie).contains("brak")) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    // przypisanie do zmiennej id pola
    int idPola = dbHelper.getIDbyName_tabelaPole(nazwaPola);
    // tworzenie mapy powi??za?? nazwa->wartosc
    ContentValues val1 = new ContentValues();
    // przypisanie do nazwy id_pola id danej dzia??ki
    val1.put("id_pola", idPola);
    // funkcja sprawdza czy wybrano przycisk Inne
    if (sprawdzRadio(radioGroupZagrozenie).equals("Inne")) {
  // je??li tak to umieszcza zawartosc z pola w nazwa-zagrozenia
        val1.put("nazwa_zagrozenia", inneZagrozenie.getText().toString());
    } else {
        // je??li nie to pobiera to co zaznaczono
        val1.put("nazwa_zagrozenia", sprawdzRadio(radioGroupZagrozenie) + " "
                + inneZagrozenie.getText().toString());
    }
    val1.put("id_user", 1);
    // okre??lenie daty za pomoc?? funkcji
    val1.put("data_wykonania", getDateTime());
   //  wprowadzenie danych do tabeli zagrozenie
    db.insert("zagrozenie", null, val1);
    db.close(); // zakonczenie po????czenia z baza
    finish(); // zako??czenie aktywno??ci
    // tworzenie obiektu intencji do nowej aktywno??ci
    Intent i = new Intent(getApplicationContext(), PoleAktywnosc.class);
    // umieszczenie w obiekcie intencji nazwe pola
    i.putExtra("nazwa", nazwaPola);
    startActivity(i); // start aktywnosci
}

            }
        });








    }

    private String getDateTime() {
        // utworzenie obiektu SimpleDateFormat
        // oraz okre??lenie formatu (pattern)
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // utworzenie obiektu Date
        Date date = new Date();
        // zwr??cenie daty
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
