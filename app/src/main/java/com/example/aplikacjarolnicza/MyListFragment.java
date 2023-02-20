package com.example.aplikacjarolnicza;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aplikacjarolnicza.BazaDanych;
import com.example.aplikacjarolnicza.R;

import java.util.ArrayList;
import java.util.List;

public class MyListFragment extends ListFragment implements OnItemClickListener {
    ArrayList<String> ListaPol= new ArrayList<String>();
    private BazaDanych mDBHelper;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.listfragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Context myContext = getActivity();
        mDBHelper = new BazaDanych(myContext);

      // pobranie nazw i umieszczenie ich w ArrayList ListaPol
      ListaPol = mDBHelper.odczytajNazwy();
      //Adapter pośredniczy pomiędzy danymi i komponentem, który ich używa
       ArrayAdapter dataAdapter = new ArrayAdapter<String>(myContext,
               android.R.layout.simple_list_item_1,ListaPol);

       setListAdapter(dataAdapter);
       // oprogramowanie klikalności w elementy listy
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // utworzenie intencji
                Intent i = new Intent(myContext,PoleAktywnosc.class);
                //przekazanie nazwy klikniętej pozycji do nowej aktywnosci
                 i.putExtra("nazwa",ListaPol.get(position));
                 // rozpoczęcie aktywności
                startActivity(i);
            }  });

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    //    Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        System.out.println("\nPositionMylist "+position+" lista: "+ListaPol.get(position));



    }






}