package cz.spurny.DatabaseResort;

/**
 * Objekt: Tee.java
 * Popis:  Objekt pro uchovani jednoho odpaliste.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

public class Tee {
    public int id;
    public int idCourse;
    public String kind;
    public double CR;
    public double SR;

    public Tee() {
    }
    
    public Tee(int id, int idCourse, String kind, double CR, double SR) {
        this.id = id;
        this.idCourse = idCourse;
        this.kind = kind;
        this.CR = CR;
        this.SR = SR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public double getCR() {
        return CR;
    }

    public void setCR(double CR) {
        this.CR = CR;
    }

    public double getSR() {
        return SR;
    }

    public void setSR(double SR) {
        this.SR = SR;
    }
    
}
