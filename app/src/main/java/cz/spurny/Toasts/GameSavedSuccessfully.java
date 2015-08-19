package cz.spurny.Toasts;

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

/**
 * Objekt: GameSavedSuccessfully.java
 * Popis:  Toast informujici uzivatele o uspesnem ulozeni hry.
 * Autor:  Frantisek Spurny
 * Datum:  18.08.2015
 */

public class GameSavedSuccessfully {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.GameSavedSuccessfully_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_LONG;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }

}
