package cz.spurny.Settings;

/**
 * Objekt: Settings.java
 * Popis:  Rozcestnik vedouci na ruzne moznosti uzivatelskeho nastaveni
 * Autor:  Frantisek Spurny
 * Datum:  1.7.2015
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import ar.com.daidalos.afiledialog.FileChooserActivity;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.Dialogs.ReloadInternalDatabase;
import cz.spurny.Dialogs.ReloadResortDatabase;
import cz.spurny.Player.CreatePlayer;
import cz.spurny.Player.EditBag;
import cz.spurny.Toasts.DatabaseImportSuccessful;

public class Settings extends ActionBarActivity {

    /* Prvky GUI */
    Button bEditProfile;
    Button bEditBag;
    Button bLoadResortDatabase;
    Button bReloadInternalDatabase;

    /* Intenty */
    Intent iEditProfile;
    Intent iEditBag;

    /* Context */
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        /* Pripojeni prvku GUI */
        bEditProfile            = (Button) findViewById(R.id.Settings_button_editProfile);
        bEditBag                = (Button) findViewById(R.id.Settings_button_editBag);
        bLoadResortDatabase     = (Button) findViewById(R.id.Settings_button_loadResortDatabase);
        bReloadInternalDatabase = (Button) findViewById(R.id.Settings_button_reloadInternalDatabase);

        /* Ulozeni kontextu */
        context = this;

        /* Tvorba intentu */
        iEditProfile = new Intent(this, CreatePlayer.class);
        iEditBag     = new Intent(this, EditBag.class);
        iEditProfile.putExtra("EXTRA_CREATE_PLAYER_TYPE", 1);
        iEditBag    .putExtra("EXTRA_EDIT_BAG_TYPE", 1);

        /* Reakce na kliknuti na tlacitka */
        buttonClickHandler();
    }

    /** Reakce na kliknuti na tlacitka v aktivite "Settings" **/
    public void buttonClickHandler() {

        /* Editace profilu */
        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ((Activity)context).finish(); startActivity(iEditProfile); }
        });

        /* Editace bagu */
        bEditBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
                startActivity(iEditBag);
            }
        });

        /* Nacteni databaze resortu */
        bLoadResortDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importResortDatabase();
            }
        });

        /* Reaload */
        bReloadInternalDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReloadInternalDatabase.dialog(context).show();
            }
        });
    }

    /** Importovani databaze resortu **/
    public void importResortDatabase() {

        DatabaseHandlerResort myDbHelper = new DatabaseHandlerResort(this);

        /** Databaze jiz existuje - prepsani **/
        if (myDbHelper.checkDataBase()) {
            ReloadResortDatabase.dialog(context).show();
        /** Databaze neexistuje - spusteni aktivity pro vyber souboru **/
        } else {
            Intent iFileChooser = new Intent(this, FileChooserActivity.class);
            iFileChooser.putExtra(FileChooserActivity.INPUT_START_FOLDER, Environment.getExternalStorageDirectory());
            ((Activity) context).startActivityForResult(iFileChooser, 0);
        }
    }

    /** Vysledek aktivity pro ziskani nazvu souboru **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            /** Ziskani cesty k souboru **/
            String filePath = "";

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.containsKey(FileChooserActivity.OUTPUT_NEW_FILE_NAME)) {
                    File folder = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                    String name = bundle.getString(FileChooserActivity.OUTPUT_NEW_FILE_NAME);
                    filePath = folder.getAbsolutePath() + "/" + name;
                } else {
                    File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                    filePath = file.getAbsolutePath();
                }
            }

            /** Vymazani databaze pokud existuje **/
            DatabaseHandlerResort myDbHelper = new DatabaseHandlerResort(this);

            if (myDbHelper.checkDataBase())
                eraseDatabase();

            /** Nacteni noveho souboru databaze **/
            try {
                myDbHelper.createDataBase(filePath);
            } catch (IOException ioe) {}

            /** Informovani uzivatele o uspesnem nacteni databaze **/
            DatabaseImportSuccessful.getToast(context).show();
        }
    }

    /** Vymazani databaze Resortu **/
    public void eraseDatabase() {

        /* Zjisteni jmena databaze */
        DatabaseHandlerResort dbr = new DatabaseHandlerResort(context);
        String dbName = dbr.getDatabaseName();
        dbr.close();

        /* Vymazani databaze */
        context.deleteDatabase(dbName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
