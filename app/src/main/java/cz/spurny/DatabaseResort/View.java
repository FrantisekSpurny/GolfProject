package cz.spurny.DatabaseResort;

/**
 * Objekt: View.java
 * Popis:  Objekt pro uchovani jednoho zobrazeni.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

public class View {
    public int id;
    public int idHole;
    public String name;
    public String kind;
    public int sizeX;
    public int sizeY;
    public int azimut;
    public byte[] image;

    public View() {
    }

    public View(int id, int idHole, String name, String kind, int sizeX, int sizeY, int azimut, byte[] image) {
        this.id = id;
        this.idHole = idHole;
        this.name = name;
        this.kind = kind;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.azimut = azimut;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdHole() {
        return idHole;
    }

    public void setIdHole(int idHole) {
        this.idHole = idHole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public int getAzimut() {
        return azimut;
    }

    public void setAzimut(int azimut) {
        this.azimut = azimut;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    
}
