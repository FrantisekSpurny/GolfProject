package cz.spurny.Toasts;

/**
 * Objekt: NotValidPlaymate.java
 * Popis:  Upozorneni uzivatele na to, ze nelze odebrat hlavni profil ze seznamu hracu.
 * Autor:  Frantisek Spurny
 * Datum:  07.07.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class NotValidPlaymate {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.NonValidPlaymate_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }
}
