package cz.spurny.DatabaseResort;

/**
 * Objekt: Resort.java
 * Popis:  Objekt pro uchovani jednoho resortu.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

public class Resort {
    public int id;
    public String name;
    public String area;
    public String city;
    public String street;
    public int streetNum;
    public double latitude;
    public double longitude;
    public int courseCount;

    public Resort() {
    }

    public Resort(int id, String name, String area, String city, String street, int streetNum, double latitude, double longitude, int courseCount) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.city = city;
        this.street = street;
        this.streetNum = streetNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.courseCount = courseCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(int streetNum) {
        this.streetNum = streetNum;
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

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

}