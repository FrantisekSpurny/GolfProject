package cz.spurny.Player;

/**
 * Objekt: CreatePlayer.java
 * Popis:  Aktivita slouzici pro upravu bagu.
 * Autor:  Frantisek Spurny
 * Datum:  24.06.2015
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.Dialogs.AddClub;
import cz.spurny.Dialogs.EditClub;
import cz.spurny.Dialogs.RemoveClub;
import cz.spurny.Settings.Settings;

public class EditBag extends ActionBarActivity {

    /* Prvky GUI */
    ListView lvClubList;
    Button   bAddClub;
    Button   bDone;

    /* Adapter */
    public EditBagAdapter adapter = null;

    /* Kontext */
    Context context;

    /* Seznam holi */
    List<Club> clubs;

    /* Typ */
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bag_layout);

        /* Prevzeti parametru z volajici aktivity - urcuje typ */
        Intent iPrevActivity = getIntent();
        type = iPrevActivity.getIntExtra("EXTRA_EDIT_BAG_TYPE",0);

        /* Ziskani kontextu */
        context = this;

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(this);

        /* Pripojeni prvku GUI */
        lvClubList = (ListView) findViewById(R.id.EditBag_listView_clubList);
        bAddClub   = (Button)   findViewById(R.id.EditBag_button_addClub);
        bDone      = (Button)   findViewById(R.id.EditBag_button_done);

        /* Inicializace adapteru */
        initAdapter(dbi);

        /* Reakce na dlouhe stlaceni polozky listu */
        if (dbi.getAllClubs()!=null)
            longPressHandler(dbi);

        /* Reakce kliknuti na tlacitka */
        buttonClickHandler(dbi);

        /* Reakce na kliknuti na polozku seznamu */
        if (dbi.getAllClubs()!=null)
            listClickHandler(dbi);

        /* Odpojeni databaze */
        dbi.close();
    }

    /** Inicializace adapteru pro list holi **/
    public void initAdapter(DatabaseHandlerInternal dbi) {

        if (dbi.getAllClubs() != null)
            clubs = dbi.getAllClubs();
        else
            clubs = new ArrayList<>();

        adapter = new EditBagAdapter(this, clubs);
        lvClubList.setAdapter(adapter);
    }

    /** Reakce na kliknuti na tlacitka "pridej hul" a "hotovo" **/
    public void buttonClickHandler(final DatabaseHandlerInternal dbi) {
		/* Reakce na kliknuti na tlacitko "Hotovo" */
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDone();
            }
        });

		/* reakce na kliknuti na tlacitko "Pridej Hul" */
        bAddClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddClub.dialog(dbi, context, adapter, clubs).show();
            }
        });
    }

    /** Reakce na kliknuti na polozku seznamu - editace dane hole **/
    public void listClickHandler(final DatabaseHandlerInternal dbi) {
        lvClubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditClub.dialog(dbi, context, adapter, clubs, position).show();
            }
        });
    }

    /** Reakce na dlouhe stlaceni polozky listu **/
    public void longPressHandler(final DatabaseHandlerInternal dbi) {
        lvClubList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                RemoveClub.dialog(dbi, clubs, pos, context, adapter).show();
                return true;
            }
        });
    }

    /** Ukonceni editace bagu **/
    public void editDone() {
        Intent iMainMenu = new Intent(context, MainMenu.class);
        Intent iSettings = new Intent(context, Settings.class);

        ((Activity)context).finish();

        if (type==0)
            startActivity(iMainMenu);
        else
            startActivity(iSettings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
