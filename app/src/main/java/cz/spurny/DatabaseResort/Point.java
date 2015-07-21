package cz.spurny.DatabaseResort;

/**
 * Objekt: Point.java
 * Popis:  Objekt pro uchovani jednoho bodu.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

public class Point {
    public int id;
    public int idView;
    public String type;
    public String name;
    public int pixelX;
    public int pixelY;
    public double latitude;
    public double longitude;
    public double elevation;

    public Point() {
    }

    public Point(int id, int idView, String type, String name, int pixelX, int pixelY, double latitude, double longitude,double elevation) {
        this.id = id;
        this.idView = idView;
        this.type = type;
        this.name = name;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdView() {
        return idView;
    }

    public void setIdView(int idView) {
        this.idView = idView;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPixelX() {
        return pixelX;
    }

    public void setPixelX(int pixelX) {
        this.pixelX = pixelX;
    }

    public int getPixelY() {
        return pixelY;
    }

    public void setPixelY(int pixelY) {
        this.pixelY = pixelY;
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

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    
}
