package cz.spurny.Toasts;

/**
 * Objekt: NotValidClubToast.java
 * Popis:  Toast informujici uzivatele o nevalidnim vstupu
 * Autor:  Frantisek Spurny
 * Datum:  24.06.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class NotValidClubToast {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.NonValidClubToast_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }

}
