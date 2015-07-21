package cz.spurny.DatabaseResort;

/**
 * Objekt: Course.java
 * Popis:  Objekt pro uchovani jednoho hriste.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

public class Course {
    public int id;
    public int idResort;
    public String name;
    public int holeCount;
    public int par;

    public Course() {
    }

    public Course(int id, int idResort, String name, int holeCount, int par) {
        this.id = id;
        this.idResort = idResort;
        this.name = name;
        this.holeCount = holeCount;
        this.par = par;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdResort() {
        return idResort;
    }

    public void setIdResort(int idResort) {
        this.idResort = idResort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHoleCount() {
        return holeCount;
    }

    public void setHoleCount(int holeCount) {
        this.holeCount = holeCount;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }
    
}
