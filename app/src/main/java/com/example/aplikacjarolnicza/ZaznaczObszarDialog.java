package com.example.aplikacjarolnicza;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
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

public class ZaznaczObszarDialog extends FragmentActivity implements OnMapReadyCallback {
    public static String RESULT_POINT="points";
    public static final  String NAZWA_POLA="nazwaPola";
    GoogleMap gMap;

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


    private static final int REQUEST_CODE = 101;
private  BazaDanych baza;


    int liczba_punktow;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zaznacz_obszar);

        // przypisanie zmiennych:
        //checkBox = findViewById(R.id.chceck_box);

        btClear = findViewById(R.id.bt_clear);

        liczba_punktow=0;
        final String nazwaP= getIntent().getStringExtra("nazwaPola");
        // DOSTEP DO BAZY:
        final SQLiteOpenHelper dbHelper = new BazaDanych(this);
baza= new BazaDanych(this);
        latLngList.clear();
        System.out.println("\nRozmiar listylatLang= "+latLngList.size());

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



        // BTN przekaz    // ZAPISZ
        ((Button)findViewById(R.id.btnPrzekaz)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // połączenie z bazą danych
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Double szerokosc = null;
                Double dlugosc = null;

                  // sprawdzenei czy urzytkownik zaznaczył pkty
                if (latLngList.size() > 0) {
                    // pobranie idPola
                    int idPola= baza.getIDbyName_tabelaPole(nazwaP);
                    // tworzenie mapy: nazwa -> wartosc
                    ContentValues val1 = new ContentValues();
                     // ustawiamy flage oznaczajace ze punkty sa zaznaczone
                    val1.put("punkty_na_mapie", 1);


                  // definicja argumentow do warunku where
                  String[] args = new String[]{nazwaP,Integer.toString(idPola)};
                    // aktualizacja tabeli pole, w ktorej zmienia sie flage punkty_na_mapie
                    long idd = db.update("pole",val1,"nazwa=? AND id_pola=?",args);
                // mapa powiązań nazwa->wartosc dla tabeli obszar
                    ContentValues val2 = new ContentValues();
                    // nazwa pola
                    val2.put("nazwa", nazwaP);
                    val2.put("id_user", 1);
                    val2.put("id_pola", idPola);
                    // tworzenie rekordu w tabeli obszar
                    long iddObszar = db.insert("obszar", null, val2);
                    // wyciągniecie z listy punktów lokalizacji
                    for (int i = 0; i < latLngList.size(); i++) {
                        dlugosc = latLngList.get(i).longitude;
                        szerokosc = latLngList.get(i).latitude;
                        // zapis do bazy:
                        try {
                          // mapa powiązań nazwa_>wartosc dla tabeli lokalizacja
                            ContentValues values = new ContentValues();
                            //określenie kolejności
                            values.put(BazaDanych.KOLEJNOSC, i + 1);
                            // ustawienie id pola
                            values.put(BazaDanych.ID_POLA, idPola);
                            // szerokosci i dlugosc geograficzna
                            values.put(BazaDanych.LAT, szerokosc);
                            values.put(BazaDanych.LNG, dlugosc);
                            // nazwa pola
                            values.put(BazaDanych.NAZWA_pola, nazwaP);
                            // id obszaru
                            values.put(BazaDanych.ID_OBSZAR, iddObszar);
                             // utworzeie rekordu w tabeli lokalizacja
                            long id = db.insert(BazaDanych.TABEL_NAME, null, values);
                        } catch (SQLException e) { // jeśli błąd
                            Toast.makeText(getApplicationContext(), "BŁĄD !! Lokalizcja2",
                                    Toast.LENGTH_LONG).show();

                        }

                    }   // koniec for()
          // informacja o prawidłowym dodaniu danych do bazy
                    Toast.makeText(getApplicationContext(), "Dodano obszar na mapie!",
                            Toast.LENGTH_LONG).show();
                    // utworzenie intencji do głównej aktywności
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish(); // zakończenie bieżącej aktywności
                } else {  // informacja o braku czynności
                    Toast.makeText(getApplicationContext(), "Nie zaznaczono punktów..",
                            Toast.LENGTH_LONG).show();
                    // ukazanie okna dialogowego
              AlertDialogAnuluj();


                }   // koniec ifa ze sprawdzaniem czy sa pkty
                db.close();  // zamknięcie połączenia z baza danych



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
        System.out.println("******** JESTEM W fetch");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            System.out.println("\nO   COOO CHODZI sprawdzenie czy jest blad tj czy sa all uprawnienia ");
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

                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(ZaznaczObszarDialog.this);
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



    public void AlertDialogAnuluj() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nie zaznaczono punktów na mapie"); // tytuł
        // ustawienie wiadomości
        builder.setMessage("Czy chcesz zrezygnować z dodawania obszaru?");
        // dodanie przycisku Tak i jego obsługa
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // tworzenie intencji która otwiera główna aktywnosc
                Intent rezygnacja = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(rezygnacja);
                finish(); // zakończenie bieżącej aktywności
            }
        });
        //dodanie przycisku NIE
        builder.setNegativeButton("Nie", null);
        // tworzenie i pokazanie dialogu alert
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
