package com.example.aplikacjarolnicza;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ListFragment_ewidencja extends ListFragment implements AdapterView.OnItemClickListener {

    ArrayList<String> ListaPol= new ArrayList<String>();
    private BazaDanych mDBHelper;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.listfragment_ewidencja, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Context myContext = getActivity();
        mDBHelper = new BazaDanych(myContext);




        ListaPol = mDBHelper.odczytajNazwy(); // pobranie posiadanych pól
        //Adapter pośredniczy pomiędzy danymi i komponentem, który ich używa
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(myContext,
                android.R.layout.simple_list_item_1,ListaPol);
        // dołączenie adaptera do listy
        setListAdapter(dataAdapter);


        // getListView().setOnItemClickListener(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  Intent i = new Intent(myContext,PoleAktywnosc.class);
              //  i.putExtra("nazwa",ListaPol.get(position));
              //  startActivity(i);


            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //    Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
        System.out.println("\nPositionMylist "+position+" lista: "+ListaPol.get(position));



    }





}