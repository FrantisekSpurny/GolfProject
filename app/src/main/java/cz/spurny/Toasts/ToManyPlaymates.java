package cz.spurny.Toasts;

/**
 * Objekt: ToManyPlaymates.java
 * Popis:  Toast informujici uzivatele o tom, ze nelze pridat dalsi spoluhrace.
 * Autor:  Frantisek Spurny
 * Datum:  23.07.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class ToManyPlaymates {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.ToManyPlaymates_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        return Toast.makeText(context, text, duration);
    }

}
