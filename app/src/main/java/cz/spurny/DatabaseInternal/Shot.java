package cz.spurny.DatabaseInternal;

/**
 * Objekt: Shot.java
 * Popis:  Objekt slouzici pro uchovani jednotlivych ran.
 * Autor:  Frantisek Spurny
 * Datum:  26.07.2015
 */

public class Shot {

    private int     id;
    private int     gameId;
    private int     holeId;
    private int     clubId;
    private int     number;
    private double  fromLatitude;
    private double  fromlongitude;
    private int     fromX;
    private int     fromY;
    private int     fromAreaType;
    private double  toLatitude;
    private double  toLongitude;
    private int     toX;
    private int     toY;
    private int     toAreaType;
    private double  distance;
    private double  deviation;
    private String  specification;

    public Shot() {
    }

    public Shot(int gameId,
                int holeId,
                int clubId,
                int number,
                double fromLatitude,
                double fromlongitude,
                int fromX,
                int fromY,
                int fromAreaType,
                double toLatitude,
                double toLongitude,
                int toX,
                int toY,
                int toAreaType,
                double distance,
                double deviation,
                String specification) {

        this.gameId = gameId;
        this.holeId = holeId;
        this.clubId = clubId;
        this.number = number;
        this.fromLatitude = fromLatitude;
        this.fromlongitude = fromlongitude;
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromAreaType = fromAreaType;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.toX = toX;
        this.toY = toY;
        this.toAreaType = toAreaType;
        this.distance = distance;
        this.deviation = deviation;
        this.specification = specification;
    }

    public Shot(int id,
                int gameId,
                int holeId,
                int clubId,
                int number,
                double fromLatitude,
                double fromlongitude,
                int fromX,
                int fromY,
                int fromAreaType,
                double toLatitude,
                double toLongitude,
                int toX,
                int toY,
                int toAreaType,
                double distance,
                double deviation,
                String specification) {

        this.id = id;
        this.gameId = gameId;
        this.holeId = holeId;
        this.clubId = clubId;
        this.number = number;
        this.fromLatitude = fromLatitude;
        this.fromlongitude = fromlongitude;
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromAreaType = fromAreaType;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.toX = toX;
        this.toY = toY;
        this.toAreaType = toAreaType;
        this.distance = distance;
        this.deviation = deviation;
        this.specification = specification;
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

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public double getFromlongitude() {
        return fromlongitude;
    }

    public void setFromlongitude(double fromlongitude) {
        this.fromlongitude = fromlongitude;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getFromAreaType() {
        return fromAreaType;
    }

    public void setFromAreaType(int fromAreaType) {
        this.fromAreaType = fromAreaType;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

    public int getToAreaType() {
        return toAreaType;
    }

    public void setToAreaType(int toAreaType) {
        this.toAreaType = toAreaType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDeviation() {
        return deviation;
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
