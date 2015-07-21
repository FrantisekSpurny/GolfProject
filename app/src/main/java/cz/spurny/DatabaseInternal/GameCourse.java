package cz.spurny.DatabaseInternal;

/**
 * Objekt: GameCourse.java
 * Popis:  Objekt slouzici pro ulozeni vazby mezi hrou a zvolenym hristem.
 * Autor:  Frantisek Spurny
 * Datum:  22.06.2015
 */
public class GameCourse {

    private int id;
    private int idGame;
    private int idCourse;
    private int idTee;

    public GameCourse() {
    }

    public GameCourse(int id, int idGame, int idCourse, int idTee) {
        this.id = id;
        this.idGame = idGame;
        this.idCourse = idCourse;
        this.idTee = idTee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }

    public int getIdTee() {
        return idTee;
    }

    public void setIdTee(int idTee) {
        this.idTee = idTee;
    }
}
