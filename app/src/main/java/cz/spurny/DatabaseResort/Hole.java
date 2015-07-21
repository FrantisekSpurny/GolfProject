package cz.spurny.DatabaseResort;

/**
 * Objekt: Hole.java
 * Popis:  Objekt pro uchovani jedne jamky.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

public class Hole {
    public int id;
    public int idCourse;
    public int number;
    public String name;
    public int par;
    public int hcp;
    public int viewCount;

    public Hole() {
    }

    public Hole(int id, int idCourse, int number, String name, int par, int hcp, int viewCount) {
        this.id = id;
        this.idCourse = idCourse;
        this.number = number;
        this.name = name;
        this.par = par;
        this.hcp = hcp;
        this.viewCount = viewCount;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public int getHcp() {
        return hcp;
    }

    public void setHcp(int hcp) {
        this.hcp = hcp;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }  
       
}