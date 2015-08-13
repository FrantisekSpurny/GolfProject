package cz.spurny.Calculations;

/**
 * Objekt: ScoreCard.java
 * Popis:  Objekt Skore karty.
 * Autor:  Frantisek Spurny
 * Datum:  11.08.2015
 */

import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseInternal.Player;

public class ScoreCard {

    private List<ScoreCardLine> lines;
    private int sumPar;
    private int sumPersonalPar;
    private int sumScore;
    private int sumParDeviation;
    private int sumStableford;

    public ScoreCard() {
        lines = new ArrayList<>();
    }

    public ScoreCard(List<ScoreCardLine> lines, int sumPar, int sumPersonalPar, int sumScore, int sumParDeviation, int sumStableford) {
        this.lines = lines;
        this.sumPar = sumPar;
        this.sumPersonalPar = sumPersonalPar;
        this.sumScore = sumScore;
        this.sumParDeviation = sumParDeviation;
        this.sumStableford = sumStableford;
    }

    public List<ScoreCardLine> getLines() {
        return lines;
    }

    public void setLines(List<ScoreCardLine> lines) {
        this.lines = lines;
    }

    public int getSumPar() {
        return sumPar;
    }

    public void setSumPar(int sumPar) {
        this.sumPar = sumPar;
    }

    public int getSumPersonalPar() {
        return sumPersonalPar;
    }

    public void setSumPersonalPar(int sumPersonalPar) {
        this.sumPersonalPar = sumPersonalPar;
    }

    public int getSumScore() {
        return sumScore;
    }

    public void setSumScore(int sumScore) {
        this.sumScore = sumScore;
    }

    public int getSumParDeviation() {
        return sumParDeviation;
    }

    public void setSumParDeviation(int sumParDeviation) {
        this.sumParDeviation = sumParDeviation;
    }

    public int getSumStableford() {
        return sumStableford;
    }

    public void setSumStableford(int sumStableford) {
        this.sumStableford = sumStableford;
    }
}
