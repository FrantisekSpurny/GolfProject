package cz.spurny.Toasts;

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

/**
 * Objekt: NotValidPlayerToast.java
 * Popis:  Zprava o tom ze uzivatel nespravne vyplnil profil hrace.
 * Autor:  Frantisek Spurny
 * Datum:  23.06.2015
 */

public class NotValidPlayerToast {

   public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.NonValidPlayerToast_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }
}
