package com.example.aplikacjarolnicza;

import android.content.Context;
import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CsvPryskanie {
    private BazaDanych db;

    public void utworzCsvPryskanie(Context context) throws IOException {
        db = new BazaDanych(context);
        ArrayList<HashMap<String, String>> listaOpryskow = db.PobierzListeOpryskow();





        // System.out.println("\nczy istniejeeee"+db.czyIstniejeNazwaPola(1,"xyz"));
        String fname = "danePryskanie.csv";
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
        // tworzenie instancji CSVWriter
        CSVWriter csvWriter = new CSVWriter(new FileWriter(f));
        // określenie nagłówku dokumentu
        String[] headerRecord = {"nazwa\nPola", "nazwa\noprysku", "dawka","data"};
        // zapisa nagłówku do pliku
        csvWriter.writeNext(headerRecord);
        HashMap<String , String> hashMap = new HashMap<>();
        // zapis kolejnych wierszy z danymi do pliku
        for(int i=0;i<listaOpryskow.size();i++){
            hashMap=listaOpryskow.get(i);
            csvWriter.writeNext(new String[]{hashMap.get("nazwa_pola").toString(),
                    hashMap.get("nazwa_oprysku"),hashMap.get("dawka"),
                    hashMap.get("data_wykonania").toString()});

        }
        csvWriter.close(); // zakończenie tworzenia pliku
    }
}