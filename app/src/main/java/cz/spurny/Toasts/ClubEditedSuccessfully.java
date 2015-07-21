package cz.spurny.Toasts;

/**
 * Objekt: ClubEditedSuccessfully.java
 * Popis:  Toast informujici o uspesnem pridani hole
 * Autor:  Frantisek Spurny
 * Datum:  30.06.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class ClubEditedSuccessfully {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.ClubEditedSuccessfully_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }
}
