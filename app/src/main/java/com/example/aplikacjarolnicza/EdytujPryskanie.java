package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.util.Calendar;

public class EdytujPryskanie extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText nazwaOpryskuet,przyczynaet,dawkaet;
    TextView datatv,nazwaPolatv;
    RadioGroup radioGroupDawka,radioGroupSkladnik;
    RadioButton btnkg, btnlitr;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private BazaDanych dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edytuj_pryskanie);
        dbHelper= new BazaDanych(this);

        final String nazwa_pola= getIntent().getStringExtra("nazwa_pola");
        final String nazwa_oprysku=getIntent().getStringExtra("nazwa_oprysku");
        final String dawka = getIntent().getStringExtra("dawka");
        final String data_wykonania = getIntent().getStringExtra("data_wykonania");
        final String id_oprysk= getIntent().getStringExtra("id_oprysk");

        radioGroupDawka= (RadioGroup)findViewById(R.id.radioGroupdawka);
        radioGroupSkladnik=(RadioGroup)findViewById(R.id.radioGroupRodzaj);

        radioGroupSkladnik.clearCheck();
        radioGroupDawka.clearCheck();

        btnkg = (RadioButton)findViewById(R.id.radiokg);
        btnlitr = (RadioButton)findViewById(R.id.radiolitr);
        nazwaPolatv=((TextView)findViewById(R.id.nazwaPolaTV));
        nazwaPolatv.setText(nazwa_pola);
        nazwaOpryskuet= ((EditText)findViewById(R.id.nazwaOpryskuET));
        nazwaOpryskuet.setText(nazwa_oprysku);

        dawkaet= ((EditText)findViewById(R.id.dawkaopyskET));

        String[] arrOfStr=null;
        String d = dawka;
        if(dawka.contains("l/ha")) {arrOfStr= d.split("l/ha");
        btnkg.setChecked(true);
        }
        else if(dawka.contains("kg/ha"))  {arrOfStr= d.split("kg/ha");
       btnlitr.setChecked(true);
       // btnkg.setChecked(true);
        }

        dawkaet.setText(arrOfStr[0]);

        przyczynaet = ((EditText)findViewById(R.id.przyczynaET));
        przyczynaet.setText(dbHelper.getPrzyczynabyByNazwaOprysku_tabelaOprysk(nazwa_oprysku, dbHelper.getIDbyName_tabelaPole(nazwa_pola)));
  datatv = (TextView)findViewById(R.id.dataTV);
        datatv.setText(data_wykonania);

        ((Button)findViewById(R.id.btnZmienDate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EdytujPryskanie.this, EdytujPryskanie.this,mYear, mMonth,mDay);
                datePickerDialog.show();
            }
        });





        ((Button)findViewById(R.id.btnZapiszZmianyop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!nazwaOpryskuet.getText().toString().isEmpty() && !dawkaet.getText().toString().isEmpty() && !przyczynaet.getText().toString().isEmpty() && !sprawdzRadio(radioGroupSkladnik).contains("brak") ) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int idPola = dbHelper.getIDbyName_tabelaPole(nazwa_pola);
                    ContentValues val1 = new ContentValues();

                    // val1.put("id_pola", idPola);
                    val1.put("nazwa_oprysku", nazwaOpryskuet.getText().toString());

                    if (sprawdzRadio(radioGroupDawka).equals("kg/ha")) {
                        val1.put("dawka", Float.parseFloat(dawkaet.getText().toString()));
                        val1.put("dawka_kg", 1);
                        val1.put("dawka_litry", 0);

                    } else {

                        val1.put("dawka", Float.parseFloat(dawkaet.getText().toString()));
                        val1.put("dawka_litry", 1);
                        val1.put("dawka_kg", 0);
                    }


                    val1.put("rodzaj_srodka", sprawdzRadio(radioGroupSkladnik));

                    val1.put("cel_stosowania", przyczynaet.getText().toString());
                    // val1.put("id_pola", idPola);
                    // val1.put("id_user", 1);
                    val1.put("data_wykonania", datatv.getText().toString());


                    System.out.println("\nWartosc update id_nawozu ="+Integer.toString(dbHelper.getIdNawozbyNameNawoz_tabelaNawozy(nazwa_oprysku,idPola))+" idpola"+idPola);
                    String [] args= { id_oprysk,Integer.toString(idPola)};
                    long idd = db.update("opryski", val1,"id_oprysk=? AND id_pola=?",args);

                    db.close();


                    Intent i = new Intent(getApplicationContext(), ZestawieniePryskanieAktywnosc.class);
                    startActivity(i);
                    finish();
                } else {

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
        TimePickerDialog timePickerDialog = new TimePickerDialog(EdytujPryskanie.this, EdytujPryskanie.this, mHour, mMinute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        datatv.setText(mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute);
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