package cz.spurny.DatabaseInternal;

/**
 * Objekt: SavedGame.java
 * Popis:  Objekt uchovavajici ulozenou hru.
 * Autor:  Frantisek Spurny
 * Datum:  19.08.2015
 */
public class SavedGame {

    private int id;
    private int gameId;

    public SavedGame() {
    }

    public SavedGame(int gameId) {
        this.gameId = gameId;
    }

    public SavedGame(int id, int gameId) {
        this.id = id;
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}