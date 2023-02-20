package com.example.aplikacjarolnicza;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

public class ZaznaczObszar extends FragmentActivity implements OnMapReadyCallback {
    public static String RESULT_POINT="points";
    GoogleMap gMap;
    CheckBox checkBox;
    SeekBar seekRed, seekGreen, seekBlue;
    Button btDraw, btClear, btnOdczyt;
    Criteria criteria;
    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>(); //  Lista przeznaczona na przechowywanie lokalizacji punktów na mapie
    List<Marker> markerList = new ArrayList<>(); // lista przeznaczona na przechowywanie znaczników (markerów)
    public List <LatLng> punkty = new ArrayList<>(); // lista na punkty wielokata


    int red = 0, green = 0, blue = 0;
    private LocationManager locationManager;
    Double lat, lng;
    public List<Double> wspLat = new ArrayList<>(); //lista na wspolrzedne latitude
    public List<Double>wspLng = new ArrayList<>(); // lista na longitude
    // z projektu current location
    public   Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    public  String bestProvider;
    private static final int REQUEST_CODE = 101;

// finish


    // do bazy:
    String nazwaTabeli=null;
    Integer id_pola= null;
    Integer kolejnosc=null;
    Double szerokosc=null;
    Double dlugosc = null;
    int liczba_punktow;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zaznacz_obszar);

        // przypisanie zmiennych:
        //checkBox = findViewById(R.id.chceck_box);

        btClear = findViewById(R.id.bt_clear);
       // btDraw = findViewById(R.id.bt_draw);
        //btnOdczyt= findViewById(R.id.odczytBtn);
        liczba_punktow=0;

        // DOSTEP DO BAZY:
        final SQLiteOpenHelper dbHelper = new BazaDanych(this);

        latLngList.clear();
        System.out.println("\nRozmiar listylatLang= "+latLngList.size());



        // inicjalizacja SupportMapFragment  --> wazne tutaj nei mogą byc, sa w fetch location

        //     SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        // supportMapFragment.getMapAsync(this);

        // zaznaczenie/ odznaczenie checkboxa - reakcja na zmiany
   /*     checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {


                if (b) {
                    if (polygon == null) return;   // jeśli nie ma linii brak reakcji
                    // w przeciwnym wypadku wypełnienie
                    polygon.setFillColor(Color.rgb(red, green, blue));  // wypełnienienie obszaru kolorem
                } else {

                    //ustawienie tła jako transparentne - brak wypełnienia
                    polygon.setFillColor(Color.TRANSPARENT);
                }

            }
        });*/
        //create polygons ,, button zapisz
/*
        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (polygon != null) polygon.remove();

                // tworzenie linii (połączeń) na podstawie listy wspołrzędnych
                PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).clickable(true);
                polygon = gMap.addPolygon(polygonOptions);

                // ustawienie koloru linii
                polygon.setStrokeColor(Color.rgb(red, green, blue));
                if (checkBox.isChecked()) {   // jesli jest zaznaczona opcja wypełnienia, obszar będzie wypełniony
                    // w przeciwnym wypadku obszar ma tło transparentne

                    polygon.setFillColor(Color.rgb(red, green, blue));
                }

                String w;
                punkty=polygon.getPoints();
                w=punkty.get(0).toString();

                for(int i=0;i<liczba_punktow;i++) {
                    dlugosc = punkty.get(i).longitude;
                    szerokosc = punkty.get(i).latitude;
                    // zapis do bazy:
                    try {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values= new ContentValues();
                        values.put(BazaDanych.KOLEJNOSC,i+1);

                        values.put(BazaDanych.ID_POLA,001);
                        values.put(BazaDanych.LAT,szerokosc);
                        values.put(BazaDanych.LNG,dlugosc);

                        long id = db.insert(BazaDanych.TABEL_NAME,null,values);




                        db.close();
                    } catch (SQLException e) {
                        Toast.makeText(ZaznaczObszar.this,"EXCEPTION:SET",
                                Toast.LENGTH_LONG).show();



                    }


                } // koniec for()



                System.out.println("\nLista punktow latlng : "+latLngList);
                System.out.println("\nLista punktow : "+punkty);
                System.out.println("\nPierwszy indeks to = "+String.valueOf(w));

                // double dd= Double.parseDouble()
                //  System.out.println("\nA  w double = "+dd);

                liczba_punktow=0;
            }




        });     */ // koniec create polygon


        // przycisk odczyt btnOdczyt
        // potrzebne pozniej do wczytania obszaru gdy uzytkownik bedzie chcial sprawdzic

//********************************
        // BTN przekaz
        ((Button)findViewById(R.id.btnPrzekaz)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("\nLista przed"+latLngList);

                System.out.println("\nLista to string"+latLngList.toString());
                Intent backIntent= new Intent();
                //backIntent.putParcelableArrayListExtra("latLangList",ArrayList<> latLngList);
                backIntent.putExtra(RESULT_POINT,(Serializable)latLngList);
               setResult(10,backIntent);
               finish();



            }
        });




// btn ODCZYT
        /*
        btnOdczyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List <LatLng> lokalizaja = new ArrayList<>();
                lokalizaja.clear();

                try {

                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String kolumny[] = {BazaDanych.LAT, BazaDanych.LNG};

                    String warunek = BazaDanych.KOLEJNOSC + " = ?";
                    String[] nazwaWarunku = {"1"};
                    String sortowanie = BazaDanych.KOLEJNOSC + " ASC";

                    Cursor cursor = db.query(
                            BazaDanych.TABEL_NAME,
                            kolumny,
                            null,// warunek,
                            null, //nazwaWarunku,
                            null,
                            null,
                            sortowanie
                    );


                    while (cursor.moveToNext()){

                        double szer= cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LAT));
                        double dl = cursor.getDouble(cursor.getColumnIndexOrThrow(BazaDanych.LNG)) ;

                        LatLng d = new LatLng(szer,dl);
                        System.out.println("\nLat lng= "+d);
                        lokalizaja.add(d);
                        System.out.println("\nLista= "+lokalizaja);


//      TUTAJ SKONCZYLEM  ----------------------------

                        System.out.println("\n ** ODCZYT# ="+szer);
                        System.out.println("\n ** ODCZYT$ ="+dl);


                    } cursor.close();


                    db.close();





                } catch (SQLException e) {
                    Toast.makeText(ZaznaczObszar.this,"EXCEPTION:SET",
                            Toast.LENGTH_LONG).show();
                }


            }
        });

*/

// Obsługa przycisku POKAŻ
        ((Button)findViewById(R.id.btnRysuj)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (polygon != null)   // jeśli jakiś obszar jest aktualnie zaznaczony
                {
                    polygon.remove();  // usunięcie linii (połączeń miedzy punktami)
                }
                if(latLngList.size()<=0) {
                    Toast.makeText(getApplicationContext(), "Nie zaznaczono punktów..",
                            Toast.LENGTH_LONG).show();
                } else {
                    // załadowanie punktów z lokalizacją oraz ustawień dla polygon
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).
                            clickable(true).fillColor(Color.TRANSPARENT);
                    // utworzenie na mapie obszaru
                    polygon = gMap.addPolygon(polygonOptions);  }

            }
        });




        // kasowanie wprowadzonych punktów- znaczników
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear all
                if (polygon != null)   // jeśli jakiś obszar zaznaczony
                    polygon.remove();  // usunięcie linii (połączeń miedzy punktami)
                for (Marker marker : markerList) marker.remove();  // usunięcie znaczników
                latLngList.clear();
                markerList.clear();
               // checkBox.setChecked(false);   // odznaczenie checkboxa


            }


        });


        //current location

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        // current location end
    } // koniec onCreate




    private void fetchLastLocation() {
      // sprawdzenie i zapytanie czy są wymagane uprawnienia
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        System.out.println("\n *****  * Fetch dalej location2 " + task);
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                System.out.println("\n ******** JESTEM W  onSuccess");
                System.out.println(location);
                if (location != null) {
                    currentLocation = location;
                    System.out.println("\nNie jest nullem !!!!!!!!!!!!!!------------#####");
                    System.out.println("\nLAt   " + currentLocation.getLatitude() + "Lng " + currentLocation.getLongitude());
                    System.out.println("\nDokladnosc " + currentLocation.getAccuracy());
                    System.out.println("SPEEED " + currentLocation.getSpeed());
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(ZaznaczObszar.this);
                }
            }
        });




    }
    // koniec fetchLastlocation()





    @Override
    public void onMapReady(GoogleMap googleMap) {
        // podstawowe ustwienia dla mapy tj. widoczny kompas, możliwość powiększania pomniejszania, rodzaj mapy.
        gMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(false);


// z currentLocation
        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("moja");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


        // dodaje okrag wokol lokalizacji
        googleMap.addCircle(new CircleOptions().center(latLng).radius(16).strokeColor(Color.GREEN).fillColor(Color.argb(80,0,0,255)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));  // powiekszy
        // current location end



        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                liczba_punktow++;
                // tworzenie  ustawień/opcji dla znacznika (markera)  : lokalizacja(położenie),można przesuwać, ikona,kotwica
                MarkerOptions markeroptions = new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).anchor(0.5f,0.5f);

                // tworzenie znacznika
                Marker marker = gMap.addMarker(markeroptions);
                latLngList.add(latLng);    // dodanie współrzędnych do listy
                markerList.add(marker); // dodanie znacznika
                double lng = latLng.longitude;
                double ltd = latLng.latitude;
                System.out.println("\n***/*  MOJ lng="+lng+"\n*** Moj ltd="+ltd);




            }
        });
    }






    // z current
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }break;
        }
    }

}
