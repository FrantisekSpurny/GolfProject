package cz.spurny.Toasts;

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

/**
 * Objekt: GameSavedSuccessfuly.java
 * Popis:  Toast informujici uzivatele o tom, ze byla hra zaznamenana.
 * Autor:  Frantisek Spurny
 * Datum:  18.08.2015
 */

public class GameRecordedSuccessfully {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.GameRecordedSuccessfuly_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_LONG;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }
}
