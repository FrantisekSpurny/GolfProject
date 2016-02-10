package cz.spurny.Toasts;

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

/**
 * Objekt: NoSavedGames.java
 * Popis:  Toast informujici uzivatele o tom, ze neexistuje zadne ulozene hry.
 * Autor:  Frantisek Spurny
 * Datum:  21.08.2015
 */
public class NoSavedGames {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.NoSavedGames_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }


}
