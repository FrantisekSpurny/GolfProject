package cz.spurny.DatabaseInternal;

/**
 * Objekt: Club.java
 * Popis:  Objekt slouzucu pro ulozeni informaci o holi.
 * Autor:  Frantisek Spurny
 * Datum:  24.06.2015
 */
public class Club {

    private int id;
    private String name;
    private String model;
    private double standardStrokeLength;
    private double averageStrokeLength;

    public Club() {
    }

    public Club(int id, String name, String model, double standardStrokeLength, double averageStrokeLength) {

        this.id = id;
        this.name = name;
        this.model = model;
        this.standardStrokeLength = standardStrokeLength;
        this.averageStrokeLength = averageStrokeLength;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getStandardStrokeLength() {
        return standardStrokeLength;
    }

    public void setStandardStrokeLength(double standardStrokeLength) {
        this.standardStrokeLength = standardStrokeLength;
    }

    public double getAverageStrokeLength() {
        return averageStrokeLength;
    }

    public void setAverageStrokeLength(double averageStrokeLength) {
        this.averageStrokeLength = averageStrokeLength;
    }
}
