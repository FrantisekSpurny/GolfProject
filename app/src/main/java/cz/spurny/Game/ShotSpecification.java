package cz.spurny.Game;

/**
 * Objekt: ShotSpecification.java
 * Popis:  Konstanty specifikace rany.
 * Autor:  Frantisek Spurny
 * Datum:  31.07.2015
 */

import android.content.Context;

import cz.spurny.CreateGame.R;

public class ShotSpecification {

    public static final int STRAIGHT = 0;
    public static final int SLAJZ    = 1;
    public static final int HOOK     = 2;
    public static final int TOPLA    = 3;
    public static final int FAT      = 4;
    public static final int SPIKE    = 5;
    public static final int HEEL     = 6;
    public static final int SAVE     = 7;

    /** Prevod ciselne konstanty na prislusny retezec **/
    public static String getString(int shotSpecifiaction,Context context) {
        switch (shotSpecifiaction) {
            case 0:
                return context.getString(R.string.ShotSpecification_string_straight);
            case 1:
                return context.getString(R.string.ShotSpecification_string_slajz);
            case 2:
                return context.getString(R.string.ShotSpecification_string_hook);
            case 3:
                return context.getString(R.string.ShotSpecification_string_topla);
            case 4:
                return context.getString(R.string.ShotSpecification_string_fat);
            case 5:
                return context.getString(R.string.ShotSpecification_string_spike);
            case 6:
                return context.getString(R.string.ShotSpecification_string_heel);
            case 7:
                return context.getString(R.string.ShotSpecification_string_save);
        }

        /** Mimo rozsah **/
        return null;
    }
}