package cz.spurny.Game;

/**
 * Objekt: GameOnHoleGestureListener.java
 * Popis:  Objekt implementujici reakce na jednoliva gesta (dotyky na displej).
 * Autor:  Frantisek Spurny
 * Datum:  17.07.2015
 */

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GameOnHoleGestureListener extends GestureDetector.SimpleOnGestureListener {

    /** Reakce na tzn "tap" **/
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d("TOUCH", "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}
