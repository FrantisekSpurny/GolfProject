package cz.spurny.GpsApi;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Objekt: GpsMethods.java
 * Popis:  Objekt obsahujici staticke metody pro praci s GPS daty.
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
 */
public class GpsMethods {

    /** Vypocet vzdalenosti mezi dvema GPS souradnicema **/
    public static double getDistance(GpsCoordinates from,
                                     GpsCoordinates to){
        double distance;

        /** TODO zatim pouze pseudo vypocet vzdalenosti */
        distance = Math.sqrt(
                                Math.pow(Math.abs(from.getLatitude()  - to.getLatitude()) ,2)
                              + Math.pow(Math.abs(from.getLongitude() - to.getLongitude()),2)
                            );
        return (int)(distance * 100000);
    }

    /** Prepocet px souradnici na gps souradnice **/
    public static GpsCoordinates pxToGps (Point          point,
                                          GpsCoordinates topLeftGps,
                                          GpsCoordinates bottomRightGps,
                                          int            width,
                                          int            height) {

        GpsCoordinates gpsCoordinates = new GpsCoordinates();

        /** TODO zatim pouze pseudo vypocet **/
        double ratioWidth  = Math.abs(topLeftGps.getLatitude()  - bottomRightGps.getLatitude())  / width;
        double ratioHeight = Math.abs(topLeftGps.getLongitude() - bottomRightGps.getLongitude()) / height;

        gpsCoordinates.setLatitude (topLeftGps.getLatitude()  + point.x * ratioWidth);
        gpsCoordinates.setLongitude(topLeftGps.getLongitude() + point.y * ratioHeight);

        return gpsCoordinates;
    }

    /** Vypocet GPS souradnic leveho horniho a praveho spodniho rohu zobrazeni **/
    public static List<GpsCoordinates> gpsOfView(Point          pxPointA,
                                                 Point          pxPointB,
                                                 GpsCoordinates gpsPointA,
                                                 GpsCoordinates gpsPointB,
                                                 int            width,
                                                 int            height) {

        List<GpsCoordinates> gpsCoordinates = new ArrayList<>();

        /** TODO zatim pouze pseudo vypocet **/
        double ratioWidth  = Math.abs(gpsPointA.getLatitude()  - gpsPointB.getLatitude())  /
                             Math.abs(pxPointA.x               - pxPointB.x);
        double ratioHeight = Math.abs(gpsPointA.getLongitude() - gpsPointB.getLongitude()) /
                             Math.abs(pxPointA.y               - pxPointB.y);

        GpsCoordinates topLeft    = new GpsCoordinates(gpsPointA.getLatitude()  - pxPointA.x * ratioWidth,
                                                       gpsPointA.getLongitude() - pxPointA.y * ratioHeight );

        GpsCoordinates rightBotom = new GpsCoordinates(gpsPointB.getLatitude()  + (width  - pxPointB.x) * ratioWidth,
                                                       gpsPointB.getLongitude() + (height - pxPointB.y) * ratioHeight);

        gpsCoordinates.add(topLeft);
        gpsCoordinates.add(rightBotom);

        return gpsCoordinates;
    }

}
