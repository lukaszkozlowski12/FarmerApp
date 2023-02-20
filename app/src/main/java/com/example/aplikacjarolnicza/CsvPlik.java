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
import java.util.List;

public class CsvPlik {
private BazaDanych db;

    public void utworzCsv(Context context) throws IOException {
db = new BazaDanych(context);






       // System.out.println("\nczy istniejeeee"+db.czyIstniejeNazwaPola(1,"xyz"));
        String fname = "dane.csv";
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
        // pobranie danych
        ArrayList <String> listaPol= db.odczytajNazwy();
        // tworzenie pliku
        CSVWriter csvWriter = new CSVWriter(new FileWriter(f));
        // okreśłenie nagłówka pliku
        String[] headerRecord = {"nazwa\nPola", "powierzchnia", "plan\nAzotowy", "Azot\nzastosowany"};
        // umieszczenie nagłówka w pliku
        csvWriter.writeNext(headerRecord);
        // umieszczenie kolejnych wierszy danych w pliku
        for(int i=0;i<listaPol.size();i++) {
            csvWriter.writeNext(new String[]{listaPol.get(i),
                    Float.toString(db.getPowierzchniaByName(listaPol.get(i))),
                    Double.toString(db.pobierzPlanAzot(db.getIDbyName_tabelaPole(listaPol.get(i)),1)),
                    Double.toString(db.getSumaAzotByid(db.getIDbyName_tabelaPole(listaPol.get(i))))});
        }
        // zakończenie zapisu
            csvWriter.close();
    }
}