package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class EdytujNawozenie extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private BazaDanych dbHelper;
    EditText nazwaNawozuET,dawkaET,skladnikEdit2;
    TextView nazwapolaTV, dateEdit;
    RadioGroup radioGroupDawka,radioGroupSkladnik;
    RadioButton btnkg, btnlitr,btnazot,btnfosfor,btnpotas,btninne;
    private int mYear, mMonth, mDay, mHour, mMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edytuj_nawozenie);

     final String nazwa_pola= getIntent().getStringExtra("nazwa_pola");

     final String nazwa_nawozu=getIntent().getStringExtra("nazwa_nawozu");
     final String dawka = getIntent().getStringExtra("dawka");
     final String data_wykonania = getIntent().getStringExtra("data_wykonania");
     final String id_nawoz = getIntent().getStringExtra("id_nawoz");
        dbHelper= new BazaDanych(this);
        radioGroupDawka= (RadioGroup)findViewById(R.id.radioGroupDawka);
        radioGroupSkladnik=(RadioGroup)findViewById(R.id.radioGroupSkladnik);

        radioGroupSkladnik.clearCheck();
        radioGroupDawka.clearCheck();
        btnkg = (RadioButton)findViewById(R.id.radioKg);
        btnlitr = (RadioButton)findViewById(R.id.radioLitr);

        btnazot= (RadioButton)findViewById(R.id.radioAzot);
        btnfosfor= (RadioButton)findViewById(R.id.radioFosfor);
        btnpotas= (RadioButton)findViewById(R.id.radioPotas);
        btninne = (RadioButton)findViewById(R.id.radioInne);


nazwapolaTV =((TextView)findViewById(R.id.nazwapolaTV));
nazwapolaTV.setText(nazwa_pola);

nazwaNawozuET = (EditText)findViewById(R.id.nazwanawozuET);
nazwaNawozuET.setText(nazwa_nawozu);
dawkaET= (EditText)findViewById(R.id.dawkaET);
        String[] arrOfStr=null;
String d = dawka;
if(dawka.contains("l/ha")) {arrOfStr= d.split("l/ha");   btnlitr.setChecked(true);}
else if(dawka.contains("kg/ha"))  {arrOfStr= d.split("kg/ha");    btnkg.setChecked(true);}

String substancja = dbHelper.getSubstancjaNawozbyNameNawoz_tabelaNawozy(nazwa_nawozu,dbHelper.getIDbyName_tabelaPole(nazwa_pola));

        if(substancja.contains("Azot")){
            btnazot.setChecked(true);
        } else if(substancja.contains("Fosfor")){ btnfosfor.setChecked(true);}
        else if(substancja.contains("Potas")){ btnpotas.setChecked(true);}
        else btninne.setChecked(true);


dawkaET.setText(arrOfStr[0]);
dateEdit= (TextView)findViewById(R.id.editTextDate);
dateEdit.setText(data_wykonania);
skladnikEdit2=(EditText)findViewById(R.id.skladnikEdit2);
        System.out.println("Nazwa pola= "+nazwapolaTV.getText().toString());

int idpola=dbHelper.getIDbyName_tabelaPole(nazwapolaTV.getText().toString());

        System.out.println("idPPola="+idpola);
skladnikEdit2.setText(Float.toString(dbHelper.getZawartoscbyNameNawoz_tabelaNawozy(nazwaNawozuET.getText().toString(),idpola)));




        ((Button)findViewById(R.id.btnData)).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(); // utworzono obiekt Calendar
        mYear = calendar.get(Calendar.YEAR); // przypisanie roku
        mMonth = calendar.get(Calendar.MONTH); // przypisanie miesiąca
        mDay = calendar.get(Calendar.DAY_OF_MONTH); // przypisanie dnia
        // utworzenie i pokazanie okna dialogowego do edycji czasu
        DatePickerDialog datePickerDialog = new DatePickerDialog(EdytujNawozenie.this,
                EdytujNawozenie.this,mYear, mMonth,mDay);
        datePickerDialog.show();
    }








});






        // dostep do bazy




        ((Button)findViewById(R.id.btnZapiszNawozenie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!nazwaNawozuET.getText().toString().isEmpty() && !dawkaET.getText().toString().isEmpty()
                        && !skladnikEdit2.getText().toString().isEmpty()
                        &&!sprawdzRadio(radioGroupSkladnik).contains("brak")) {
                  // połączenei z bazą danych
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                  // pobranie id pola
                    int idPola = dbHelper.getIDbyName_tabelaPole(nazwa_pola);
                    // stworzenie mapy powiązań: nazwa(kolumny)->wartość
                    ContentValues val1 = new ContentValues();
                   // val1.put("id_pola", idPola);
                    val1.put("nazwa_nawozu", nazwaNawozuET.getText().toString());
                   // sprawdzenie czy dawka jest w kg
                 if (sprawdzRadio(radioGroupDawka).equals("kg/ha")) {
                        val1.put("dawka", Float.parseFloat(dawkaET.getText().toString()));
                        val1.put("dawka_kg", 1);
                        val1.put("dawka_litry", 0);
                 } else {  // jeśli dawka w litrach
                        val1.put("dawka", Float.parseFloat(dawkaET.getText().toString()));
                        val1.put("dawka_litry", 1);
                        val1.put("dawka_kg", 0);
                    }
                    val1.put("substancja", sprawdzRadio(radioGroupSkladnik));
                    val1.put("zawartosc_procentowa", Float.parseFloat(skladnikEdit2.getText().toString()));
                    val1.put("data_wykonania", dateEdit.getText().toString());

                       // określenie warunków dla których ma być edycja
                       String [] args= { id_nawoz ,Integer.toString(idPola)};
                  // wykonanie polecenia do aktualizacji danych
                    long idd = db.update("nawozy", val1,"id_nawoz=? AND id_pola=?",args);
                    db.close();// zamknięcie bazy
             // utworzenie obiektu intencji do przejscia na zestawienei nawożenia
                    Intent i = new Intent(getApplicationContext(), ZestawienieNawozenieAktywnosc.class);
                    startActivity(i);
                    finish();// zamknięcie bieżącej aktywności
                } else { // jeśli dane nie zostały wprowadzone
   // komunikat
                    Toast.makeText(getApplicationContext(),
                            "Wprowadź wszystkie dane..",
                            Toast.LENGTH_SHORT)
                            .show();

                }








            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mDay = dayOfMonth;
        mMonth = month+1;
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(EdytujNawozenie.this, EdytujNawozenie.this, mHour, mMinute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
      dateEdit.setText(mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute);
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