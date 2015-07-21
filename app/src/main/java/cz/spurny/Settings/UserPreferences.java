package cz.spurny.Settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Objekt: UserPreferences.java
 * Popis:  Objekt umoznujici ukladat a nacitat preference uzivatele
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
 */

public class UserPreferences {

    /* Nastaveni preference uzivatele pro zobrazovani GPS dialogu */
    public static void setGpsDialogShow(Context context, boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("gpsDialogShow",flag);
        editor.commit();
    }

    /* Ziskani preference uzivatele pro zobrazovani GPS dialogu */
    public static boolean getGpsDialogShow(Context context) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        return settings.getBoolean("gpsDialogShow",false);
    }

    /* Vlozeni id "hlavniho" uzivatelskeho profilu */
    public static void setMainUserId(Context context,long id) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("mainUserId",id);
        editor.commit();
    }

    /* Ziskani id "hlavniho" profilu uzivatele */
    public static long getMainUserId (Context context) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        return settings.getLong("mainUserId",-1);
    }

}
