package cz.spurny.Game;

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
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.View;
import cz.spurny.GpsApi.GpsCoordinates;
import cz.spurny.GpsApi.GpsMethods;
import cz.spurny.Library.BitmapConversion;
import cz.spurny.Library.TouchImageView;

/**
 * Objekt: Measure.java
 * Popis:  Objekt slouzici pro obsluhu funkcionality mereni na jamce.
 * Autor:  Frantisek Spurny
 * Datum:  15.07.2015
 */
public class MeasureDraw {

    /** Barvy **/
    private Paint linePaint;
    private Paint textPaint;
    private Paint textPaintLarge;

    /** Atributy **/
    private Point                   actualPoint;
    private Point                   destinationPoint;
    private Canvas                  canvas;
    private Bitmap                  bitmap;
    private DatabaseHandlerInternal dbi;
    private DatabaseHandlerResort   dbr;
    private TouchImageView          tivCourseImage;
    private View                    view;
    private int                     canvasWidth;
    private int                     canvasHeight;
    private int                     lineLenght;
    private Drawable                dActualPoint;
    private Drawable                dDestinationPoint;

    /** Parametry vykreslovani **/
    int    lineBorderLeft  = 20;  // Velikost leveho okraje do ktereho nesmi zasahovat krivky
    int    lineBorderRight = 0;   // [NEEDITOVAT]
    double lineLenghtRatio = 0.4; // Ovlivnuje velikost vykreslovanych car 0 = nejvetsi / 1 = zadne
    int    lineThickness   = 2;   // Sirka vykreslenych linek
    int    lineColor       = Color.BLUE; // barva linek
    int    lineOpacity     = 150; // Pruhlednost linek
    int    textSize        = 14;  // Velikost textu
    int    textMargin      = 3;   // odsazeni textu od linie

    /** Priznaky **/
    private boolean pointsVisible;
    private boolean fromSelection;
    private boolean destinationSelection;
    
    /** Konstruktor **/
    public MeasureDraw(Point actualPoint,
                       Point destinationPoint,
                       Canvas canvas,
                       Bitmap bitmap,
                       DatabaseHandlerInternal dbi,
                       DatabaseHandlerResort dbr,
                       TouchImageView tivCourseImage,
                       View view,
                       Drawable dActualPoint,
                       Drawable dDestinationPoint) {

        this.actualPoint = actualPoint;
        this.destinationPoint = destinationPoint;
        this.canvas = canvas;
        this.bitmap = bitmap;
        this.dbi = dbi;
        this.dbr = dbr;
        this.tivCourseImage = tivCourseImage;
        this.view = view;
        this.dActualPoint = dActualPoint;
        this.dDestinationPoint = dDestinationPoint;

        fromSelection        = false;
        destinationSelection = false;

        /* Inicializace barev */
        initPaint();

        /* Inicializace rozmeru */
        initDimensions();
    }

    /** Incializace stetcu a barev **/
    public void initPaint() {

        linePaint = new Paint() {
            {
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeWidth(lineThickness);
                setAntiAlias(true);
                setColor(lineColor);
                setPathEffect(new DashPathEffect(new float[]{10,10}, 5));
                setAlpha(lineOpacity);
            }
        };

        textPaint = new Paint() {
            {
                setColor(lineColor);
                setTextSize(textSize);
            }
        };

        textPaintLarge  = new Paint() {
            {
                setColor(lineColor);
                setTextSize(textSize*2);
            }
        };
    }

    /** Inicializace rozmeru **/
    public void initDimensions() {
        canvasHeight = canvas.getHeight();
        canvasWidth  = canvas.getWidth();
        lineLenght   = (int) (canvasWidth - (canvasWidth * lineLenghtRatio));
    }


    /*** VYKRESLENI VZDALENOSTI VYZNAMNYCH BODU ***/

    /** Metoda vykresli vsechny body nachazejici se pred aktualnim bodem */
    public void drawPoints() {

        /* Obnoveni bitmapy */
        reinitBitmap();

        /* Vykresleni zdrojoveho bodu */
        drawMeasureActualPoint();

        /* Nastavime priznak vykresleni vyznamnych bodu */
        pointsVisible = true;

        /* Nejprve je nutne ziskat vsechny body ktere se maji vykreslit */
        List<Point> pointsToDraw = getPointsToDraw();

        for (Point p:pointsToDraw)
            drawPoint(p);

        /* Po zakresleni vsech bodu je nutne aktualizovat bitmapu */
        tivCourseImage.invalidate();
    }

    /** Vykresleni jednoho bodu **/
    public void drawPoint(Point midlePoint) {

        /* Vypocet vzdalenosti od aktualniho bodu k bodu vykreslovanemu */
        int distancePx = DistanceCalculations.pointDistancePx(midlePoint, actualPoint);
        String distanceM = String.valueOf(DistanceCalculations.pointDistanceM(actualPoint,midlePoint)) + "m";

        /* Vypocet velikosti mista, ktere je nutne vynechat na text */
        lineBorderRight = distanceM.length() * textSize + textMargin;

        /* Vypocet krajnich bodu krivky */
        Point leftPoint  = countLeftPoint(midlePoint,distancePx);
        Point rightPoint = countRightPoint(midlePoint,distancePx);

        /* Tvorba cesty pro krivku */
        final Path path = new Path();
        path.moveTo(leftPoint.getPixelX(), leftPoint.getPixelY());
        path.quadTo(midlePoint.getPixelX(),midlePoint.getPixelY(),rightPoint.getPixelX(),rightPoint.getPixelY());

        /* vykresleni krivky */
        canvas.drawPath(path, linePaint);

        /* Vykresleni vzdalenosti linek od aktialniho bodu */
        canvas.drawText(distanceM, rightPoint.getPixelX() + textMargin, rightPoint.getPixelY() + textSize / 2, textPaint);
    }

    /** Vypocet souradnice leveho bodu krivky **/
    public Point countLeftPoint(Point midlePoint,int distance) {
        Point leftPoint = new Point();

        int x1 = midlePoint.getPixelX();
        int x2,y2;

        /* Vypocet x2 */
        if ((x1 - lineLenght/2) <= lineBorderLeft)
            x2 = lineBorderLeft;
        else
            x2 = x1 - lineLenght/2;

        /* Vypocet y2 */
        y2 = (int)Math.sqrt( Math.pow(distance,2) - Math.pow(Math.abs(x1-x2),2) );
        y2 = actualPoint.getPixelY() - y2;

        leftPoint.setPixelX(x2);
        leftPoint.setPixelY(y2);

        return leftPoint;
    }

    /** Vypocet souradnice leveho bodu krivky **/
    public Point countRightPoint(Point midlePoint,int distance) {
        Point rightPoint = new Point();

        int x1 = midlePoint.getPixelX();
        int x2,y2;

        /* Vypocet x2 */
        if ((x1 + lineLenght/2) >= canvasWidth - lineBorderRight)
            x2 = canvasWidth - lineBorderRight;
        else
            x2 = x1 + lineLenght/2;

        /* Vypocet y2 */
        y2 = (int)Math.sqrt( Math.pow(distance,2) - Math.pow(Math.abs(x1-x2),2) );
        y2 = actualPoint.getPixelY() - y2;

        rightPoint.setPixelX(x2);
        rightPoint.setPixelY(y2);

        return rightPoint;
    }

    /** Ziskani vsech bodu ktere se maji vykreslit **/
    public List<Point> getPointsToDraw() {
        List<Point> pointsToDraw =  new ArrayList<>();
        List<Point> allPoints = dbr.getAllPointsOfView(view.getId());

        /** Filtrovani bodu **/
        for (Point p:allPoints) {

            /* filtrovani odpalist */
            if (p.getType().startsWith("T"))
                continue;

            /* Filtrujeme body za aktualnim bodem */
            if (p.getPixelY() >= actualPoint.getPixelY() - canvasHeight * 0.15 )
                continue;

            /* TODO tady mohou byt pripadne filtry na to co se bude zobrazovat */

            pointsToDraw.add(p);
        }

        return pointsToDraw;
    }

    /*** VYKRESLENI MERENI ***/

    /** Metoda zajistujici vykresleni mereni mezi dvojici bodu **/
    public void drawMeasure () {

        /* Je nutne prekreslit bitmapu */
        reinitBitmap();

        /* Vykresleni zdrovojeho bodu */
        drawMeasureActualPoint();

        /* Vykresleni ciloveho bodu */
        drawMeasureDestinationPoint();

        /* Vykresleni linky spojujici body */
        drawMeasureLine();
    }

    /** Vykresleni zdrojoveho bodu **/
    public void drawMeasureActualPoint () {

        /* Zobrazeni aktualni polohy */
        int x = (int)(dActualPoint.getIntrinsicWidth()/1.75);
        int y = (int)(dActualPoint.getIntrinsicHeight()/1.75);

        dActualPoint.setBounds(actualPoint.getPixelX() - x / 2,
                actualPoint.getPixelY() - y,
                actualPoint.getPixelX() + x / 2,
                actualPoint.getPixelY());

        dActualPoint.draw(canvas);
    }

    /** Vykresleni ciloveho bodu **/
    public void drawMeasureDestinationPoint () {

        /* Zobrazeni aktualni polohy */
        int x = (int)(dDestinationPoint.getIntrinsicWidth()/1.75);
        int y = (int)(dDestinationPoint.getIntrinsicHeight()/1.75);

        dDestinationPoint.setBounds(destinationPoint.getPixelX() - x / 2,
                destinationPoint.getPixelY() - y,
                destinationPoint.getPixelX() + x / 2,
                destinationPoint.getPixelY());

        dDestinationPoint.draw(canvas);
    }

    /** Vykresleni vzdalenosti mezi body **/
    public void drawMeasureLine() {

        /* Souradnice zvolenych bodu */
        int x1 = actualPoint.getPixelX();
        int y1 = actualPoint.getPixelY();
        int x2 = destinationPoint.getPixelX();
        int y2 = destinationPoint.getPixelY();
        int x3,y3;

        /* Linka */
        canvas.drawLine(x1,y1,x2,y2,linePaint);

        /* Text */
        /* PX pozice */
        if (x1 >= x2)
            x3 = x2 +  Math.abs(x1-x2)/2 + lineBorderLeft;
        else
            x3 = x1 +  Math.abs(x1-x2)/2 + lineBorderLeft;

        if (y1 > y2)
            y3 = y2 + Math.abs(y1-y2)/2;
        else
            y3 = y1 + Math.abs(y1-y2)/2;

        /* GPS pozice */
        GpsCoordinates gps1 =  new GpsCoordinates(actualPoint.getLatitude(),actualPoint.getLongitude());
        GpsCoordinates gps2 =  new GpsCoordinates(destinationPoint.getLatitude(),destinationPoint.getLongitude());
        double distance = GpsMethods.getDistance(gps1,gps2);

        /* vykresleni */
        canvas.drawText(String.valueOf(distance)+"m",x3,y3,textPaintLarge);
    }

    /** Reinicializace bitmapy **/
    public void reinitBitmap() {

        /* Nacteni puvodni bitmapy */
        bitmap = BitmapConversion
                .convertToMutable(BitmapFactory.decodeByteArray(view.getImage(), 0, view.getImage().length));

        /* Obnoveni */
        canvas.setBitmap(bitmap);
        tivCourseImage.setImageBitmap(bitmap);
        tivCourseImage.invalidate();

        /* Uz nejsou vykreslene body */
        pointsVisible = false;
    }

    /*** GETTERS AND SETTERS ***/

    public Point getActualPoint() {
        return actualPoint;
    }

    public void setActualPoint(Point actualPoint) {
        this.actualPoint = actualPoint;
    }

    public Point getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(Point destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public DatabaseHandlerInternal getDbi() {
        return dbi;
    }

    public void setDbi(DatabaseHandlerInternal dbi) {
        this.dbi = dbi;
    }

    public DatabaseHandlerResort getDbr() {
        return dbr;
    }

    public void setDbr(DatabaseHandlerResort dbr) {
        this.dbr = dbr;
    }

    public Paint getLinePaint() {
        return linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public TouchImageView getTivCourseImage() {
        return tivCourseImage;
    }

    public void setTivCourseImage(TouchImageView tivCourseImage) {
        this.tivCourseImage = tivCourseImage;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getLineLenght() {
        return lineLenght;
    }

    public void setLineLenght(int lineLenght) {
        this.lineLenght = lineLenght;
    }

    public int getLineBorderLeft() {
        return lineBorderLeft;
    }

    public void setLineBorderLeft(int lineBorderLeft) {
        this.lineBorderLeft = lineBorderLeft;
    }

    public int getLineBorderRight() {
        return lineBorderRight;
    }

    public void setLineBorderRight(int lineBorderRight) {
        this.lineBorderRight = lineBorderRight;
    }

    public double getLineLenghtRatio() {
        return lineLenghtRatio;
    }

    public void setLineLenghtRatio(double lineLenghtRatio) {
        this.lineLenghtRatio = lineLenghtRatio;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineOpacity() {
        return lineOpacity;
    }

    public void setLineOpacity(int lineOpacity) {
        this.lineOpacity = lineOpacity;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(int textMargin) {
        this.textMargin = textMargin;
    }

    public boolean isPointsVisible() {
        return pointsVisible;
    }

    public void setPointsVisible(boolean pointsVisible) {
        this.pointsVisible = pointsVisible;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
}
