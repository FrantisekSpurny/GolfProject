package cz.spurny.DatabaseInternal;

/**
 * Objekt: Player.java
 * Popis:  Objekt uchovavajici informace o jednom hraci
 * Autor:  Frantisek Spurny
 * Datum:  22.06.2015
 */
public class Player {

    private int id;
    private String name;
    private String surname;
    private String nickname;
    private Double handicap;

    public Player() {
    }

    public Player(int id, String name, String surname, String nickname, Double handicap) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.handicap = handicap;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getHandicap() {
        return handicap;
    }

    public void setHandicap(Double handicap) {
        this.handicap = handicap;
    }
}
