package cz.spurny.Calculations;

/**
 * Objekt: DistanceCalculations.java
 * Popis:  Pomocne metody pro vypocty vzdalenosti.
 * Autor:  Frantisek Spurny
 * Datum:  14.07.2015
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.GameCourse;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.Tee;
import cz.spurny.GpsApi.GpsCoordinates;
import cz.spurny.GpsApi.GpsMethods;

public class DistanceCalculations {

    /** Vypocet delky vsech jamek dane hry **/
    public static List<Double> getLenghtOfAllHoles(int idGame,Context context) {

        /* Pripojeni databazi */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
        DatabaseHandlerResort   dbr = new DatabaseHandlerResort  (context);

        /* Ziskani kontextu hry */
        Game             game       = dbi.getGame(idGame);
        List<GameCourse> gameCourse = dbi.getAllGameCourseOfGame(idGame);

        /* Vydledne vzdalenosti */
        List<Double> distances = new ArrayList<>();

        /* Projdi vsechny hriste dane hry */
        for (int i = 0; i < gameCourse.size(); i++) {

            /* Seznam jamek daneho hriste a zvolene odpaliste*/
            List<Hole> holes = dbr.getAllHolesOnCourse(gameCourse.get(i).getIdCourse());
            Tee tee          = dbr.getTee             (gameCourse.get(i).getIdTee());

            for (int j = 0; j < holes.size(); j++) {
                distances.add(getLengthOfHole(holes.get(j),tee,dbi,dbr));
            }
        }

        /* Uzavreni databazi */
        dbi.close();
        dbr.close();

        return distances;
    }

    /* Vypocet vzdalenosti mezi zvolenym odpalistem a greenem dane jamky */
    public static double getLengthOfHole(Hole hole,Tee tee,DatabaseHandlerInternal dbi,DatabaseHandlerResort dbr) {

        /* Ziskani bodu green a odpaliste */
        Point greenPoint = dbr.getPointGreen(hole.getId());
        Point teePoint   = dbr.getTeePoint(hole.getId(),tee.getKind());

        /* Definice GPS koordinant */
        GpsCoordinates gspGreen =  new GpsCoordinates(greenPoint.getLatitude(),greenPoint.getLongitude());
        GpsCoordinates gspTee   =  new GpsCoordinates(teePoint.getLatitude()  ,teePoint.getLongitude());

        /* Vypocet samotne vzdalenosti */
        return GpsMethods.getDistance(gspGreen,gspTee);
    }

    /** Vypocet vzdalenosti 2 bodu v px **/
    public static int pointDistancePx(Point p1,Point p2) {
        int x1 = p1.getPixelX();
        int x2 = p2.getPixelX();
        int y1 = p1.getPixelY();
        int y2 = p2.getPixelY();

        return (int) (Math.sqrt(Math.pow(Math.abs(x1 - x2),2) + Math.pow(Math.abs(y1 - y2),2)));
    }

    /** Vypocet vzdalenosti 2 bodu v m **/
    public static double pointDistanceM(Point p1,Point p2) {
        GpsCoordinates c1 = new GpsCoordinates(p1.getLatitude(),p1.getLongitude());
        GpsCoordinates c2 = new GpsCoordinates(p2.getLatitude(),p2.getLongitude());

        return GpsMethods.getDistance(c1,c2);
    }

}
