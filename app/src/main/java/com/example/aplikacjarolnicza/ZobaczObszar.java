package com.example.aplikacjarolnicza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.app.AlertDialog;
import android.Manifest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;




import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ZobaczObszar extends FragmentActivity implements OnMapReadyCallback{
    public static final  String NAZWA_POLA="nazwaPola";
     String nazwaPola;
    private BazaDanych dbHelper;

    GoogleMap gMap;

    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>(); //  Lista przeznaczona na przechowywanie lokalizacji punktów na mapie


    Double lat, lng;


    private static final int REQUEST_CODE = 101;




    int liczba_punktow;


    List <LatLng> lokalizaja = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zobacz_obszar);


        liczba_punktow=0;
        nazwaPola= getIntent().getStringExtra(NAZWA_POLA);
        // DOSTEP DO BAZY:
     dbHelper = new BazaDanych(this);
        ((TextView)findViewById(R.id.nazwaPolaView)).setText("Nazwa pola:"+nazwaPola);
        // powierzchnia.setText("Powierzchnia: "+Float.toString(mDBHelper.getPowierzchniaByName(nazwaP))+" ha");
        ((TextView)findViewById(R.id.powierzchniaView)).setText("Powierzchnia: "+Float.toString(dbHelper.getPowierzchniaByName(nazwaPola))+"ha");


        System.out.println("\nRozmiar ListylatLang= "+latLngList.size());
        // inicjalizacja SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
// odczyt listy punktow


        lokalizaja.clear();
        latLngList.clear();

        try {

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String kolumny[] = {BazaDanych.LAT, BazaDanych.LNG, BazaDanych.NAZWA_pola};


            String warunek = BazaDanych.ID_POLA + " = "+dbHelper.getIDbyName_tabelaPole(nazwaPola);
            //int [] nazwaWarunku = {dbHelper.getIDbyName_tabelaPole(nazwaPola);}
           // String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

            Cursor cursor = db.query(
                   "lokalizacja",
                    kolumny,
                    warunek,
                    null,
                    null,
                    null,
                    null
            );


            while (cursor.moveToNext()){

                double szer= cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LAT));
                double dl = cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LNG)) ;
lat=szer;
lng=dl;




                LatLng d = new LatLng(szer,dl);
                System.out.println("\nLat lng= "+d);
                lokalizaja.add(d);
                System.out.println("\nLista= "+lokalizaja);




            } cursor.close();
            System.out.println("\n Liczba pkt w petli"+lokalizaja.size());

            db.close();
           /*
            // tworzenie linii (połączeń) na podstawie listy wspołrzędnych
            PolygonOptions polygonOptions = new PolygonOptions().addAll(lokalizaja).clickable(true);
            polygon = gMap.addPolygon(polygonOptions);

            // ustawienie koloru linii
            polygon.setStrokeColor(Color.rgb(red, green, blue));

*/


        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(),"EXCEPTION:SET",
                    Toast.LENGTH_LONG).show();
        }





    } // koniec onCreate









    @Override
    public void onMapReady(GoogleMap googleMap) {
        // podstawowe ustwienia dla mapy tj. widoczny kompas,
        // //możliwość powiększania pomniejszania, rodzaj mapy.
        gMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true); uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true); uiSettings.setMapToolbarEnabled(false);
       // określenie rodzaju mapy: Hybrid
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // jeśli lista z punktami ma rozmiar <0 oznacza ze nie ma przypisanego obszaru
     if(lokalizaja.size()!=0) {
         // ustawienie kamery na pierwszy punkt z listy
         CameraUpdate center = CameraUpdateFactory.newLatLng(lokalizaja.get(1));
         // przybliżenie
         CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
         // określenie widoku
         googleMap.moveCamera(center);
         googleMap.animateCamera(zoom);
           // tworzenie opcji znacznika dla jednego punktu lokalizacaji
         MarkerOptions markeroptions = new MarkerOptions().position(lokalizaja.get(1)).draggable(false).
                 icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).
                 anchor(0.5f, 0.5f).title(nazwaPola);
         Marker marker = gMap.addMarker(markeroptions);// dodanie znacznika na mapie
         if (polygon != null) polygon.remove();   // na wszelki wypadek usunięcie poprzednich lini
         // tworzenie obszaru dla działki rolnej zapisanej w bazie
         PolygonOptions polygonOptions = new PolygonOptions().addAll(lokalizaja).
                 clickable(true).fillColor(Color.BLUE);
          // dodanie obszaru na mape
         polygon = gMap.addPolygon(polygonOptions);

     } else {  // jeśli działka nie ma punktów wyświetla się komunikat

         Toast.makeText(getApplicationContext(),
                 "Nie przypisano działki na mapie",
                 Toast.LENGTH_LONG).show();
         // Wyświetlenie zapytania o dodanie punktów do bazy danych
         AlertDialogDodajObszar();
     }





        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               // liczba_punktow++;
                // tworzenie  ustawień/opcji dla znacznika (markera)  : lokalizacja(położenie),można przesuwać, ikona,kotwica
            //    MarkerOptions markeroptions = new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).anchor(0.5f,0.5f);

                // tworzenie znacznika
             //   Marker marker = gMap.addMarker(markeroptions);
             //   latLngList.add(latLng);    // dodanie współrzędnych do listy
              //  markerList.add(marker); // dodanie znacznika
              //  double lng = latLng.longitude;
              //  double ltd = latLng.latitude;
              //  System.out.println("\n***/*  MOJ lng="+lng+"\n*** Moj ltd="+ltd);




            }







        });
    }



    // z current
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   // fetchLastLocation();
                }break;
        }
    }



    public void AlertDialogDodajObszar() {
        // ładowanie buildera dialogu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lokalizacja pola"); // tytuł okna
        builder.setMessage("Czy chcesz zaznaczyć pole na mapie?");
        // dodanie przycisku Tak i jego obsługa
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // tworzenie obiektu intencji otwierającej nową aktywność
                Intent punkty = new Intent(getApplicationContext(),ZaznaczObszarDialog.class);
                // umieszczenie nazwy pola w intencji
                punkty.putExtra(NAZWA_POLA,nazwaPola);
                startActivity(punkty); // start aktywnosci dodania obszaru
                finish(); // koniec aktualnej aktywnosci
            }});
        // dodanie przycisku anuluj
        builder.setNegativeButton("Anuluj", null);
        // tworzenie dialogi u jego pokazanie
        AlertDialog dialog = builder.create();
        dialog.show();  }}
