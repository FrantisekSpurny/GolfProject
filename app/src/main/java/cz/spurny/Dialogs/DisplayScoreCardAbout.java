package cz.spurny.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import cz.spurny.CreateGame.R;

/**
 * Objekt: DisplayScoreCardAbout.java
 * Popis:  Dialog zobrazujici legendu
 * Autor:  Frantisek Spurny
 * Datum:  13.08.2015
 */
public class DisplayScoreCardAbout {

    public static Dialog dialog(Context context) {

		/* tvorba dialogu pro tvorbu hole */
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.display_score_card_about);
        dialog.setTitle(context.getString(R.string.DisplayScoreCardAbout_string_title));

        Button bDone = (Button) dialog.findViewById(R.id.DisplayScoreCardAbout_button_ok);

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.hide(); }
        });

        return dialog;
    }

}
