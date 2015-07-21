package cz.spurny.Toasts;

/**
 * Objekt: GameCreatedSuccessfully.java
 * Popis:  Toast informujici uzivatele o uspesnem vytvoreni hry.
 * Autor:  Frantisek Spurny
 * Datum:  08.07.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class GameCreatedSuccessfully {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.GameCreatedSuccessfully_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_LONG;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }

}
