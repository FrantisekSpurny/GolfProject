package cz.spurny.Game;

/**
 * Objekt: BallPosition.java
 * Popis:  Konstanty pozice mice.
 * Autor:  Frantisek Spurny
 * Datum:  01.08.2015
 */

import android.content.Context;

import cz.spurny.CreateGame.R;

public class BallPosition {

    public static final int OK           = 0;
    public static final int DROP_FREE    = 1;
    public static final int DROP_PENALTY = 2;
    public static final int NEW_PENALTY  = 3;
    public static final int LOST_BALL    = 4;


    /** Prevod ciselne konstanty na prislusny retezec **/
    public static String getString(int ballPosition,Context context) {
        switch (ballPosition) {
            case 0:
                return context.getString(R.string.BallPosition_string_ok);
            case 1:
                return context.getString(R.string.BallPosition_string_dropFree);
            case 2:
                return context.getString(R.string.BallPosition_string_dropPenalty);
            case 3:
                return context.getString(R.string.BallPosition_string_newPenalty);
            case 4:
                return context.getString(R.string.BallPosition_string_lostBall);
        }

        /** Mimo rozsah **/
        return null;
    }
}
