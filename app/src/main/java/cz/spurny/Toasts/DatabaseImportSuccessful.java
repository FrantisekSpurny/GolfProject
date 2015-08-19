package cz.spurny.Toasts;

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

/**
 * Objekt: DatabaseImportSuccessful.java
 * Popis:  Toast informujici uzivatele o uspesnem nacteni databaze.
 * Autor:  Frantisek Spurny
 * Datum:  16.08.2015
 */
public class DatabaseImportSuccessful {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.DatabaseImportSuccessful_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_LONG;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }

}
