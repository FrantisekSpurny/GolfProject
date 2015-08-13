package cz.spurny.Calculations;

/**
 * Objekt: ScoreCardCounting.java
 * Popis:  Pomocne metody slouzici pro vypocet vsech hodnot Score karty.
 * Autor:  Frantisek Spurny
 * Datum:  11.08.2015
 */

import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;

public class ScoreCardCounting {

    /** Tato metoda vypocte scoreCartu pro jednoho hrace dane hry **/
    public static ScoreCard countScorecard(Game game,
                                           Player player,
                                           DatabaseHandlerInternal dbi,
                                           DatabaseHandlerResort dbr) {

        ScoreCard scoreCard = new ScoreCard(new ArrayList<ScoreCardLine>(),0,0,0,0,0);
        List<Hole> holes   = dbi.getAllHolesOfGame(game.getId());

        for (int i = 0; i < holes.size(); i++) {

            /* Vypocet radku skore karty */
            ScoreCardLine scl = countScoreOnHole(holes.get(i),
                    dbi.getScore(holes.get(i).getId(), player.getId(), game.getId()));

            scoreCard.getLines().add(scl);

            /* Vypocet sum */
            scoreCard.setSumPar         (scoreCard.getSumPar()         + scl.getPar());
            scoreCard.setSumPersonalPar (scoreCard.getSumPersonalPar() + scl.getPersonalPar());
            scoreCard.setSumScore       (scoreCard.getSumScore()       + scl.getScore());
            scoreCard.setSumParDeviation(scoreCard.getSumParDeviation()+ scl.getParDeviation());
            scoreCard.setSumStableford  (scoreCard.getSumStableford()  + scl.getStableford());
        }

        return scoreCard;
    }

    /** Vypocet skore pro jednu jamku **/
    public static ScoreCardLine countScoreOnHole(Hole hole,Score score) {
        ScoreCardLine scl = new ScoreCardLine();

        System.out.println(score == null ? "null":"score");

        /* Par */
        scl.setPar(hole.getPar());

        /* Os. par */
        scl.setPersonalPar(hole.getPar()+1); //TODO

        /* Score */
        if (score == null)
            scl.setScore(0);
        else
            scl.setScore(score.getScore());

        /* +-Par */
        if (score == null)
            scl.setParDeviation(0);
        else
            scl.setParDeviation(score.getScore()-hole.getPar());

        /* Stableford */
        scl.setStableford(1); //TODO

        return scl;
    }

    /** Vypocet zahranych ran danym hracem **/
    public static int playedHoles(ScoreCard sc) {

        List<ScoreCardLine> scLines = sc.getLines();
        int playedShots = 0;

        for (int i = 0; i < scLines.size(); i++) {
            if (scLines.get(i).getScore() != 0)
                playedShots++;
        }

        return playedShots;
    }

    /** Vypocet poctu "Eagle" skore **/
    public static int eagleCount(ScoreCard sc) {

        int eagleCount = 0;
        List<ScoreCardLine> scl = sc.getLines();

        for (int i = 0; i < scl.size(); i++) {
            if (scl.get(i).getPar() - 2 == scl.get(i).getScore())
                eagleCount++;
        }

        return eagleCount;
    }

    /** Vypocet poctu "Birdie" skore **/
    public static int birdieCount(ScoreCard sc) {

        int birdieCount = 0;
        List<ScoreCardLine> scl = sc.getLines();

        for (int i = 0; i < scl.size(); i++) {
            if (scl.get(i).getPar() - 1 == scl.get(i).getScore())
                birdieCount++;
        }

        return birdieCount;
    }

    /** Vypocet poctu "Par" skore **/
    public static int parCount(ScoreCard sc) {

        int parCount = 0;
        List<ScoreCardLine> scl = sc.getLines();

        for (int i = 0; i < scl.size(); i++) {
            if (scl.get(i).getPar() == scl.get(i).getScore())
                parCount++;
        }

        return parCount;
    }

    /** Vypocet poctu "Boogie" skore **/
    public static int boogieCount(ScoreCard sc) {

        int boogieCount = 0;
        List<ScoreCardLine> scl = sc.getLines();

        for (int i = 0; i < scl.size(); i++) {
            if (scl.get(i).getPar() + 1 == scl.get(i).getScore())
                boogieCount++;
        }

        return boogieCount;
    }

    /** Vypocet poctu "Boogie2" skore **/
    public static int boogie2Count(ScoreCard sc) {

        int boogie2Count = 0;
        List<ScoreCardLine> scl = sc.getLines();

        for (int i = 0; i < scl.size(); i++) {
            if (scl.get(i).getPar() + 2 == scl.get(i).getScore())
                boogie2Count++;
        }

        return boogie2Count;
    }

    /** Vypocet poctu "jine" skore **/
    public static int othersCount(ScoreCard sc) {

        int othersCount = 0;
        List<ScoreCardLine> scl = sc.getLines();
        int par,score;


        for (int i = 0; i < scl.size(); i++) {
            par   = scl.get(i).getPar();
            score = scl.get(i).getScore();

            if ((score < par-2 || score > par+2) && score != 0)
                othersCount++;
        }

        return othersCount;
    }

}
