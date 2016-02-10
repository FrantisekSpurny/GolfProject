package cz.spurny.DatabaseInternal;

/**
 * Objekt: RecordedGame.java
 * Popis:  Objekt uchovavajici zaznamenanou hru.
 * Autor:  Frantisek Spurny
 * Datum:  19.08.2015
 */
public class RecordedGame {

    private int id;
    private int gameId;

    public RecordedGame() {
    }

    public RecordedGame(int gameId) {
        this.gameId = gameId;
    }

    public RecordedGame(int id, int gameId) {
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
