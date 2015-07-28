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
    int displayWidth;
    int displayHeight;

    /** Konstruktor **/
    public SingleTapDetector(double thresholdRatio,Context context) {
        this.context = context;

        /* Vypocet thresholdu */
        this.threshold = (int)(getDisplayDimensions().x * thresholdRatio);

        /* Tvorba objektu bodu */
        downPoint = new Point();
        upPoint   = new Point();

        displayWidth  = (int)(getDisplayDimensions().x);
        displayHeight = (int)(getDisplayDimensions().y);
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

    /** Ziskani souradnici x bodu stlaceni dispeje **/
    public int getDownX() {
        return downPoint.pixelX;
    }

    /** Ziskani souradnici y bodu stlaceni dispeje **/
    public int getDownY() {
        return downPoint.pixelY;
    }

    /** Zjisteni jestli se jedna o "tap" **/
    public boolean isTap() {

        /* Kontrola vzdalenosti */
        return DistanceCalculations.pointDistancePx(downPoint, upPoint) <= threshold;

    }

    /** Sirka displeje **/
    public int getDisplayWidth() {
        return displayWidth;
    }

    /** Vyska displeje **/
    public int getDisplayHeight() {
        return displayHeight;
    }
}
