package cz.spurny.Toasts;

/**
 * Objekt: NoPlaymates.java
 * Popis:  Toast informujici ze nelze zmenit hrace.
 * Autor:  Frantisek Spurny
 * Datum:  17.08.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class NoPlaymates {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.NoPlaymates_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        Toast toast = Toast.makeText(context, text, duration);

        return toast;
    }

}
