package cz.spurny.DatabaseInternal;

/**
 * Objekt: Score.java
 * Popis:  Objekt udrzujici skore jednoho hrace na jedne jamce v ramci jedne hry.
 * Autor:  Frantisek Spurny
 * Datum:  25.07.2015
 */

public class Score {
    private int id;
    private int gameId;
    private int holeId;
    private int playerId;
    private int score;
    private int puts;
    private int penaltyShots;

    public Score() {
    }

    public Score(int gameId, int holeId, int playerId, int score, int puts, int penaltyShots) {
        this.gameId = gameId;
        this.holeId = holeId;
        this.playerId = playerId;
        this.score = score;
        this.puts = puts;
        this.penaltyShots = penaltyShots;
    }

    public Score(int id, int gameId, int holeId, int playerId, int score, int puts, int penaltyShots) {
        this.id = id;
        this.gameId = gameId;
        this.holeId = holeId;
        this.playerId = playerId;
        this.score = score;
        this.puts = puts;
        this.penaltyShots = penaltyShots;
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

    public int getHoleId() {
        return holeId;
    }

    public void setHoleId(int holeId) {
        this.holeId = holeId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPuts() {
        return puts;
    }

    public void setPuts(int puts) {
        this.puts = puts;
    }

    public int getPenaltyShots() {
        return penaltyShots;
    }

    public void setPenaltyShots(int penaltyShots) {
        this.penaltyShots = penaltyShots;
    }
}
