package cz.spurny.Game;

import android.content.Context;

import cz.spurny.CreateGame.R;

/**
 * Objekt: AreaType.java
 * Popis:  Konstanty a pomocne metody pro zadavani typu plochy.
 * Autor:  Frantisek Spurny
 * Datum:  31.07.2015
 */

public class AreaType {

    public static final int FAIRWAY   = 0;
    public static final int SEMIROUGH = 1;
    public static final int ROUGHT    = 2;
    public static final int BUNKER    = 3;
    public static final int GREEN     = 4;
    public static final int WATER     = 5;
    public static final int BIOZONE   = 6;
    public static final int OUT       = 7;
    public static final int TEE       = 8;

    /** Prevod ciselne konstanty na prislusny retezec **/
    public static String getString(int areaType,Context context) {
        switch (areaType) {
            case 0:
                return context.getString(R.string.AreaType_string_fairway);
            case 1:
                return context.getString(R.string.AreaType_string_semirough);
            case 2:
                return context.getString(R.string.AreaType_string_rough);
            case 3:
                return context.getString(R.string.AreaType_string_bunker);
            case 4:
                return context.getString(R.string.AreaType_string_green);
            case 5:
                return context.getString(R.string.AreaType_string_water);
            case 6:
                return context.getString(R.string.AreaType_string_biozone);
            case 7:
                return context.getString(R.string.AreaType_string_out);
            case 8:
                return context.getString(R.string.AreaType_string_tee);
        }

        /** Mimo rozsah **/
        return null;
    }
}
