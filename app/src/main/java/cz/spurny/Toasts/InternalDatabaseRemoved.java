package cz.spurny.Toasts;

/**
 * Objekt: InternalDatabaseRemoved.java
 * Popis:  Toast informujici o uspesnem odtsraneni interni databaze.
 * Autor:  Frantisek Spurny
 * Datum:  13.08.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class InternalDatabaseRemoved {
    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.InternalDatabaseRemoved_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_LONG;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }
}
