package cz.spurny.Calculations;

import android.content.Context;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;

/**
 * Objekt: SwipeDetector.java
 * Popis:  Detekce "swajp" pohybu.
 * Autor:  Frantisek Spurny
 * Datum:  26.07.2015
 */
public class SwipeDetector {

    /** Konstanty **/
    public static int LEFT_SWIPE  = -1;
    public static int RIGHT_SWIPE =  0;
    public static int NOT_SWIPE   =  1;

    /** Detekce jestli se jednalo o swipe **/
    public static int isSwipe(Context context,int x1,int y1,int x2,int y2,int widht,int height) {

        /* Rozdil na ose y nesmi byt vesti jak 0.2 * vyska_dipleje */
        if (Math.abs(y1-y2) > height * 0.2)
            return NOT_SWIPE;

        /* Rozdil na ose x musi byt vetsi jak 0.75 * sirka_displeje */
        if (Math.abs(x1-x2) < widht * 0.75)
            return NOT_SWIPE;

        /* Smer posunu */
        if (x1 > x2)
            return RIGHT_SWIPE;
        else
            return LEFT_SWIPE;
    }
}
