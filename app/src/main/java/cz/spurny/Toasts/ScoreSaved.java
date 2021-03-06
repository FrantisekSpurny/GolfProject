package cz.spurny.Toasts;

/**
 * Objekt: ScoreSaved.java
 * Popis:  Toast informujici uzivatele o uspesnem ulozeni skore jamky.
 * Autor:  Frantisek Spurny
 * Datum:  25.07.2015
 */

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

public class ScoreSaved {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.ScoreSaved_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        return Toast.makeText(context, text, duration);
    }

}
