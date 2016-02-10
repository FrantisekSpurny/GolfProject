package cz.spurny.Game;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;

/**
 * Objekt: Weather.java
 * Popis:  Konstanty pro zobrazeni "pocasi".
 * Autor:  Frantisek Spurny
 * Datum:  19.08.2015
 */

public class Weather {

    public final static int SUNNY        = 0;
    public final static int PARTLYCLOUDY = 1;
    public final static int CLOUDY       = 2;
    public final static int RAIN         = 3;
    public final static int THUNDER      = 4;

    public static String getString(int weather,Context context) {

        switch (weather) {
            case SUNNY:
                return context.getString(R.string.Weather_string_sunny);
            case PARTLYCLOUDY:
                return context.getString(R.string.Weather_string_partlyCloudy);
            case CLOUDY:
                return context.getString(R.string.Weather_string_cloudy);
            case RAIN:
                return context.getString(R.string.Weather_string_rain);
            case THUNDER:
                return context.getString(R.string.Weather_string_thunder);
        }
        return null;
    }

    public static List<String> getStringList (Context context) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            list.add(getString(i,context));
        }

        return list;
    }

    public static List<Drawable> getDrawableList(Context context) {
        List<Drawable> drawable = new ArrayList<>();
        drawable.add(context.getResources().getDrawable(R.drawable.sunny_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.partly_cloudy_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.cloudy_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.rain_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.thunder_selector));

        return drawable;
    }
}