package cz.spurny.Calculations;

/**
 * Objekt: ScoreCardCounting.java
 * Popis:  Pomocne metody slouzici pro vypocet vsech hodnot Score karty.
 * Autor:  Frantisek Spurny
 * Datum:  11.08.2015
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.DatabaseResort.Course;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Tee;

public class ScoreCardCounting {

    /** Tato metoda vypocte scoreCartu pro jednoho hrace dane hry **/
    public static ScoreCard countScorecard(Game game,
                                           Player player,
                                           DatabaseHandlerInternal dbi,
                                           DatabaseHandlerResort dbr,
                                           Context context) {

        ScoreCard scoreCard = new ScoreCard(new ArrayList<ScoreCardLine>(),0,0,0,0,0);
        List<Hole> holes   = dbi.getAllHolesOfGame(game.getId());

        /* Vypocet osobniho paru */
        Tee tee = dbr.getTee(dbi.getAllGameCourseOfGame(game.getId()).get(0).getIdTee());

        int personalPar[]  = countPersonalPar(tee.getId(),player,game,context);

        for (int i = 0; i < holes.size(); i++) {

            /* Vypocet radku skore karty */
            ScoreCardLine scl = countScoreOnHole(holes.get(i),
                    dbi.getScore(holes.get(i).getId(), player.getId(), game.getId()));
            scl.setPersonalPar(personalPar[i]);

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
        if (score == null)
            scl.setStableford(0);
        else
            scl.setStableford(countStableford(score.getScore()-hole.getPar()));

        return scl;
    }

    /** Vypocet stableford bodu **/
    public static int countStableford (int diff) {

        switch (diff) {
            case 0:
                return 2;
            case 1:
                return 1;
            case -1:
                return 3;
            case -2:
                return 4;
            case -3:
                return 5;
            case -4:
                return 6;
            default:
                return 0;
        }
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

    /** Vypocet Os. paru **/
    public static int[] countPersonalPar(int teeId,
                                         Player player,
                                         Game game,
                                         Context context) {

        int[] adition;
        int[] personalPar = new int[18];

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
        DatabaseHandlerResort   dbr = new DatabaseHandlerResort  (context);

        Tee          tee     = dbr.getTee(teeId);
        List<Course> courses = dbi.getAllCoursesOfGame(game.getId());

        /* Vypocet hraciho handicapu */
        int playHcp = countPlayHcp(player.getHandicap().intValue(),tee.getCR(),tee.getSR(),getTotalPar(courses));

        /* Ziskani jamek jednotlivych hrist */
        List<Hole> holes = dbi.getAllHolesOfGame(game.getId());

        /* Vypocet prirustku k paru */
        if   (courses.size() == 0)
            adition = countForOneCourse(sortList(holes), playHcp);
        else
            adition = countForTwoCourses(sortList(holes.subList(0, 9)), sortList(holes.subList(9,18)),playHcp);

         /* Ziskani jamek jednotlivych hrist */
        holes = dbi.getAllHolesOfGame(game.getId());

        System.out.println("Hraci HCP: " + playHcp);

        for (int i = 0; i < personalPar.length; i++) {
            personalPar[i] = holes.get(i).getPar() + adition[i];

           // System.out.println("Jamka:      " + holes.get(i).getNumber());
           // System.out.println("Hriste:     " + holes.get(i).getIdCourse());
           // System.out.println("HCP:        " + holes.get(i).getHcp());
           // System.out.println("Par/Osobni: " + holes.get(i).getPar() + " / " + personalPar[i]);
        }
        
        /* Uzavreni databaze */
        dbi.close();
        dbr.close();

        return personalPar;
    }

    /** Vypocet osobniho paru pro jendo hriste **/
    public static int[] countForOneCourse (List<Hole> holes,int hcp) {

        int[] adition = new int[18];
        for (int i = 0; i < adition.length; i++) {
            adition[i] = 0;
        }

        for (int i = 0; i < holes.size(); i++) {

            if (hcp == 0)
                break;

            adition[holes.get(i).getNumber()-1] += 1;

            hcp--;
        }

        return adition;
    }

    public static int[] countForTwoCourses (List<Hole> holes1,List<Hole> holes2,int hcp) {

        int[] adition = new int[18];
        for (int i = 0; i < adition.length; i++) { adition[i] = 0; }
        int i=0,j=0;

        while (hcp > 0) {

            /* Prvni hriste */
            adition[holes1.get(i).getNumber()-1] += 1;
            i = ((i+1) > 8 ? 0 : (i+1));
            hcp--;

            if (hcp == 0)
                break;

            /* Druhe hriste */
            adition[holes2.get(j).getNumber()+8] += 1;
            j = ((j+1) > 8 ? 0 : (j+1));
            hcp--;
        }

        return adition;
    }


    /** Vypocet hraciho handicapu **/
    public static int countPlayHcp(int hcp,double cr,double sr,int par) {

        double playHcp    =  hcp * (sr / 113) + (cr - par);
        int    intPart    = (int)playHcp;
        double doublePart =  playHcp - intPart;

        System.out.println(hcp + " * (" + sr + " / 113) + (" + cr + " - " + par + ") = " + (intPart + (doublePart>=0.5 ? 1 : 0)) );

        return intPart + (doublePart>=0.5 ? 1 : 0);
    }

    /** Vypocet paru hriste **/
    public static int getTotalPar(List<Course> courses) {

        if (courses.size()==1)
            return courses.get(0).getPar();
        else
            return courses.get(0).getPar() + courses.get(1).getPar();
    }

    /* Serazeni listu jamek podle HCP */
    public static List<Hole> sortList(List<Hole> holes) {

        Collections.sort(holes,
                new Comparator<Hole>()
                {
                    public int compare(Hole hole2, Hole hole1) {
                        if (hole1.getHcp() == hole2.getHcp())
                            return 0;
                        else if (hole1.getHcp() < hole2.getHcp())
                            return -1;
                        return 1;
                    }
                });

        return holes;
    }

}
