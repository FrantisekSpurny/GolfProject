package cz.spurny.CreateGame;

/**
 * Objekt: SelectResort.java
 * Popis:  Aktivita zobrazujici seznam resortu.
 * Autor:  Frantisek Spurny
 * Datum:  16.6.2015
 **/

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Resort;
import cz.spurny.Dialogs.NewGameTerminate;

public class SelectResort extends ActionBarActivity {

    /* Kontext */
    Context context;

    /* Prvky GUI */
    ListView lvResorts;
    EditText etSearch;

    /* Adapter */
    SelectResortAdapter adapter;
    List<Resort> resorts = null; // hodnoty pro adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_resort_layout);

        /* Ziskani kontextu */
        context = this;

        /* Pripojeni databaze */
        DatabaseHandlerResort dbr = new DatabaseHandlerResort(this);
        try { dbr.openDataBase(); } catch(SQLException exception){ /**TODO**/ }

        /* Pripojeni prvku GUI */
        lvResorts = (ListView) findViewById(R.id.SelectResort_listView_resorts);
        etSearch  = (EditText) findViewById(R.id.SelectResort_editText_search);

        /* Ziskani hodnot z databaze */
        resorts = dbr.getAllResorts();

        /* Tvorba adapteru */
        adapter = new SelectResortAdapter(this, resorts);

		/* Prirazeni adapteru */
        lvResorts.setAdapter(adapter);

        /* Funkcionalita EditText */
        searchEditText(etSearch);

        /* Zachytavani kliknuti na polozku seznamu */
        lvResorts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                selectCourse(context,resorts.get(arg2).getId());
            }
        });

        /* Zavreni databaze */
        dbr.close();
    }

    /* Metoda provadejici volani aktivity SelectCourse */
    public void selectCourse(Context context,int idResort) {
        Intent iSelectCourse = new Intent(context,SelectCourse.class);
        iSelectCourse.putExtra("EXTRA_SELECT_COURSE_IDRESORT",idResort);
        context.startActivity(iSelectCourse);
    }

    /** Implementace filtrovani polde nazvu resortu **/
    public void searchEditText(EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                /* Uzivatel zmeni text - na tuto zmenu reagujeme */
                SelectResort.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // Neiplmentovano

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // Neiplmentovano
            }
        });
    }

    /** Reakce na zmacknuti tlacitka "zpet" **/
    @Override
    public void onBackPressed()
    {
        NewGameTerminate.dialog(this).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false; //nepouzivame menu
    }

}
