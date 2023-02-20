package com.example.aplikacjarolnicza;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CsvNawozenie {
    private BazaDanych db;

    public void utworzCsvNawoz(Context context) throws IOException {
        db = new BazaDanych(context);


        // System.out.println("\nczy istniejeeee"+db.czyIstniejeNazwaPola(1,"xyz"));
        String fname = "daneNawozenie.csv";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File f = new File(path, "/" + fname);
        if(f.exists()){
            System.out.println("\n Plik istnieje");
            boolean usun = f.delete();

        } else System.out.println("\n Plik   NIE istnieje");
        //   File file = new File("dane.csv");
//        f.createNewFile();
        path.mkdirs();
        //  f.createNewFile();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // pobranie listy nawożenia
        ArrayList<HashMap<String, String>> listaNawozenia = db.PobierzListeNawozenia();
        //tworzenie pliku
        CSVWriter csvWriter = new CSVWriter(new FileWriter(f));
        // określenie nagłówka tworzonego pliku
        String[] headerRecord = {"nazwa\nPola", "nazwaNawozu", "dawka","data"};
        csvWriter.writeNext(headerRecord); // wpis do pliku
      //"nazwa_pola","nazwa_nawozu","dawka","data_wykonania"
        HashMap<String , String> hashMap = new HashMap<>(); // deklaracja hashmapy
        for(int i=0;i<listaNawozenia.size();i++){  //wstawianie do mapy elementów listy nawożenia
            hashMap=listaNawozenia.get(i);
              // zapis do pliku danych, wyciąganie danych z mapy
              csvWriter.writeNext(new String[]{hashMap.get("nazwa_pola").toString(),
                      hashMap.get("nazwa_nawozu"),hashMap.get("dawka"),
                      hashMap.get("data_wykonania").toString()});
        }
        csvWriter.close();   // zamknięcie pliku
    }
}