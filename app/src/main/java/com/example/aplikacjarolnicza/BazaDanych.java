package com.example.aplikacjarolnicza;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BazaDanych extends SQLiteOpenHelper{
    private SQLiteDatabase mDatabase;
    // nazwa bazy:
    private static String DBNAME= "gospodarstwo.db";
    private static int DBVER=1; // wersja bazy

    // nazwa tabeli :
    public static final String TABEL_NAME="lokalizacja";


    // nazwa pol tabeli :
    public static final String ID= "id_lokalizacja";
    public static final String LAT="szerokosc_geo";
    public static final String LNG="dlugosc_geo";
    public static final String ID_POLA="id_pola";
    public static final String KOLEJNOSC="kolejnosc";
    public static final String NAZWA_pola="nazwa_pola";
    public static final String ID_OBSZAR="id_obszar";


    // instancja bazy :
    private SQLiteDatabase mDB;
    private Context mContext;



    public BazaDanych(Context context) {
        super(context,DBNAME,null,DBVER);
        this.mContext = context;
        this.mDB=getWritableDatabase();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+TABEL_NAME+ "("+ID+" integer primary key autoincrement, "+ LAT+" REAL , "+LNG+" REAL , "+ID_POLA+" integer ,"+ID_OBSZAR+" integer, "+KOLEJNOSC+" integer, "+NAZWA_pola+" TEXT"+" )";
        db.execSQL(sql);
        String sql2= "create table pole (id_pola integer primary key autoincrement, nazwa TEXT, nr_ewidencyjny TEXT, powierzchnia FLOAT, plan_azotowy FLOAT, klasa_gleby TEXT,id_user integer,punkty_na_mapie boolean, posiadanie boolean)";
        db.execSQL(sql2);
        sql2="create table obszar (id_obszar integer primary key autoincrement, nazwa TEXT, id_pola integer, id_user integer)";
        db.execSQL(sql2);
        sql2= "create table nawozy (id_nawoz integer primary key autoincrement, nazwa_nawozu TEXT, dawka_litry boolean,dawka_kg boolean,dawka FLOAT, substancja TEXT, zawartosc_procentowa FLOAT, id_user integer, id_pola integer, data_wykonania DATETIME)";
        db.execSQL(sql2);
        sql2= "create table opryski (id_oprysk integer primary key autoincrement, nazwa_oprysku TEXT, dawka_litry boolean,dawka_kg boolean,dawka FLOAT, rodzaj_srodka TEXT, cel_stosowania TEXT, id_user integer, id_pola integer, data_wykonania DATETIME)";
        db.execSQL(sql2);
        sql2= "create table notatki (id_notatka integer primary key autoincrement, tresc TEXT,  id_user integer, id_pola integer, data_wykonania DATETIME)";
        db.execSQL(sql2);
        sql2= "create table zbior (id_zbior integer primary key autoincrement, nazwa_rosliny TEXT,plon_w_sztukach boolean,plon_w_tonach boolean, plon FLOAT, id_user integer, id_pola integer, data_wykonania DATETIME)";
        db.execSQL(sql2);

        sql2= "create table uprawa (id_uprawa integer primary key autoincrement, nazwa_czynnosci TEXT, uwagi TEXT, id_user integer, id_pola integer, data_wykonania DATETIME)";
        db.execSQL(sql2);

        sql2= "create table zagrozenie (id_zagrozenie integer primary key autoincrement, nazwa_zagrozenia TEXT, id_user integer, id_pola integer, data_wykonania DATETIME)";
        db.execSQL(sql2);




    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

public boolean sprawdzPosiadanie_tabelapole(int id_pola){
        boolean sprawdz=true;
    SQLiteDatabase db = this.getWritableDatabase();
    String sql="SELECT id_pola, nazwa, posiadanie FROM pole WHERE posiadanie=1 AND id_pola="+id_pola;
       // String[] nazwaWarunku = {Integer.toString(id_pola)};

   // String sql="SELECT  * FROM pole WHERE posiadanie=1";

    Cursor c = db.rawQuery(sql,null);


    while (c.moveToNext()){
        System.out.println("\n Kursor="+c.getInt(c.getColumnIndexOrThrow("id_pola"))+ " posiadanie="+c.getString(c.getColumnIndexOrThrow("posiadanie"))+" przekazaneID="+id_pola);



    }
    if(c.getCount()>0){sprawdz=true;
        System.out.println("\nKursor pole istnieje");
    }
    else sprawdz=false;
    c.close();
    db.close();
    System.out.println("\nCzy pole istnieje= "+sprawdz);

return sprawdz;}


public ArrayList<HashMap<String, String>> PobierzListeNawozenia(){

    SQLiteDatabase db = this.getWritableDatabase();

    ArrayList<String> listaPol = new ArrayList<String>();
    listaPol= this.odczytajNazwy();
    System.out.println("\nLiczb pol w zestawieniuN= "+listaPol.size());



    ArrayList<HashMap<String, String>> polaNawoz_lista= new ArrayList<>();

    String sql= "SELECT * FROM nawozy";
    // WYSWIETLA DOBRZE TE CO MAJA NAWOZENIE
    // teraz trzeba dodac wpisy dla tych co nie mają!!
    Cursor c = db.rawQuery(sql,null);
    while(c.moveToNext()){
        System.out.println("\nJakie pola maja nawoz: "+c.getInt(c.getColumnIndex("id_pola")));
        HashMap<String, String> nazwy = new HashMap<>();

            if (c.getInt(c.getColumnIndex("id_pola")) ==getIDbyName_tabelaPole(getNazwaByID(c.getInt(c.getColumnIndex("id_pola")))) && this.sprawdzPosiadanie_tabelapole(c.getInt(c.getColumnIndexOrThrow("id_pola"))) )
            {

            nazwy.put("nazwa_pola", getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))));
            nazwy.put("nazwa_nawozu",c.getString(c.getColumnIndex("nazwa_nawozu")));
            nazwy.put("id_nawoz",c.getString(c.getColumnIndexOrThrow("id_nawoz")));

            if(c.getString(c.getColumnIndexOrThrow("dawka_kg")).equals("1")){
                nazwy.put("dawka",Float.toString(c.getFloat(c.getColumnIndexOrThrow("dawka")))+"kg/ha");


            } else  nazwy.put("dawka",Float.toString(c.getFloat(c.getColumnIndexOrThrow("dawka")))+"l/ha");

             nazwy.put("data_wykonania",c.getString(c.getColumnIndexOrThrow("data_wykonania")));

                polaNawoz_lista.add(nazwy);

            } else if(this.sprawdzPosiadanie_tabelapole(c.getInt(c.getInt(c.getColumnIndex("id_pola"))))==true ) { // jesli nie ma nic zadnego nawozenia:
                nazwy.put("nazwa_pola", getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))));
                nazwy.put("nazwa_nawozu","brak");
                nazwy.put("dawka","brak");
                nazwy.put("data_wykonania","brak");
                nazwy.put("id_nawoz","brak");
                polaNawoz_lista.add(nazwy);
            }


    }



return polaNawoz_lista;
}

    public ArrayList<HashMap<String, String>> PobierzListedoSzacowania(){

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> listaPol = new ArrayList<String>();
        listaPol= this.odczytajNazwy();
        System.out.println("\nLiczb pol w zestawieniuN= "+listaPol.size());



        ArrayList<HashMap<String, String>> polaNawoz_lista= new ArrayList<>();


        // !! musze wyswietlac z tabeli pola      (chyba) zamiast z nawozy
        String sql= "SELECT * FROM pole where posiadanie=1";

        Cursor c = db.rawQuery(sql,null);
        int idpola;
        while(c.moveToNext()){
            idpola=c.getInt(c.getColumnIndex("id_pola"));
            System.out.println("\nJakie pola maja nawoz: "+c.getInt(c.getColumnIndex("id_pola")));
            HashMap<String, String> nazwy = new HashMap<>();

            if (c.getInt(c.getColumnIndex("id_pola")) ==getIDbyName_tabelaPole(getNazwaByID(c.getInt(c.getColumnIndex("id_pola")))) )
            {

                nazwy.put("nazwa_pola", getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))));
                nazwy.put("plan",Double.toString(pobierzPlanAzot(c.getInt(c.getColumnIndexOrThrow("id_pola")),c.getInt(c.getColumnIndexOrThrow("id_user")))));
                nazwy.put("sumaAzotu",Double.toString(obliczAzot(1,idpola)));
                System.out.println("\nSumaAzotu fun pobierz liste do szacowania"+Double.toString(obliczAzot(1,idpola)));
                if(obliczAzot(c.getInt(c.getColumnIndexOrThrow("id_user")),c.getInt(c.getColumnIndexOrThrow("id_pola"))) < pobierzPlanAzot(c.getInt(c.getColumnIndexOrThrow("id_pola")),c.getInt(c.getColumnIndexOrThrow("id_user"))))
                {
                    nazwy.put("sprawdz","PLANOK");
                }else    nazwy.put("sprawdz","PLANNOT");


            } else { // jesli nie ma nic zadnego nawozenia:
                nazwy.put("nazwa_pola", getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))));
                nazwy.put("plan","brak");
                nazwy.put("sumaAzotu","brak");
            }
            polaNawoz_lista.add(nazwy);

        }



        return polaNawoz_lista;
    }














    public ArrayList<HashMap<String, String>> PobierzListeOpryskow(){

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> listaPol = new ArrayList<String>();
        listaPol= this.odczytajNazwy();
        System.out.println("\nLiczb pol w zestawieniuN= "+listaPol.size());



        ArrayList<HashMap<String, String>> polaOpryski_lista= new ArrayList<>();

        String sql= "SELECT * FROM opryski";
        // WYSWIETLA DOBRZE TE CO MAJA opryski
        // teraz trzeba dodac wpisy dla tych co nie mają!!
        Cursor c = db.rawQuery(sql,null);
        while(c.moveToNext()){
            System.out.println("\nJakie pola maja oprysk: "+c.getInt(c.getColumnIndex("id_pola")));
            HashMap<String, String> nazwy = new HashMap<>();

            if (c.getInt(c.getColumnIndex("id_pola")) ==getIDbyName_tabelaPole(getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))))  && this.sprawdzPosiadanie_tabelapole(c.getInt(c.getColumnIndexOrThrow("id_pola"))) )
            {

                nazwy.put("nazwa_pola", getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))));
                nazwy.put("nazwa_oprysku",c.getString(c.getColumnIndex("nazwa_oprysku")));
                nazwy.put("id_oprysk",c.getString(c.getColumnIndexOrThrow("id_oprysk")));

                if(c.getString(c.getColumnIndexOrThrow("dawka_kg")).equals("1")){
                    nazwy.put("dawka",Float.toString(c.getFloat(c.getColumnIndexOrThrow("dawka")))+"kg/ha");


                } else  nazwy.put("dawka",Float.toString(c.getFloat(c.getColumnIndexOrThrow("dawka")))+"l/ha");

                nazwy.put("data_wykonania",c.getString(c.getColumnIndexOrThrow("data_wykonania")));

                polaOpryski_lista.add(nazwy);

            } else if(this.sprawdzPosiadanie_tabelapole(c.getInt(c.getColumnIndexOrThrow("id_pola"))) ) { // jesli nie ma nic zadnego nawozenia:
                nazwy.put("nazwa_pola", getNazwaByID(c.getInt(c.getColumnIndex("id_pola"))));
                nazwy.put("nazwa_oprysku","brak");
                nazwy.put("dawka","brak");
                nazwy.put("data_wykonania","brak");
                //nazwy.put("id_oprysk",c.getString(c.getColumnIndexOrThrow("id_oprysk")));
                polaOpryski_lista.add(nazwy);
            }


        }



        return polaOpryski_lista;
    }
























    public String getNazwaByID(Integer id){

        String nazwa="";
        try{
            ;

            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nazwa"};

            boolean dawka_kg;
            String warunek = "id_pola = "+id;
            //int[] nazwaWarunku = {idpola};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "pole",
                    kolumny,
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

               nazwa=cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));



            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }




        return nazwa; }






    public double getSumaAzotByid(int idpola){

       double suma=0;
        try{
            ;

            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nazwa_nawozu, id_pola, dawka, zawartosc_procentowa "};

            boolean dawka_kg;
            String warunek = "id_pola = "+idpola;
            //int[] nazwaWarunku = {idpola};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "nawozy",
                    kolumny,
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                if (cursor.getInt(cursor.getColumnIndexOrThrow("id_pola")) == idpola)
                {
                   suma+=cursor.getDouble(cursor.getColumnIndexOrThrow("dawka"))* cursor.getDouble(cursor.getColumnIndexOrThrow("zawartosc_procentowa"))/100.0;

                }

            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }




        return suma; }


    public String getNawozenieBy_IDpola(int idpola){

        String tekst="";   // zmienna do której będą zapisywane dane
        try{

         // połączenie z bazą danych  opcja odczytu
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //definicja kolumn, które mają być pobrane
            String kolumny[] = {"nazwa_nawozu, dawka_litry, dawka_kg, dawka, data_wykonania "};

            boolean dawka_kg;
            String warunek = "id_pola = "+idpola;

   // użycie kursora do realizacji polecenia sql
            Cursor cursor = db.query(
                    "nawozy", // nazwa tabeli
                    kolumny, // kolumny
                    warunek,// warunek,
                    null,
                    null,
                    null,
                    null
            );
            // jeśli istnieją wiersze danych
            while (cursor.moveToNext()){
          // sprawdzenie czy oprysk był w litrach
                if (cursor.getInt(cursor.getColumnIndexOrThrow("dawka_kg")) == 1)
                {    // zapisanie do zmiennej danych, kursor wyciąga dane
                    tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_nawozu"))+" "
                            + cursor.getFloat(cursor.getColumnIndexOrThrow("dawka"))
                            +" kg/ha\n"+"data: "
                            + cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"))+"\n";
                    // zapisanie do zmiennej danych, kursor wyciąga dane
                } else  tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_nawozu"))+ " "
                        + cursor.getFloat(cursor.getColumnIndexOrThrow("dawka"))+" l/ha\n"+"data: "
                        + cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"))+"\n";

            } cursor.close(); //zakończenie połączenia
            db.close();


        } catch (SQLException e) { // jeśli błąd
            System.out.println("Błąd pobierania nawozenia");
        }

 // zwrócenie nawożenia
   return tekst; }


// Pobranie z bazy informacji o opyskach


    public String getOpryskiBy_IDpola(int idpola){
        String tekst=""; // zmienna będie zawierała dane oprysków
        try{
             // połączenie z bazą
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            // określenie danych, które mają być pobrane za pomocą polecenia
            String kolumny[] = {"nazwa_oprysku, dawka_litry, dawka_kg," +
                    " dawka, cel_stosowania, data_wykonania"};
            boolean dawka_kg;
            // określenie warunku polecenia sql
            String warunek = "id_pola = "+idpola;
            // wykonanie polecenia sql za pomocą cursora
            Cursor cursor = db.query(
                    "opryski", // nazwa tabeli
                    kolumny, // kolumny
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );
            // cursor zaczyna od -1, moveToNext() zacznie właściwie
            while (cursor.moveToNext()){
                  // sprawdzenie czy dawka była w kg
                if (cursor.getInt(cursor.getColumnIndexOrThrow("dawka_kg")) == 1)
                {  // pobranie danych
                    tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_oprysku"))+" "
                            + cursor.getFloat(cursor.getColumnIndexOrThrow("dawka"))+" kg/ha \nCel: "
                            +cursor.getString(cursor.getColumnIndexOrThrow("cel_stosowania"))+"\n"+"data: "
                            + cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"))+"\n";
               // pobranie danych za pomoca cursora
                } else  tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_oprysku"))
                        + " "+ cursor.getFloat(cursor.getColumnIndexOrThrow("dawka"))+" l/ha \nCel: "
                        +cursor.getString(cursor.getColumnIndexOrThrow("cel_stosowania"))+"\n"+"data: "
                        + cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"))+"\n";
             } cursor.close(); // zwolnienie cursora
            // zamknięcie połączenia z baza danych
                   db.close();
                } catch (SQLException e) { System.out.println("Błąd pobierania danych opryskiwania"); }
                         // zwrócenie danych
                               return tekst; }





/// Pobranie zbioru z bazy danych



    public boolean czyIstniejeNazwaPola(int id_user,String name){
      boolean sprawdz ;
      ArrayList<String> lista = new ArrayList<>();
        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_user", "nazwa"};
            String warunek = "id_user = "+id_user;
            Cursor cursor = db.query(
                    "pole", kolumny,
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()){
         lista.add(cursor.getString(cursor.
                 getColumnIndexOrThrow("nazwa")));
            } cursor.close();
            db.close(); // zwolnienie kursora
        } catch (SQLException e) {
            System.out.println("Bład sprawdzania nazwy pola");
        }
        if(lista.contains(name)){ sprawdz=true;
        } else sprawdz=false;
        return sprawdz; }



    public String getZbiorBy_IDpola(int idpola){

        String tekst="";
        try{


            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nazwa_rosliny, plon_w_sztukach, plon_w_tonach, plon, data_wykonania"};

            boolean plonTony;
            String warunek = "id_pola = "+idpola;
            //int[] nazwaWarunku = {idpola};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "zbior",
                    kolumny,
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                if (cursor.getInt(cursor.getColumnIndexOrThrow("plon_w_tonach")) == 1)
                {
                    tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_rosliny"))+" \nPlon: "+ cursor.getFloat(cursor.getColumnIndexOrThrow("plon"))+" ton/ha "+"\ndata: "+ cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"));

                } else  tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_rosliny"))+" \nPlon: "+ cursor.getFloat(cursor.getColumnIndexOrThrow("plon"))+" sztuk/ha "+"\ndata: "+ cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"));


                //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
                //listaPol.add(name);


            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }




        return tekst; }


// pobranie Notatki z bazy danych


    public String getNotatkaBy_IDpola(int idpola){

        String tekst="";
        try{


            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"tresc, data_wykonania"};


            String warunek = "id_pola = "+idpola;
            //int[] nazwaWarunku = {idpola};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "notatki",
                    kolumny,
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){


                    tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("tresc"))+" \ndata: "+ cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"))+"\n";

                System.out.println("\n NOTATKA "+tekst);

                //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
                //listaPol.add(name);









            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }




        return tekst; }




        // Pobranie danych z tabeli uprawa


    public String getUprawaBy_IDpola(int idpola){

        String tekst="";
        try{


            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nazwa_czynnosci, uwagi, data_wykonania"};


            String warunek = "id_pola = "+idpola;
            //int[] nazwaWarunku = {idpola};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "uprawa",
                    kolumny,
                    warunek,// warunek,
                    null, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){


                tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_czynnosci"))+" Uwagi: "+cursor.getString(cursor.getColumnIndexOrThrow("uwagi"))+ " \ndata: "+ cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"));



                //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
                //listaPol.add(name);









            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }




        return tekst; }


// Pobranie  danych z tabeli zagrozenie
public String getZagrozenieBy_IDpola(int idpola){

    String tekst="";
    try{


        final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String kolumny[] = {"nazwa_zagrozenia, data_wykonania"};


        String warunek = "id_pola = "+idpola;
        //int[] nazwaWarunku = {idpola};
        //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

        Cursor cursor = db.query(
                "zagrozenie",
                kolumny,
                warunek,// warunek,
                null, //nazwaWarunku,
                null,
                null,
                null
        );


        while (cursor.moveToNext()){


            tekst+="\n"+cursor.getString(cursor.getColumnIndexOrThrow("nazwa_zagrozenia"))+ " \ndata: "+ cursor.getString(cursor.getColumnIndexOrThrow("data_wykonania"))+"\n";



            //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
            //listaPol.add(name);









        } cursor.close();


        db.close();


    } catch (SQLException e) {

    }




    return tekst; }



/////////////////////////////////////////

    public int liczbaObszarow(String name){
int liczba=0;
        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_obszar"};

            String warunek = "nazwa = ?";
            String[] nazwaWarunku = {name};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "pole",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

               liczba=cursor.getInt(cursor.getColumnIndexOrThrow("id_obszar"));
                //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
                //listaPol.add(name);
                System.out.println("\n----Powierzchnia ==== "+liczba);







            } cursor.close();





        } catch (SQLException e) {

        }



        return liczba;


    }



public int LiczbaPol(){
        int liczba; // zmienna do przypisania liczby pól
    // utworzenie pomocnika do połączenia z baza danych:
    final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    // przypisanie zapytania SQL do zmiennej String:
    String sql="SELECT  * FROM pole WHERE posiadanie=1";
    // posiadanie =1 : użytkownik posiada pole
    // posiadanie =0 : użytkownik nie posiada pola

    // utworzenie kursora z którego pobierzemy liczbę rekordow
    Cursor cursor = db.rawQuery(sql, null);
    // przypisanie do zmiennej liczba liczbę wierszy w kursorze
    liczba = cursor.getCount();
    cursor.close(); // zamknięcie kursora
    db.close();   // zamknięcie połączenia z baza danych

        return liczba;  //  zwrócenie liczby pól
}



public int getIDbyName_tabelaPole(String name){
 int idd=0;

    try{
        final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String kolumny[] = {"id_pola"};

        String warunek = "nazwa = ?";
        String[] nazwaWarunku = {name};
        //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

        Cursor cursor = db.query(
                "pole",
                kolumny,
                warunek,// warunek,
                nazwaWarunku, //nazwaWarunku,
                null,
                null,
                null
        );


        while (cursor.moveToNext()){

            idd=cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"));
            //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
            //listaPol.add(name);
            System.out.println("\n----idPola ==== "+idd);



        } cursor.close();


db.close();


    } catch (SQLException e) {

    }



    return idd;

}



    public int getIdNawozbyNameNawoz_tabelaNawozy(String name, int id_pola){
        int idd=0;
        int idSzukane=0;

        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_nawoz, id_pola"};

            String warunek = "nazwa_nawozu = ?";
            String[] nazwaWarunku = {name};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "nawozy",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                idd=cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"));
                if(idd==id_pola){
                    idSzukane= cursor.getInt(cursor.getColumnIndexOrThrow("id_nawoz"));
                }


            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }



        return idSzukane;

    }



    public String getSubstancjaNawozbyNameNawoz_tabelaNawozy(String name, int id_pola){
        String substancja="";

int idd;
        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_nawoz, id_pola", "substancja"};

            String warunek = "nazwa_nawozu = ?";
            String[] nazwaWarunku = {name};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "nawozy",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                idd=cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"));
                if(idd==id_pola){
                   substancja=cursor.getString(cursor.getColumnIndexOrThrow("substancja"));
                }


            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }



        return substancja;

    }


    public Float getZawartoscbyNameNawoz_tabelaNawozy(String name, int id_pola){
       float zawartosc=0;

        int idd;
        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_nawoz, id_pola", "zawartosc_procentowa"};

            String warunek = "nazwa_nawozu = ?";
            String[] nazwaWarunku = {name};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "nawozy",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                idd=cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"));
                if(idd==id_pola){
                    zawartosc=cursor.getFloat(cursor.getColumnIndexOrThrow("zawartosc_procentowa"));
                }


            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }



        return zawartosc;

    }





    public int getIdByNameOprysk_tabelaOprysk(String nazwaoprysku, int id_pola){
        int idd=0;
        int idSzukane=0;

        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_oprysk, id_pola"};

            String warunek = "nazwa_oprysku = ?";
            String[] nazwaWarunku = {nazwaoprysku};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "opryski",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                idd=cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"));
                if(idd==id_pola){
                    idSzukane= cursor.getInt(cursor.getColumnIndexOrThrow("id_oprysk"));
                }




            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }



        return idSzukane;

    }

    public String getPrzyczynabyByNazwaOprysku_tabelaOprysk(String nazwaoprysku, int id_pola){
        int idd=0;
        String przyczyna=" ";

        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"id_oprysk, id_pola, cel_stosowania"};

            String warunek = "nazwa_oprysku = ?";
            String[] nazwaWarunku = {nazwaoprysku};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "opryski",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                idd=cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"));
                if(idd==id_pola){
                    przyczyna= cursor.getString(cursor.getColumnIndexOrThrow("cel_stosowania"));
                }




            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }



        return przyczyna;

    }








public float getPowierzchniaByName(String name){

        float powierzchnia=0; // zmienna do zapisania powierzchni
        try{
            // połączenie z baza opcja odczytu
    final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    // zdefiniowanie kolumny, która chcemy pobrac
    String kolumny[] = {"powierzchnia"};

    // określenie warunków
    String warunek = "nazwa = ?";
     String[] nazwaWarunku = {name};
    //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";
    // wykonanie polecenia z użyciem kursora
    Cursor cursor = db.query(
            "pole",
            kolumny,
            warunek,// warunek,
            nazwaWarunku, //nazwaWarunku,
            null,
            null,
            null
    );


    while (cursor.moveToNext()){
             // wyciągniecie danych przy użyciu kursora
          powierzchnia=cursor.getFloat(cursor.
                  getColumnIndexOrThrow("powierzchnia"));

    }
    // zamknięcie połączenia
    cursor.close();   db.close();

 } catch (SQLException e) {
            System.out.println("Błąd pobieranai powierzchni pola");
    }
 // zwrócenie powierzchni pola
  return powierzchnia;}




    public String getNrEwidencyjnyByName(String name){

       String nr=" ";
        try{
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nr_ewidencyjny"};

            String warunek = "nazwa = ?";
            String[] nazwaWarunku = {name};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "pole",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

               nr= cursor.getString(cursor.getColumnIndexOrThrow("nr_ewidencyjny"));
                //String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));


            } cursor.close();


            db.close();


        } catch (SQLException e) {

        }



        return nr;}




    public ArrayList<String> odczytajNazwy(){
  // definiowanie arraylisty, w której będą zapisywane nazwy pol
         ArrayList<String> listaPol = new ArrayList<String>();
        try {

            //  SQLiteOpenHelper dbHelper;
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            // połączenie z baza danych
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            // zdefiniowanie kolumn które mają zostać pobrane
            String kolumny[] = {"nazwa"};
          // okreslenie warunku, ktory oznacza, aby pobrac te pola ktre
            // sa w posiadaniu
            String warunek =  "posiadanie =?";
             String[] nazwaWarunku = {"1"};
             // kursor umożliwia przeglądanie zwróconych wyników
            // zapytania sql
            Cursor cursor = db.query(
                    "pole",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){
         // w momencie gdy kursor przenoszony jest do nowego wiersza
                // wyników zwroconych przez zapytanie sql

       // pobierania jest nazwa pola przy uzyciu kursora i zapisana jest w zmiennej name
                String name= cursor.getString(cursor.getColumnIndexOrThrow("nazwa"));
                // dodanie zmiennej name do listy
                listaPol.add(name);
              //  System.out.println("\n----Nazwy pol baza.class ==== "+name);

            }
            // zamknięcie połącenia
            cursor.close();
            db.close();

        } catch (SQLException e) {
            System.out.println("Exception OdczytajNazwy()"); }

// zwracana jest lista pól
        return listaPol; }


    public List<LatLng> punktyPola(String name, int id_pola){
        // deklaracja listy dla lokalizacji
        List<LatLng> lista = new ArrayList<LatLng>();

    try { // połączenei z baza danych
        final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // określenie kolumn do wyświetlenia
        String kolumny[] = {BazaDanych.LAT, BazaDanych.LNG,"nazwa_pola","id_pola"};

        // określenie warunku pobrania danych
        String warunek = "nazwa_pola "+ "=? ";
        // warunkiem jest nazwa pola
        String[] nazwaWarunku={name};
        // wykonanie polecenia sql za pomocą cursora
        Cursor cursor = db.query(
                "lokalizacja",// nazwa tabeli
                kolumny,// kolumny
                warunek,// warunek
                nazwaWarunku,
                null,
                null,
                null
        );

        // wyciągnięcie danych za pomocą cursora
        while (cursor.moveToNext()) {
           // wyciągnięcie danych o długości i szerokości geograficznej
                double szer = cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LAT));
            double dl = cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LNG));

          // tworzenei obiektu LatLng z lokalizacją
            LatLng d = new LatLng(szer, dl);
            // dodanie lokalizacji do listy
            lista.add(d);
        } cursor.close(); // zwolnienie cursora
        db.close();// zamknięcie połączenia z bazą

    } catch (SQLException e) {
        System.out.println("\nError, funkcja PunktyPola()..");
    }
      // zwrócenie listy z lokalizacją obszaru
     return  lista;}




    public ArrayList<String> odczytajNazwyzPunktami(int id_user){

        ArrayList<String> listaPol = new ArrayList<String>();
        try {


            //  SQLiteOpenHelper dbHelper;
            final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {"nazwa","id_pola","id_user","punkty_na_mapie"};

            String warunek = "punkty_na_mapie" + " = ? AND posiadanie =?";
             String[] nazwaWarunku = {"1","1"};
            //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                    "pole",
                    kolumny,
                    warunek,// warunek,
                    nazwaWarunku, //nazwaWarunku,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

if(cursor.getInt(cursor.getColumnIndexOrThrow("id_user"))==id_user) {

    listaPol.add(cursor.getString(cursor.getColumnIndexOrThrow("nazwa")));
    //  System.out.println("\n----Nazwy pol baza.class ==== "+name);
}

            } cursor.close();
            System.out.println("\nPola: "+listaPol);

            db.close();





        } catch (SQLException e) {

        }






        return listaPol; }


        public double pobierzPlanAzot(int id_pola, int id_user){

        double plan=0;


            try {


                //  SQLiteOpenHelper dbHelper;
                final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String kolumny[] = {"nazwa","id_pola","id_user","plan_azotowy"};

                String warunek = "id_pola" + " = ?";
                String[] nazwaWarunku = {Integer.toString(id_pola)};
                //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

                Cursor cursor = db.query(
                        "pole",
                        kolumny,
                        warunek,// warunek,
                        nazwaWarunku, //nazwaWarunku,
                        null,
                        null,
                        null
                );


                while (cursor.moveToNext()){

                    if(cursor.getInt(cursor.getColumnIndexOrThrow("id_user"))==id_user) {

                        plan=cursor.getDouble(cursor.getColumnIndexOrThrow("plan_azotowy"));
                        System.out.println("\nPlan dla pola id "+cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"))+" = "+plan );
                    }

                } cursor.close();
                System.out.println("\nPLAN n: "+plan);

                db.close();





            } catch (SQLException e) {

            }






            return plan;}

            public double obliczAzot(int id_user, int id_pola){

        double azot=0;

                System.out.println("\n Jestem w metodzie obliczAzot()");

                try {


                    //  SQLiteOpenHelper dbHelper;
                    final SQLiteOpenHelper dbHelper = new BazaDanych(mContext);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String kolumny[] = {"dawka","substancja","zawartosc_procentowa","id_pola","id_user"};

                    String warunek = "substancja = ?";
                    String[] nazwaWarunku = {"Azot"};
                    //String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

                    Cursor cursor = db.query(
                            "nawozy",
                            kolumny,
                            warunek,// warunek,
                            nazwaWarunku, //nazwaWarunku,
                            null,
                            null,
                            null
                    );


                    while (cursor.moveToNext()){

                        if(cursor.getInt(cursor.getColumnIndexOrThrow("id_user"))==id_user && cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"))== id_pola) {

                            azot+=(cursor.getDouble(cursor.getColumnIndexOrThrow("zawartosc_procentowa"))/100.0) *cursor.getDouble(cursor.getColumnIndexOrThrow("dawka"));
                            System.out.println("\nAzot suma pol "+cursor.getInt(cursor.getColumnIndexOrThrow("id_pola"))+" = "+azot);
                        }

                    } cursor.close();


                    db.close();





                } catch (SQLException e) {

                }


                System.out.println("\n Ostatecznie zastosowany azot = "+azot);
                return azot;}


}
