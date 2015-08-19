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

    /** Zobrazeni GPS dialogu **/

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

    /** Hlavni hrac **/

    /* Vlozeni id "hlavniho" uzivatelskeho profilu */
    public static void setMainUserId(Context context,long id) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("mainUserId", id);
        editor.commit();
    }

    /* Ziskani id "hlavniho" profilu uzivatele */
    public static long getMainUserId (Context context) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        return settings.getLong("mainUserId", -1);
    }

    /** Zobrazeni dialog Zaznamenani hry **/
    public static void setRecordDialogShow(Context context, boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("recordDialogShow", flag);
        editor.commit();
    }

    public static boolean getRecordDialogShow(Context context) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        return settings.getBoolean("recordDialogShow", false);
    }

    /** Zobrazeni dialog Ulozeni hry **/
    public static void setSaveDialogShow(Context context, boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("saveDialogShow", flag);
        editor.commit();
    }

    public static boolean getSaveDialogShow(Context context) {
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        return settings.getBoolean("saveDialogShow", false);
    }

}
