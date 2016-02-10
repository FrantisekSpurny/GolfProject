package cz.spurny.GpsApi;

/**
 * Objekt: GpsCoordinates.java
 * Popis:  Objekt slouzici pro ulozeni zemepisne sirky a delky.
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
 */

public class GpsCoordinates {

    private double latitude;
    private double longitude;

    public GpsCoordinates() {
    }

    public GpsCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
