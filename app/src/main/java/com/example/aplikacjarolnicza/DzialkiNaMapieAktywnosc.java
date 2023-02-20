package com.example.aplikacjarolnicza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class DzialkiNaMapieAktywnosc  extends AppCompatActivity implements OnMapReadyCallback {
    private BazaDanych dbHelper;
    public static  String NAZWA_POLA="nazwaPola";
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
        setContentView(R.layout.dzialki_na_mapie_aktywnosc);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
     
     
        liczba_punktow=0;
        dbHelper = new BazaDanych(this);

        // inicjalizacja SupportMapFragment


        lokalizaja.clear();
        latLngList.clear();




        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map2);
        supportMapFragment.getMapAsync(this);

    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // podstawowe ustwienia dla mapy tj. widoczny kompas, możliwość powiększania pomniejszania, rodzaj mapy.
        gMap = googleMap;
        final UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(false);


// z currentLocation
        //   LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

        //   MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("moja");
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


        // dodaje okrag wokol lokalizacji
        // googleMap.addCircle(new CircleOptions().center(latLng).radius(16).strokeColor(Color.GREEN).fillColor(Color.argb(80,0,0,255)));
        // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));  // powiekszy
        // current location end

        // LatLng latLng = lokalizaja.get(1);
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));  // powiekszy



       // System.out.println("\n Map read lat="+lat+" lng="+lng+"\n");
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);






        if (polygon != null) polygon.remove();



        ArrayList<String> listaPol= new ArrayList<>();
        // pobranie nazw pól, które mają obszar w bazie danych
        listaPol=dbHelper.odczytajNazwyzPunktami(1);
      for(int i=0;i<listaPol.size();i++){
           // pobranie punktów dla i-tego obszaru z listy
          lokalizaja=dbHelper.punktyPola(listaPol.get(i),1);
          // jeśli punkty istnieją
          if(lokalizaja.size()!=0) {
              // tworzenie znacznika  w pierwszym punkcie lokalizacji danego obszaru
              MarkerOptions markeroptions = new MarkerOptions().position(lokalizaja.get(1)).
                      draggable(false).icon(BitmapDescriptorFactory.
                      defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                      anchor(0.5f, 0.5f).title(listaPol.get(i));
              // dodanie znacznika do mapy
              Marker marker = gMap.addMarker(markeroptions);
              // określenie opcji linii i punktów lokalizacji
              PolygonOptions polygonOptions = new PolygonOptions().addAll(lokalizaja).
                      clickable(true).fillColor(Color.BLUE);
              // dodanie obszaru do mapy
              polygon = gMap.addPolygon(polygonOptions);

          } else { // jeśli pola nie mają lokalizacji
              // komunikat
              Toast.makeText(getApplicationContext(),
                      "Nie przypisano działki na mapie",
                      Toast.LENGTH_LONG).show();
              
              

          }

      }


      if(lokalizaja.size()>0) {
          CameraUpdate center = CameraUpdateFactory.newLatLng(lokalizaja.get(1));
          CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
          googleMap.moveCamera(center);
          googleMap.animateCamera(zoom);

      } else {  Toast.makeText(getApplicationContext(), "Brak pól na mapie..",
              Toast.LENGTH_LONG).show();


      }




    gMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
        @Override
        public void onPolygonClick(Polygon polygon) {
            //  zmiana koloru wypełnienia
            polygon.setFillColor(Color.YELLOW);
            // komunikat
            Toast.makeText(getApplicationContext(),
                    "Kliknij marker aby wykonać czynność",
                    Toast.LENGTH_LONG).show();
        }
    });



      gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
          @Override
          public boolean onMarkerClick(Marker marker) {


              System.out.println("\nMarker tag= "+marker.getTitle());
              marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            //
              //
              //  PopupMenu popupMenu = new PopupMenu(getApplicationContext(),);

              View v = (View)findViewById(R.id.toolbar);
              openMenuDialog(v,marker.getTitle());

              return false;

          }

      });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // obsługa kliknięć w menu
        int id = item.getItemId();
        // sprawdzenie czy kliknięto zdefiniowana pozycje
        if (id == R.id.action3_ZestawienieNawozenia) {
            Toast.makeText(getApplicationContext(), "Zestawienie",
                    Toast.LENGTH_LONG).show();
            // tworzenie intencji do nowej aktywnosci
            Intent zestawienieNawozenia = new Intent(getApplicationContext(),
                    ZestawienieNawozenieAktywnosc.class);
            startActivity(zestawienieNawozenia);
            finish();


        } else if(id == R.id.action2ZestawieniePryskania) {

            Intent zestawieniePryskania = new Intent(getApplicationContext(),ZestawieniePryskanieAktywnosc.class);
            startActivity(zestawieniePryskania);
finish();
        } else if( id== R.id.action1ListaPol){

            Intent listapolIntent= new Intent(getApplicationContext(),ListaPolAktywnosc.class);
            startActivity(listapolIntent);
            finish();
        } else if(id == R.id.action4_Dodajpole){
            Intent dodajPole = new Intent(getApplicationContext(),ObslugaAktywnosc.class);
            startActivity(dodajPole);
 finish();

        }


        return super.onOptionsItemSelected(item);
    }




           public void openMenuDialog(View view, final String name) {
             // określenie elementów okna dialogowego
            String[] dialogOpcje = {"Dodaj zabieg", "Informacje o polu",
                "Zobacz zabiegi", "Dodaj notatke", "Anuluj"};
             // tworzenie okna
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogStyl) );
            // obsługa kliknięcia w opcje
               alertDialogBuilder.setTitle("Wybierz działanie, nazwa pola:"+name).
                setItems(dialogOpcje, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:  { Toast.makeText(getApplicationContext(), "Dodaj zabieg",
                            Toast.LENGTH_LONG).show();
                    // tworzenie intencji do nowej aktywności
                    Intent dodajZabieg = new Intent(getApplicationContext(),
                            DodajZabiegAktywnosc.class);
                    dodajZabieg.putExtra(NAZWA_POLA,name);
                    startActivity(dodajZabieg);
                    }break;
                    case 1: { // komunikat o wyborze
                        Toast.makeText(getApplicationContext(), "Informacje o polu",
                                Toast.LENGTH_LONG).show();
                        // tworzenie intencji do nowej aktywności
                        Intent intent = new Intent(getApplicationContext(),InformacjeoPolu.class);
                        intent.putExtra(NAZWA_POLA,name);
                        startActivity(intent);
                    }break;
                    case 2: {Toast.makeText(getApplicationContext(), "Zobacz zabiegi",
                            Toast.LENGTH_LONG).show();
                    Intent zabiegi = new Intent(getApplicationContext(),
                            ZobaczZabiegiAktywnosc.class);
                    zabiegi.putExtra(NAZWA_POLA,name);
                    startActivity(zabiegi);
                    }break;
                    case 3:  {Toast.makeText(getApplicationContext(), "Dodaj notatkę",
                            Toast.LENGTH_LONG).show();
                    Intent notatka = new Intent(getApplicationContext(),
                            NotatkaAktywnosc.class);
                    notatka.putExtra(NAZWA_POLA,name);
                    startActivity(notatka);
                    }break;
                    case 4: Toast.makeText(getApplicationContext(), "Powrót",
                            Toast.LENGTH_LONG).show(); break; }
            }
        });
               // stworzenie i pokazanie okna
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
         }





}

