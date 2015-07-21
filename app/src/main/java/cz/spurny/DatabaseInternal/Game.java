package cz.spurny.DatabaseInternal;

/**
 * Objekt: Game.java
 * Popis:  Objekt slouzici pro uchovani jednotlivych her
 * Autor:  Frantisek Spurny
 * Datum:  22.06.2015
 */

public class Game {

    private int id;
    private String date;
    private String description;
    private int weather;
    private int wind;
    private int windPower;
    private int courseToughness;

    public Game() {
    }

    public Game(int id, String date, String description, int weather, int wind, int windPower, int courseToughness) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.weather = weather;
        this.wind = wind;
        this.windPower = windPower;
        this.courseToughness = courseToughness;
    }

    public String windInt2String(int wind) {

        switch (wind) {
            case 0:
                return "BV";
            case 1:
                return "S";
            case 2:
                return "J";
            case 3:
                return "V";
            case 4:
                return "Z";
            case 5:
                return "SZ";
            case 6:
                return "SV";
            case 7:
                return "JZ";
            case 8:
                return "JV";
        }

        return null; //chyba
    }

    public int windString2Int(String wind) {

        switch (wind) {
            case "BV":
                return 0;
            case "S":
                return 1;
            case "J":
                return 2;
            case "V":
                return 3;
            case "Z":
                return 4;
            case "SZ":
                return 5;
            case "SV":
                return 6;
            case "JZ":
                return 7;
            case "JV":
                return 8;
        }

        return -1; //chyba
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public int getWindPower() {
        return windPower;
    }

    public void setWindPower(int windPower) {
        this.windPower = windPower;
    }

    public int getCourseToughness() {
        return courseToughness;
    }

    public void setCourseToughness(int courseToughness) {
        this.courseToughness = courseToughness;
    }
}
