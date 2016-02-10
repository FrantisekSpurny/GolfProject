package cz.spurny.Toasts;

import android.content.Context;
import android.widget.Toast;

import cz.spurny.CreateGame.R;

/**
 * Objekt: SavedGameErasedSuccessfully.java
 * Popis:  Toast informujici o uspesnem odstraneni ulozene hry.
 * Autor:  Frantisek Spurny
 * Datum:  23.08.2015
 */
public class SavedGameErasedSuccessfully {

    public static Toast getToast(Context context) {

        /* Text zpravy */
        CharSequence text = context.getString(R.string.SavedGameErasedSuccessfully_string_toast);

        /* Delka zobrazeni */
        int duration = Toast.LENGTH_SHORT;

        /* Inicializace */
        return Toast.makeText(context, text, duration);
    }

}
