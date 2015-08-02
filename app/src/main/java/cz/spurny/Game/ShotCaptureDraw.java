package cz.spurny.Game;

/**
 * Objekt: ShotCaptureDraw.java
 * Popis:  Objekt obsahujici vsechny metody potrebne pro zaznamenani a vykresleni uzivatelem
 *         zadanych ran.
 * Autor:  Frantisek Spurny
 * Datum:  31.07.2015
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.Tee;
import cz.spurny.GpsApi.GpsCoordinates;
import cz.spurny.GpsApi.GpsMethods;
import cz.spurny.Library.TouchImageView;

public class ShotCaptureDraw {

    /*** ATRIBUTY ***/
    private Shot                    actualShot;
    private List<Shot>              shotList;
    private Game                    game;
    private Player                  player;
    private Tee                     tee;
    private Hole                    hole;
    private Canvas                  canvas;
    private Bitmap                  bitmap;
    private TouchImageView          tivCourseImage;
    private DatabaseHandlerResort   dbr;
    private DatabaseHandlerInternal dbi;

    /** Stetce **/
    Paint paintLine,paintText,paintTextLarge,paintCircle;

    /** Parametry vykreslovani **/
    int    lineThickness   = 2;           // Sirka vykreslenych linek
    int    lineColor       = Color.WHITE; // barva linek
    int    lineBorderLeft  = 20;          // Velikost leveho okraje od linek
    int    textColor       = Color.WHITE; // Barva textu
    int    textSize        = 14;          // Velikost pisma
    int    textMargin      = 20;          // Odsazeni textu
    float  circleRadius    = 4;           // Velikost "puntiku"
    int    circleColor     = Color.WHITE; // Barva "puntiku"
    int    arrowWidht      = 20;           // Sirka sipky
    int    arrowHeight     = 7;          // Vyska sipky

    /*** INICIALIZACE ***/

    /** Konstruktor **/
    public ShotCaptureDraw(Game game,
                           Hole hole,
                           Tee tee,
                           Canvas canvas,
                           Bitmap bitmap,
                           TouchImageView tivCourseImage,
                           DatabaseHandlerInternal dbi,
                           DatabaseHandlerResort   dbr) {

        this.game           = game;
        this.player         = dbi.getMainPlayer();
        this.hole           = hole;
        this.tee            = tee;
        this.canvas         = canvas;
        this.bitmap         = bitmap;
        this.tivCourseImage = tivCourseImage;
        this.dbi            = dbi;
        this.dbr            = dbr;
        this.shotList       = dbi.getAllShots(hole.getId(),game.getId());

        /* Incializace stetcu */
        initPaints();
    }

    /** Inicializace stetcu pro vykreslovani **/
    public void initPaints() {

        paintLine = new Paint() {
            {
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeWidth(lineThickness);
                setAntiAlias(true);
                setColor(lineColor);
            }
        };

        paintText = new Paint() {
            {
                setColor(textColor);
                setTextSize(textSize);
                setAntiAlias(true);
            }
        };

        paintTextLarge = new Paint() {
            {
                setColor(textColor);
                setTextSize(textSize * 2);
                setAntiAlias(true);
            }
        };

        paintCircle = new Paint() {
            {
                setColor(circleColor);
                setAntiAlias(true);
            }
        };

    }

    /** Inicializace prvni rany, uzivatel zatim nic nezadal **/
    public void initFirstShot() {
        Shot shot = new Shot();
        Point pointFrom = dbr.getTeePoint(hole.getId(),tee.getKind());

        /** Bod "od" **/
        shot.setFromX(pointFrom.getPixelX());
        shot.setFromY(pointFrom.getPixelY());
        shot.setFromLatitude(pointFrom.getLatitude());
        shot.setFromlongitude(pointFrom.getLongitude());
        shot.setFromAreaType(AreaType.FAIRWAY);

        /** Bod "Kam" **/
        generateNextPoint(shot);

        /** Vzdalenost rany **/
        calculateShotDistance(shot);

        /** Ostatni parametry **/
        shot.setGameId(game.getId());
        shot.setHoleId(hole.getId());
        shot.setClubId(determineClub(shot));
        shot.setNumber(1); // prvni rana
        shot.setDeviation(0); // defaultni hodnota je vzdy presna
        shot.setSpecification(ShotSpecification.STRAIGHT);
        shot.setBallPosition(BallPosition.OK);

        actualShot = shot;

        return;
    }

    /** Podle toho o kolikatou ranu se jedna a toho jaky ma jamka par urcujeme polohu dopadu. **/
    public void generateNextPoint(Shot shot) {

        /** Jedna se o prvni ranu **/
        if (shot.getNumber() == 1) {
            if (hole.getPar() == 3)
                setNextPointGreen(shot);
            else
                setNextPointNbDrive(shot);
        }
        /** Jedna se o druhou ranu **/
        else if (shot.getNumber() == 2) {
            if (hole.getPar() == 3 || hole.getPar() == 4)
                setNextPointGreen(shot);
            else
                setNextPointNb100(shot);
        }
        /** Jedna se o treti a dalsi rany **/
        else {
            setNextPointGreen(shot);
        }
    }

    /** Nastaveni greenu jako bodu dopadu **/
    public void setNextPointGreen(Shot shot) {

        Point green  = dbr.getPointGreen(hole.getId());
        shot.setToX(green.getPixelX());
        shot.setToY(green.getPixelY());
        shot.setToLatitude(green.getLatitude());
        shot.setToLongitude(green.getLongitude());
        shot.setToAreaType(AreaType.GREEN);
    }

    /** Nastaveni NB Drive jako bodu dopadu **/
    public void setNextPointNbDrive(Shot shot) {
        Point nbDrive  = dbr.getPointNbDrive(hole.getId());
        shot.setToX(nbDrive.getPixelX());
        shot.setToY(nbDrive.getPixelY());
        shot.setToLatitude(nbDrive.getLatitude());
        shot.setToLongitude(nbDrive.getLongitude());
        shot.setToAreaType(AreaType.GREEN);
    }

    /** Nastaveni NB 100 jako bodu dopadu **/
    public void setNextPointNb100(Shot shot) {
        Point nb100  = dbr.getPointNb100(hole.getId());
        shot.setToX(nb100.getPixelX());
        shot.setToY(nb100.getPixelY());
        shot.setToLatitude(nb100.getLatitude());
        shot.setToLongitude(nb100.getLongitude());
        shot.setToAreaType(AreaType.GREEN);
    }

    /*** POMOCNE METODY ***/

    /** Vypocet vzdalenosti rany **/
    public void calculateShotDistance(Shot shot) {
        GpsCoordinates from =  new GpsCoordinates(shot.getFromLatitude(),shot.getFromlongitude());
        GpsCoordinates to   = new GpsCoordinates(shot.getToLatitude(),shot.getToLongitude());

        shot.setDistance(GpsMethods.getDistance(from, to));
    }

    /** Urceni vhodne hole **/
    public int determineClub(Shot shot) {

        List<Club> clubs = dbi.getAllClubs();
        double diference = Double.POSITIVE_INFINITY;
        int clubId = -1;

        /** Uzivatel nevytvoril zadne hole **/
        if (clubs == null)
            return -1;

        /** Existuji hole **/
        else {
            for (int i = 0; i < clubs.size(); i++) {
                if (shot.getDistance() - clubs.get(i).getStandardStrokeLength() < diference) {
                    clubId = clubs.get(i).getId();
                    diference = shot.getDistance() - clubs.get(i).getStandardStrokeLength();
                }
            }
        }

        return clubId;
    }

    /*** VYKRESLOVANI ***/

    /** Vykresleni aktualni rany **/
    public void drawActualShot() {

        int x1 = actualShot.getFromX();
        int x2 = actualShot.getToX();
        int y1 = actualShot.getFromY();
        int y2 = actualShot.getToY();

        /** Vykresleni linky **/
        canvas.drawLine(x1,y1,x2,y2,paintLine);

        /** Vykresleni textu rany na linku **/
        drawText(x1,y1,x2,y2,actualShot);

        /** Vykresleni "puntiku" na zacatku linky **/
        drawLineStart(actualShot);

        /** Vykresleni konce linky (sipka a otaznik) **/
        drawActiveLineEnd();

        /** Obnoveni obrazku **/
        tivCourseImage.invalidate();
    }

    /** Vykresleni textu na linku **/
    public void drawText(int x1,int y1,int x2,int y2,Shot shot) {

        /** Vypocet pozice pro text **/
        int x3,y3;

        if (x1 >= x2)
            x3 = x2 +  Math.abs(x1-x2)/2 + lineBorderLeft;
        else
            x3 = x1 +  Math.abs(x1-x2)/2 + lineBorderLeft;

        if (y1 > y2)
            y3 = y2 + Math.abs(y1-y2)/2;
        else
            y3 = y1 + Math.abs(y1-y2)/2;

        /** Tvorba a vykresleni textu **/

        /* Prvni radek textu - cislo jamky a pouzita hul */
        String lineText = shot.getNumber() + " - " + //cislo jamky
                          "(" + dbi.getClub(shot.getClubId()).getName() + ")";
        canvas.drawText(lineText, x3, y3, paintTextLarge);

        /* Druhy radek textu - delka rany */
        y3 += textSize + textMargin;
        lineText = shot.getDistance() + "m";
        canvas.drawText(lineText, x3, y3, paintTextLarge);
    }

    /** Vykresleni "puntiku" na zacatku linky. Jeho velikost je ovlivnena cislem rany **/
    private void drawLineStart(Shot shot) {

        int x = shot.getFromX();
        int y = shot.getFromY();

        /* U prvni rany je vykreslen vetsi "puntik" */
        if (shot.getNumber() == 1)
            canvas.drawCircle(x,y,circleRadius*2,paintCircle);
        else
            canvas.drawCircle(x,y,circleRadius,paintCircle);
    }

    /** Vykresleni konce aktivni rany **/
    private void drawActiveLineEnd() {

        int x1 = actualShot.getFromX();
        int x2 = actualShot.getToX();
        int y1 = actualShot.getFromY();
        int y2 = actualShot.getToY();

        /** Vykresleni sipky **/
        drawArrowHead(x1,x2,y1,y2,arrowWidht,arrowHeight);
    }

    /** Vykresleni sipky na konci linky **/
    public void drawArrowHead(int x1,int x2,int y1,int y2,int d,int h) {

        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy/D, cos = dx/D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        Path path =  new Path();
        path.reset();
        path.moveTo((int)xn,(int)yn);
        path.lineTo(x2,y2);
        path.lineTo((int)xm,(int)ym);

        canvas.drawPath(path,paintLine);
    }

    /*** GETTERS AND SETTERS ***/
    public Shot getActualShot() {
        return actualShot;
    }

    public void setActualShot(Shot actualShot) {
        this.actualShot = actualShot;
    }

    public List<Shot> getShotList() {
        return shotList;
    }

    public void setShotList(List<Shot> shotList) {
        this.shotList = shotList;
    }
}