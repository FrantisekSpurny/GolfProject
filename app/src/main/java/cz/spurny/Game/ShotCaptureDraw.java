package cz.spurny.Game;

/**
 * Objekt: ShotCaptureDraw.java
 * Popis:  Objekt obsahujici vsechny metody potrebne pro zaznamenani a vykresleni uzivatelem
 *         zadanych ran.
 * Autor:  Frantisek Spurny
 * Datum:  31.07.2015
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.Calculations.DistanceCalculations;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.Tee;
import cz.spurny.DatabaseResort.View;
import cz.spurny.GpsApi.GpsCoordinates;
import cz.spurny.GpsApi.GpsMethods;
import cz.spurny.Library.BitmapConversion;
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
    private View                    view;
    private TouchImageView          tivCourseImage;
    private DatabaseHandlerResort   dbr;
    private DatabaseHandlerInternal dbi;
    private Context                 context;

    /** Stetce **/
    Paint paintLine,paintText,paintTextLarge,paintCircle,paintPuts;

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

    /** Priznaky **/
    private boolean fromSelection;
    private boolean destinationSelection;

    /** Drawable **/
    Drawable dShotDone,dWater,dOut,dBiozone;

    /*** INICIALIZACE ***/

    /** Konstruktor **/
    public ShotCaptureDraw(Game game,
                           Hole hole,
                           Tee tee,
                           Canvas canvas,
                           Bitmap bitmap,
                           View   view,
                           TouchImageView tivCourseImage,
                           DatabaseHandlerInternal dbi,
                           DatabaseHandlerResort   dbr,
                           Context context) {

        this.game           = game;
        this.player         = dbi.getMainPlayer();
        this.hole           = hole;
        this.tee            = tee;
        this.canvas         = canvas;
        this.bitmap         = bitmap;
        this.view           = view;
        this.tivCourseImage = tivCourseImage;
        this.dbi            = dbi;
        this.dbr            = dbr;
        this.shotList       = dbi.getAllShots(hole.getId(),game.getId());
        this.context        = context;

        if (shotList == null)
            shotList = new ArrayList<>();

        /* Incializace stetcu */
        initPaints();

        /* Inciializace drawables */
        initDrawables();

        /* Incializace priznaku */
        fromSelection        = false;
        destinationSelection = false;
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

        paintPuts = new Paint() {
            {
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeWidth(lineThickness);
                setAntiAlias(true);
                setColor(lineColor);
                setPathEffect(new DashPathEffect(new float[]{10, 10}, 5));
            }
        };
    }

    /** Inicilializace "Drawables" (vykreslitelnych objektu) **/
    public void initDrawables() {
        dShotDone = context.getResources().getDrawable(R.drawable.check);
        dWater    = context.getResources().getDrawable(R.drawable.water);
        dOut      = context.getResources().getDrawable(R.drawable.out);
        dBiozone  = context.getResources().getDrawable(R.drawable.biozone);
    }

    /** Inicializace prvni rany, uzivatel zatim nic nezadal **/
    public void initFirstShot() {
        Shot shot = new Shot();
        Point pointFrom = dbr.getTeePoint(hole.getId(),tee.getKind());

        /** Ostatni parametry **/
        shot.setGameId(game.getId());
        shot.setHoleId(hole.getId());
        shot.setClubId(determineClub(shot));
        shot.setNumber(1); // prvni rana
        shot.setDeviation(0); // defaultni hodnota je vzdy presna
        shot.setSpecification(ShotSpecification.STRAIGHT);
        shot.setBallPosition(BallPosition.OK);

        /** Bod "od" **/
        shot.setFromX(pointFrom.getPixelX());
        shot.setFromY(pointFrom.getPixelY());
        shot.setFromLatitude(pointFrom.getLatitude());
        shot.setFromlongitude(pointFrom.getLongitude());
        shot.setFromAreaType(AreaType.TEE);

        /** Bod "Kam" **/
        generateNextPoint(shot);

        /** Vzdalenost rany **/
        calculateShotDistance(shot);

        actualShot = shot;

        /** Uzivatel muze editovat polohu dopadu **/
        destinationSelection = true;

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
        shot.setToAreaType(AreaType.FAIRWAY);
    }

    /** Nastaveni NB 100 jako bodu dopadu **/
    public void setNextPointNb100(Shot shot) {
        Point nb100  = dbr.getPointNb100(hole.getId());
        shot.setToX(nb100.getPixelX());
        shot.setToY(nb100.getPixelY());
        shot.setToLatitude(nb100.getLatitude());
        shot.setToLongitude(nb100.getLongitude());
        shot.setToAreaType(AreaType.FAIRWAY);
    }

    /*** ULOZENI RANY ***/

    /** Ulozeni rany do databaze a inicializace dalsi rany **/
    public void saveActualShot() {

        System.out.println(actualShot.getNumber() + " - cislo rany");

        /* Vlozeni do databaze */
        dbi.createShot(actualShot);
        shotList.add(actualShot);

        /* Uprava prumerne vzdalenosti rany pouzite hole */
        recalculateAverageStrokeLenght(actualShot);

        /** Prekresleni **/
        reinitBitmap();
        drawShotList();

        /* Pokud rana dopadla na green, uzivatel bude zadavat skore jamky */
        if (actualShot.getToAreaType() == AreaType.GREEN) {
            ((GameOnHole) context).goToHoleScore();
            holeFinished();
            shotList.add(actualShot);
            return;
        }

        /* Tvorba nasledujici rany */
        initShot(actualShot);

        /* Vykresleni nove aktualni rany */
        drawActualShot();

        /** Aktualizace informacniho panelu **/
        ((GameOnHole)context).infoPanelCaptureShot();
    }

    /** Incializace rany **/
    public void initShot(Shot previousShot) {

        Shot shot = new Shot();

        /** Ostatni parametry **/
        shot.setGameId(game.getId());
        shot.setHoleId(hole.getId());
        shot.setClubId(determineClub(shot));
        shot.setNumber(shotList.size() + 1); // prvni rana
        shot.setDeviation(0); // defaultni hodnota je vzdy presna
        shot.setSpecification(ShotSpecification.STRAIGHT);
        shot.setBallPosition(BallPosition.OK);

        /** Bod "od" **/
        generateFromPoint(previousShot, shot);

        /** Bod "Kam" **/
        generateNextPoint(shot);

        /** Vzdalenost rany **/
        calculateShotDistance(shot);

        actualShot = shot;

        /** Uzivatel muze editovat polohu dopadu **/
        destinationSelection = true;

        return;
    }

    /** Urceni bodu odpalu podle predchozi rany **/
    public void generateFromPoint(Shot previousShot,Shot shot) {

        switch (previousShot.getBallPosition()) {

            case BallPosition.OK:
                pointPreviousTo(previousShot, shot,false);
                break;
            case BallPosition.DROP_FREE:
                pointPreviousTo(previousShot, shot,true);
                break;
            case BallPosition.DROP_PENALTY:
                pointDropPenalty(previousShot,shot);
                break;
            case BallPosition.LOST_BALL:
                pointPreviousFrom(previousShot, shot,true);
                break;
            case BallPosition.NEW_PENALTY:
                pointPreviousFrom(previousShot,shot,true);
                break;
        }
    }

    /** Bod_odkud = bod_kam - 1 **/
    public void pointPreviousTo(Shot previousShot,Shot shot,boolean modification) {
        shot.setFromX        (previousShot.getToX());
        shot.setFromY        (previousShot.getToY());
        shot.setFromLatitude (previousShot.getToLatitude());
        shot.setFromlongitude(previousShot.getToLongitude());
        shot.setFromAreaType(previousShot.getToAreaType());

        // TODO nelze modifikovat polohu
    }

    /** Bod_odkud = bod_odkud -1 **/
    public void pointPreviousFrom(Shot previousShot,Shot shot,boolean modification) {
        shot.setFromX        (previousShot.getFromX());
        shot.setFromY        (previousShot.getFromY());
        shot.setFromLatitude (previousShot.getFromLatitude());
        shot.setFromlongitude(previousShot.getFromlongitude());
        shot.setFromAreaType(previousShot.getFromAreaType());

        // TODO nelze modifikovat polohu
    }

    /** Bod odpalu pro polohu "Drop s trestnou" **/
    public void pointDropPenalty(Shot previousShot,Shot shot) {

        // TODO tady dodat vypocet
        shot.setFromX        (previousShot.getToX());
        shot.setFromY        (previousShot.getToY());
        shot.setFromLatitude (previousShot.getToLatitude());
        shot.setFromlongitude(previousShot.getToLongitude());
        shot.setFromAreaType(previousShot.getToAreaType());
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

    /** Prepocet prumerne vzdalenosti odpalu u hole **/
    public void recalculateAverageStrokeLenght(Shot shot) {

        Club club           = dbi.getClub(shot.getClubId());
        double shotDistance = shot.getDistance();
        double currentASL   = club.getAverageStrokeLength();

        /* Nastaveni nove prumerne vzdalenosti */
        club.setAverageStrokeLength((currentASL + shotDistance) / 2);

        /* Aktualizace databaze */
        dbi.updateClub(club);
    }

    /** Byla jiz zadana posledni rana ? **/
    public boolean isLastShot() {

        if (shotList.size() == 0)
            return false;

        /* Posledni rana vede na GREEN a pro danou jamku bylo zadano skore */
        if (shotList.get(shotList.size()-1).getToAreaType() == AreaType.GREEN
            && dbi.getScore(hole.getId(),player.getId(),game.getId()) != null)
            return true;
        else
            return false;
    }

    /** Jamka uz je uzavrena zadavat rany **/
    public void holeFinished() {

        /* Nelze vybirat nove body */
        destinationSelection = false;
        fromSelection        = false;

        /* Nelze zadavat nove body */
        ((GameOnHole)context).getMiChangeFrom().setEnabled(false);
        ((GameOnHole)context).getMiChangeTo()  .setEnabled(false);
        ((GameOnHole)context).getMiSaveShot()  .setEnabled(false);
    }

    /** Urceni zdali se dany bod nachazi na greenu **/
    public boolean isOnGreen(Point point) {

        Point greenStart = dbr.getPointGreenStart(hole.getId());
        Point greenEnd   = dbr.getPointGreenEnd  (hole.getId());

        int yStart = greenStart.getPixelY();
        int yPoint = point     .getPixelY();
        int yEnd   = greenEnd  .getPixelY();

        if (yStart >= yPoint && yPoint >= yEnd)
            return true;

        return false;
    }

    /*** VYKRESLOVANI ***/

    /** Vykresleni aktualni rany **/
    public void drawActualShot() {

        if (actualShot == null)
            return;

        int x1 = actualShot.getFromX();
        int x2 = actualShot.getToX();
        int y1 = actualShot.getFromY();
        int y2 = actualShot.getToY();

        /** Vykresleni linky **/
        canvas.drawLine(x1,y1,x2,y2,paintLine);

        /** Vykresleni textu rany na linku **/
        drawText(x1, y1, x2, y2, actualShot);

        /** Vykresleni "puntiku" na zacatku linky **/
        drawLineStart(actualShot);

        /** Vykresleni konce linky (sipka a otaznik) **/
        drawActiveLineEnd();

        /** Obnoveni obrazku **/
        tivCourseImage.invalidate();
    }

    /** Vykresleni vsech ostatnich ran **/
    public void drawShotList() {

        if (shotList.size() <= 0)
            return;

        for (int i = 0; i < shotList.size(); i++) {
            drawShot(shotList.get(i));
        }

        /** Obnoveni obrazku **/
        tivCourseImage.invalidate();
    }

    /** Vykresleni jedne rany **/
    public void drawShot(Shot shot) {

        int x1 = shot.getFromX();
        int x2 = shot.getToX();
        int y1 = shot.getFromY();
        int y2 = shot.getToY();

        /** Vykresleni linky **/
        canvas.drawLine(x1, y1, x2, y2, paintLine);

        /** Vykresleni textu rany na linku **/
        drawText(x1, y1, x2, y2, shot);

        /** Vykresleni "puntiku" na zacatku linky **/
        drawLineStart(shot);

        /** Vykresleni konce linky (sipka a otaznik) **/
        drawLineEnd(shot);
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

    /** Vykresleni "puntiku na konci linky. Jeho velikost je ovlivnena cislem rany" **/
    public void drawLineEnd(Shot shot) {

        int x2 = shot.getToX();
        int y2 = shot.getToY();

        /* U rany na green rany je vykreslen vetsi "puntik" */
        if (shot.getToAreaType() == AreaType.GREEN)
            canvas.drawCircle(x2,y2,circleRadius*2,paintCircle);
        else {
            canvas.drawCircle(x2,y2,circleRadius,paintCircle);
        }

        /* Vykresleni symbolu na zaklade plochy dopadu */
        drawLineEndSymbol(shot);
    }

    /** Vykresleni symbolu na konci rany na zaklade plochy dopadu **/
    public void drawLineEndSymbol(Shot shot) {

        Drawable drawable = dShotDone;

        int x2 = shot.getToX();
        int y2 = shot.getToY();

        /** Vypocet souradnic pro vykresleni **/
        int x = x2-textMargin-textSize;
        int y = y2-textSize;

        switch (shot.getToAreaType()) {

            /* Vse v poradku */
            case AreaType.FAIRWAY:
            case AreaType.GREEN:
            case AreaType.SEMIROUGH:
            case AreaType.TEE:
                drawable = dShotDone;
                break;
            /* Spatny povrch */
            case AreaType.ROUGHT:
            case AreaType.BIOZONE:
            case AreaType.BUNKER:
                drawable = dBiozone;
               break;
            /* Voda */
            case AreaType.WATER:
                drawable = dWater;
                break;
            /* Out */
            case AreaType.OUT:
                drawable = dOut;
                break;
        }

        drawable.setBounds(x, y, x + textSize * 2, y + textSize * 2);
        drawable.draw(canvas);
    }

    /** Vykresleni konce aktivni rany **/
    private void drawActiveLineEnd() {

        int x1 = actualShot.getFromX();
        int x2 = actualShot.getToX();
        int y1 = actualShot.getFromY();
        int y2 = actualShot.getToY();

        /** Vykresleni sipky **/
        drawArrowHead(x1, x2, y1, y2, arrowWidht, arrowHeight);

        /** Vykresleni otazniku **/
        canvas.drawText("?", x2 - textMargin / 2 - textSize, y2 + textSize, paintTextLarge);
    }

    /** Vykresleni sipky na konci linky **/
    public void drawArrowHead(int x1,int x2,int y1,int y2,int d,int h) {

        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
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
        path.lineTo((int) xm, (int) ym);

        canvas.drawPath(path, paintLine);
    }

    /** Vykreslovani patu **/
    public void drawPuts() {

        if (!isLastShot())
            return;

        /* Ziskani bodu zacatku a konce greenu */
        Point greenStart = dbr.getPointGreenStart(view.getId());
        Point greenEnd   = dbr.getPointGreenEnd  (view.getId());

        int x1 = greenStart.getPixelX();
        int x2 = greenEnd  .getPixelX();
        int y1 = greenStart.getPixelY();
        int y2 = greenEnd  .getPixelY();

        /* Vypocet stredoveho bodu */
        int x3,y3;

        if (x1 >= x2)
            x3 = x2 +  Math.abs(x1-x2)/2;
        else
            x3 = x1 +  Math.abs(x1-x2)/2;

        if (y1 > y2)
            y3 = y2 + Math.abs(y1-y2)/2;
        else
            y3 = y1 + Math.abs(y1-y2)/2;

        /* Vypocet vzdalenosti bodu */
        int radius = DistanceCalculations.pointDistancePx(greenStart,greenEnd);

        /* Vykresleni kruhu pres green */
        canvas.drawCircle(x3,y3,radius/2,paintPuts);

        /* Vykresleni textu */
        String putsText = dbi.getScore(hole.getId(),player.getId(),game.getId()).getPuts()
                          + " "
                          + context.getString(R.string.ShotCaptureDraw_string_puts);
        canvas.drawText(putsText,x3 + radius/2 + textMargin,y3+textSize,paintTextLarge);
    }

    /** Prekresleni bitmapy **/
    public void reinitBitmap() {

        /* Nacteni puvodni bitmapy */
        bitmap = BitmapConversion
                .convertToMutable(BitmapFactory.decodeByteArray(view.getImage(), 0, view.getImage().length));

        /* Obnoveni */
        canvas.setBitmap(bitmap);
        tivCourseImage.setImageBitmap(bitmap);
        tivCourseImage.invalidate();
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

    public boolean isFromSelection() {
        return fromSelection;
    }

    public void setFromSelection(boolean fromSelection) {
        this.fromSelection = fromSelection;
    }

    public boolean isDestinationSelection() {
        return destinationSelection;
    }

    public void setDestinationSelection(boolean destinationSelection) {
        this.destinationSelection = destinationSelection;
    }

    public Shot getLastPlayedShot() {
        if (shotList.size() == 0)
            return null;
        else
            return shotList.get(shotList.size()-1);
    }
}