package cz.spurny.Game;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;

/**
 * Objekt: Wind.java
 * Popis:  Konstanty pro zobrazeni specifikaci vetru.
 * Autor:  Frantisek Spurny
 * Datum:  19.08.2015
 */
public class Wind {

    public final static int NORTH        = 0;
    public final static int NORTH_EAST   = 1;
    public final static int EAST         = 2;
    public final static int SOUTH_EAST   = 3;
    public final static int SOUTH        = 4;
    public final static int SOUTH_WEST   = 5;
    public final static int WEST         = 6;
    public final static int NORTH_WEST   = 7;

    public static List<Drawable> getDrawableList(Context context) {
        List<Drawable> drawable = new ArrayList<>();
        drawable.add(context.getResources().getDrawable(R.drawable.wind_north_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_north_east_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_east_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_south_east_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_south_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_south_west_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_west_selector));
        drawable.add(context.getResources().getDrawable(R.drawable.wind_north_west_selector));

        return drawable;
    }

}
