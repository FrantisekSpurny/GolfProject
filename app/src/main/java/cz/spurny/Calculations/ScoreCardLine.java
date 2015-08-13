package cz.spurny.Calculations;

/**
 * Objekt: ScoreCardLine.java
 * Popis:  Jeden radek skore karty.
 * Autor:  Frantisek Spurny
 * Datum:  11.08.2015
 */
public class ScoreCardLine {

    private int par;
    private int personalPar;
    private int score;
    private int parDeviation;
    private int stableford;

    public ScoreCardLine() {
    }

    public ScoreCardLine(int par, int personalPar, int score, int parDeviation, int stableford) {
        this.par = par;
        this.personalPar = personalPar;
        this.score = score;
        this.parDeviation = parDeviation;
        this.stableford = stableford;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public int getPersonalPar() {
        return personalPar;
    }

    public void setPersonalPar(int personalPar) {
        this.personalPar = personalPar;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getParDeviation() {
        return parDeviation;
    }

    public void setParDeviation(int parDeviation) {
        this.parDeviation = parDeviation;
    }

    public int getStableford() {
        return stableford;
    }

    public void setStableford(int stableford) {
        this.stableford = stableford;
    }
}
