package cz.spurny.DatabaseInternal;

/**
 * Objekt: .java
 * Popis:
 * Autor:  Frantisek Spurny
 * Datum:  22.06.2015
 */
public class GamePlayer {

    private int id;
    private int idGame;
    private int idPlayer;

    public GamePlayer() {
    }

    public GamePlayer(int id, int idGame, int idPlayer) {
        this.id = id;
        this.idGame = idGame;
        this.idPlayer = idPlayer;
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

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }
}
