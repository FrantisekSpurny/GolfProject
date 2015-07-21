package cz.spurny.Calculations;

/**
 * Objekt: SingleTapDetector.java
 * Popis:  Detekce pokliknuti na displej.
 * Autor:  Frantisek Spurny
 * Datum:  20.07.2015
 */

import android.content.Context;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;
import cz.spurny.DatabaseResort.Point;

public class SingleTapDetector {

    Point   downPoint;
    Point   upPoint;
    int     threshold;
    Context context;

    /** Konstruktor **/
    public SingleTapDetector(double thresholdRatio,Context context) {
        this.context = context;

        /* Vypocet thresholdu */
        this.threshold = (int)(getDisplayDimensions().x * thresholdRatio);

        /* Tvorba objektu bodu */
        downPoint = new Point();
        upPoint   = new Point();
    }

    /** Zjisteni rozmeru dipleje **/
    public PointF getDisplayDimensions() {
        PointF dimensions = new PointF();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        dimensions.set(size.x,size.y);

        return dimensions;
    }

    /** Nastaveni bodu stlaceni dipleje **/
    public void setUpPoint(int x,int y) {
        upPoint.setPixelX(x);
        upPoint.setPixelY(y);
    }

    /** Nastaveni bodu "opusteni" dipleje **/
    public void setDownPoint(int x,int y) {
        downPoint.setPixelX(x);
        downPoint.setPixelY(y);
    }

    /** Zjisteni jestli se jedna o "tap" **/
    public boolean isTap() {

        /* Kontrola vzdálenosti */
        return DistanceCalculations.pointDistancePx(downPoint, upPoint) <= threshold;

    }
}
