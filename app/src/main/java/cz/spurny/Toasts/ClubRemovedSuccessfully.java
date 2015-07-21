package cz.spurny.Toasts;

/**
 * Objekt: ClubRemovedSuccessfully.java
 * Popis:  Toast informujici o uspesnem odstraneni hole
 * Autor:  Frantisek Spurny
 * Datum:  29.06.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class ClubRemovedSuccessfully {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.ClubRemovedSuccessfully_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }
}
