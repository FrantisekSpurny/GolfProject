package cz.spurny.Game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import cz.spurny.Calculations.SingleTapDetector;
import cz.spurny.Calculations.SwipeDetector;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.DatabaseResort.Course;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.Tee;
import cz.spurny.DatabaseResort.View;
import cz.spurny.Dialogs.MeasureDrawPointSelectionMethod;
import cz.spurny.Dialogs.SelectBallPosition;
import cz.spurny.Dialogs.SelectClub;
import cz.spurny.Dialogs.SelectFromAreaType;
import cz.spurny.Dialogs.SelectSpecification;
import cz.spurny.Dialogs.SelectToAreaType;
import cz.spurny.GpsApi.GpsCoordinates;
import cz.spurny.GpsApi.GpsMethods;
import cz.spurny.Library.BitmapConversion;
import cz.spurny.Library.TouchImageView;
import cz.spurny.Settings.UserPreferences;

public class GameOnHole extends ActionBarActivity {

    /** Aktualne zobrazeny info panel **/
    int actualInfoPanel;

    /** Konstanty urcujici info panel **/
    final int INFO_PANEL_BASIC        = 0;
    final int INFO_PANEL_MEASURE      = 1;
    final int INFO_PANEL_SCORE        = 2;
    final int INFO_PANEL_CAPTURE_SHOT = 3;


    /** GPS souradnice okraju zobrazeni **/
    GpsCoordinates gpsTopLeft;
    GpsCoordinates gpsBottomRight;

    /** Detekce gesta "Tap" **/
    SingleTapDetector singleTapDetector;

    /** Drawable objekty **/
    Drawable actualPositionDrawable ;
    Drawable destinationPositionDrawable;

    /** Aktualni bod a bod cile **/
    Point actualPoint;
    Point destinationPoint;

    /** Canvas a Bitmapa  **/
    Canvas canvas;
    Bitmap bitmap;

    /** Objekty pro vykreslovani jednotlivych cinosti **/
    MeasureDraw     measureDraw     = null; // vykreslovani mereni
    ShotCaptureDraw shotCaptureDraw = null; // vykreslovani zadavani ran

    /** Pole obsahujici vzdalenosti jednotlivych jamek a pozice aktualni jamky **/
    double[] holeLengthArray;
    int      holeIndex;

    /** Atributy hra,jamka,hriste,odpaliste,zobrazeni,body,aktualni rana **/
    private Game        game;
    private Hole        hole;
    private Course      course;
    private Tee         tee;
    private View        view;
    private List<Point> points;

    /** Kontext aktivity **/
    private Context context;

    /** Databaze **/
    private DatabaseHandlerResort   dbr;
    private DatabaseHandlerInternal dbi;

    /** Prvky GUI **/
    private TableLayout tlInfoPanel;
    private TouchImageView tivCourseImage;
    private TextView tvRow1,tvRow2,tvRow3,tvRow4,tvRow5,tvRow6,tvRow7,tvRow8;
    private TextView tvRow9,tvRow10,tvRow11,tvRow12;
    private TableRow trRow1,trRow2,trRow3,trRow4,trRow5,trRow6,trRow7,trRow8;
    private TableRow trRow9,trRow10,trRow11,trRow12;

    /** Max zoom na "TouchImageView" **/
    float maxZoom = 2;

    /*** ZIVOTNI CYKLUS AKTIVITY ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_on_hole_layout);

        /* Ziskani kontextu */
        context = this;

        /* Inicializace aktivity */
        initActivity();

        /* Nacteni bitmapy hriste */
        initCourseImage();
    }

    /** Reakce na ukonceni aktivity **/
    @Override
    protected void onStop() {
        super.onStop();

        /* Uzavzeni databaze */
        dbr.close();
        dbi.close();
    }

    /** Reakce na zmacknuti tlacitka "zpet" - navrat do hlavniho menu **/
    @Override
    public void onBackPressed()
    {
        Intent iSelectHole = new Intent(this,SelectHole.class);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_IDGAME", (long) game.getId());
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_LENGHT_ARRAY", holeLengthArray);
        this.startActivity(iSelectHole);
    }

    /*** INICIALIZACE APLIKACE ***/

    /** Incializace zobrazeni jamky **/
    public void initActivity() {

        /* Pripojeni databazi */
        dbr = new DatabaseHandlerResort  (context);
        dbi = new DatabaseHandlerInternal(context);

        /* Ziskani objektu hra,jamka,hriste,odpaliste,zobrazeni,body */
        getDataFromPreviousActivity();

        /* Inicializace "Canvas" */
        canvas = new Canvas();

        /* Inicilaizace bodu */
        initPoints();

        /* Pripojeni prvku GUI */
        connectGui();

        /* Inicializace informacniho panelu */
        infoPanelBasic();

        /* Nastaveni titulku aktivity */
        setTitleHandler();

        /* Inicializace "drawable" */
        initDrawabale();

        /* Inicializace detektoru gesta "tap" */
        singleTapDetector = new SingleTapDetector(0.05,context);

        /* Detekce dotyku */
        displayTouchHandler();

        /* Vypocet gps souradnic zobrazeni */
        initGpsOfView();
    }

    /** Inicializace vykreslitelnych "drawable" objektu **/
    public void initDrawabale() {
        actualPositionDrawable      = getResources().getDrawable(R.drawable.actual_position_rawable);
        destinationPositionDrawable = getResources().getDrawable(R.drawable.destanation_position_drawable);
    }

    /** Inicializace bodu:
     *  - aktualni bod: zvolene odpaliste
     *  - cilovy bod:   stred greenu **/
    public void initPoints() {
        actualPoint      = dbr.getTeePoint(hole.getId(), tee.getKind());
        destinationPoint = null; // nedefinovano
    }

    /** Pripojeni vsech prvku GUI **/
    public void connectGui() {
        tlInfoPanel    = (TableLayout)    findViewById(R.id.GameOnHole_tableLayout_infoPanel);
        tivCourseImage = (TouchImageView) findViewById(R.id.GameOnHole_touchImageView_courseImage);
        tvRow1         = (TextView)       findViewById(R.id.GameOnHole_textView_row1);
        tvRow2         = (TextView)       findViewById(R.id.GameOnHole_textView_row2);
        tvRow3         = (TextView)       findViewById(R.id.GameOnHole_textView_row3);
        tvRow4         = (TextView)       findViewById(R.id.GameOnHole_textView_row4);
        tvRow5         = (TextView)       findViewById(R.id.GameOnHole_textView_row5);
        tvRow6         = (TextView)       findViewById(R.id.GameOnHole_textView_row6);
        tvRow7         = (TextView)       findViewById(R.id.GameOnHole_textView_row7);
        tvRow8         = (TextView)       findViewById(R.id.GameOnHole_textView_row8);
        tvRow9         = (TextView)       findViewById(R.id.GameOnHole_textView_row9);
        tvRow10        = (TextView)       findViewById(R.id.GameOnHole_textView_row10);
        tvRow11        = (TextView)       findViewById(R.id.GameOnHole_textView_row11);
        tvRow12        = (TextView)       findViewById(R.id.GameOnHole_textView_row12);
        trRow1         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row1);
        trRow2         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row2);
        trRow3         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row3);
        trRow4         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row4);
        trRow5         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row5);
        trRow6         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row6);
        trRow7         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row7);
        trRow8         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row8);
        trRow9         = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row9);
        trRow10        = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row10);
        trRow11        = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row11);
        trRow12        = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row12);
    }

    /** Prevzeti hodnot z predchozi aktivity **/
    public void getDataFromPreviousActivity() {

        int idGame,idHole;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame          = iPrevActivity.getIntExtra("EXTRA_GAME_ON_HOLE_IDGAME", -1);
        idHole          = iPrevActivity.getIntExtra("EXTRA_GAME_ON_HOLE_IDHOLE", -1);
        holeLengthArray = iPrevActivity.getDoubleArrayExtra("EXTRA_GAME_ON_HOLE_LENGHT_ARRAY");
        holeIndex       = iPrevActivity.getIntExtra("EXTRA_GAME_ON_HOLE_INDEX", -1);

        game = dbi.getGame(idGame);
        hole = dbr.getHole(idHole);
        course = dbr.getCourseWithHole(idHole);
        tee    = dbr.getTeeOfGameOnCourse((int) idGame, course.getId());
        view   = dbr.getAllViewsOfHole((int)idHole).get(0); //TODO
        points = dbr.getAllPointsOfView(view.getId());
    }

    /** Tvorba a nastaveni titulku aplikace **/
    public void setTitleHandler() {

        String title    = hole.getNumber() + ". jamka (" + hole.getName() + ")";
        String subtitle = "Par: " + hole.getPar() + " Hcp: " + hole.getHcp();

        /* Nastaveni titulku */
        setTitle(title);

        /* Nastaveni podtitulku */
        getSupportActionBar().setSubtitle(subtitle);
    }

    /** Inicializace bitmapy hriste **/
    public void initCourseImage() {

        /* Prevedeni bitmapy neupravitelne na upravitelnou */
        bitmap = BitmapConversion
        .convertToMutable(BitmapFactory.decodeByteArray(view.getImage(), 0, view.getImage().length));

        /* Prirazeni bitmapy na canvas */
        canvas.setBitmap(bitmap);

        displayCurrentPosition();

        /* Nastaveni maximalniho priblizeni */
        tivCourseImage.setMaxZoom(maxZoom);

        /* Vlozeni bitmapy k zobrazeni */
        tivCourseImage.setImageBitmap(bitmap);
    }

    /** Zobrazeni aktualni polohy **/
    public void displayCurrentPosition() {

        /* Zobrazeni aktualni polohy */
        int x = (int)(actualPositionDrawable.getIntrinsicWidth()/1.75);
        int y = (int)(actualPositionDrawable.getIntrinsicHeight()/1.75);

        actualPositionDrawable.setBounds(actualPoint.getPixelX() - x / 2,
                actualPoint.getPixelY() - y,
                actualPoint.getPixelX() + x / 2,
                actualPoint.getPixelY());

        actualPositionDrawable.draw(canvas);
    }

    /** Vypocet GPS souradnic leveho horniho a praveho spodniho rohu zobrazeni **/
    public void initGpsOfView() {

        /* Bod odpaliste a jamky */
        Point pA = dbr.getPointGreen(hole.getId());
        Point pB = dbr.getTeePoint(hole.getId(), tee.getKind());

        /* Tvorba px bodu */
        android.graphics.Point pxA = new android.graphics.Point(pA.getPixelX(),pA.getPixelY());
        android.graphics.Point pxB = new android.graphics.Point(pB.getPixelX(),pB.getPixelY());

        /* Tvorba gps bodu */
        GpsCoordinates gpsA = new GpsCoordinates(pA.getLatitude(),pA.getLongitude());
        GpsCoordinates gpsB = new GpsCoordinates(pB.getLatitude(),pB.getLongitude());

        List<GpsCoordinates> gpsCoordinates = GpsMethods.gpsOfView(pxA,pxB,gpsA,gpsB,view.getSizeX(),view.getSizeY());

        gpsTopLeft     = gpsCoordinates.get(0);
        gpsBottomRight = gpsCoordinates.get(1);
    }

    /*** MENU AKTIVITY ***/

    /** Zobrazeni menu nabidky **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_on_hole_menu_layout, menu);
        return true;
    }

    /** Reakce na kliknuti na polozku menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.GameOnHoleMenu_item_measuring: // mereni
                measuring();
                return true;
            case R.id.GameOnHoleMenu_item_holeScore: //score jamky
                holeScore();
                return true;
            case R.id.GameOnHoleMenu_item_captureShot: // zaznam rany
                captureShot();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Prechod na predchozi jamku **/
    public void prevHole() {

        List<Hole> holes = dbi.getAllHolesOfGame(game.getId());

        /* Index predchozi jamky */
        int prevIndex;

        /* Zjisteni id predchozi jamky */
        if ( holeIndex == 0) {
            prevIndex = holes.size()-1;
        } else {
            prevIndex = holeIndex-1;
        }

        /* Spusteni aktivity predchozi jamky */
        Intent iPrevHole =  new Intent(context,GameOnHole.class);
        iPrevHole.putExtra("EXTRA_GAME_ON_HOLE_IDGAME", game.getId());
        iPrevHole.putExtra("EXTRA_GAME_ON_HOLE_IDHOLE", holes.get(prevIndex).getId());
        iPrevHole.putExtra("EXTRA_GAME_ON_HOLE_LENGHT_ARRAY", holeLengthArray);
        iPrevHole.putExtra("EXTRA_GAME_ON_HOLE_INDEX", prevIndex);
        startActivity(iPrevHole);
    }

    /** Prechod na nasledujici jamku **/
    public void nextHole() {

        /* Vsechny jamky dane teto hry */
        List<Hole> holes = dbi.getAllHolesOfGame(game.getId());

        /* Pozice nasledujici jamky */
        int nextIndex;

        /* Zjisteni id predchozi jamky */
        if (holeIndex == holes.size()-1) {
            nextIndex = 0;
        } else {
            nextIndex = holeIndex+1;
        }

        /* Spusteni aktivity nasledujici jamky */
        Intent iNextHole =  new Intent(context,GameOnHole.class);
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_IDGAME", game.getId());
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_IDHOLE",holes.get(nextIndex).getId());
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_LENGHT_ARRAY",holeLengthArray);
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_INDEX", nextIndex);
        startActivity(iNextHole);
    }

    /*** REZIM MERENI ***/

    /** Hlavni obsluzna metoda rezimu mereni **/
    public void measuring() {

        /* Tvorba objektu pro vykreslovani */
        measureDraw = new MeasureDraw(actualPoint,null,canvas,bitmap,dbi,dbr,
                tivCourseImage,view,actualPositionDrawable,destinationPositionDrawable);

        /* Vykresleni vyznamnych bodu jamky */
        measureDraw.drawPoints();

        /* Nevoli se poloha */
        measureDraw.setDestinationSelection(false);
        measureDraw.setFromSelection(false);

        /* Zobrazeni informacniho panelu a reakce na kliknuti na jeho polozky */
        infoPanelMeasure();
        infoPanelMeasureClickHandle();
    }

    /** Reakce na kliknuti polozku odkud / kam v informacnim panelu **/
    public void infoPanelMeasureClickHandle() {

        /* Zvoleni zdrojoveho bodu mereni */
        trRow4.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_MEASURE)
                    MeasureDrawPointSelectionMethod.dialog(context, measureDraw, true, view).show();
            }
        });

        /* Zvoleni ciloveho bodu mereni */
        trRow6.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_MEASURE)
                    MeasureDrawPointSelectionMethod.dialog(context, measureDraw, false, view).show();
            }
        });

    }

    /*** INFORMACNI PANEL ***/

    /** Inicializace informacniho panelu se zakladnimi informacemi **/
    public void infoPanelBasic() {

        actualInfoPanel = INFO_PANEL_BASIC;

        showLastPanelInfoRow();
        hideCaptureShotInfoPanel();

        /* Cislo jamky */
        tvRow1.setText(context.getString(R.string.GameOnHole_string_holeNumber));
        tvRow2.setText(String.valueOf(hole.getNumber()));

        /* Par / osobni par */
        tvRow3.setText(context.getString(R.string.GameOnHole_string_par));
        tvRow4.setText(hole.getPar() + "/" + hole.getPar()); //TODO

        /* Handicap */
        tvRow5.setText(context.getString(R.string.GameOnHole_string_handicap));
        tvRow6.setText(String.valueOf(hole.getHcp()));

        /* Delka jamky */
        tvRow7.setText(context.getString(R.string.GameOnHole_string_holeLength));
        tvRow8.setText(String.valueOf(holeLengthArray[holeIndex]) + "m");
    }

    /** Inicializace informacniho panelu pro mereni **/
    public void infoPanelMeasure() {

        actualInfoPanel = INFO_PANEL_MEASURE;

        hideLastPanelInfoRow();
        hideCaptureShotInfoPanel();

        /* Cislo jamky */
        tvRow1.setText(context.getString(R.string.GameOnHole_string_holeNumber));
        tvRow2.setText(String.valueOf(hole.getNumber()));

        /* Vychozi bod mereni */
        tvRow3.setText(context.getString(R.string.GameOnHole_string_from));
        if (measureDraw.getActualPoint().getName() != null)
            tvRow4.setText(measureDraw.getActualPoint().getName());
        else
            tvRow4.setText(context.getString(R.string.GameOnHole_string_setByUser));

        /* Cilovy bod mereni */
        tvRow5.setText(context.getString(R.string.GameOnHole_string_to));
        if (measureDraw.getDestinationPoint() == null)
            tvRow6.setText(Html.fromHtml("<font color=#CC3333>" + context.getString(R.string.GameOnHole_string_notSet) + "</font>"));
        else {
            if (measureDraw.getDestinationPoint().getName() != null)
                tvRow6.setText(measureDraw.getDestinationPoint().getName());
            else
                tvRow6.setText(context.getString(R.string.GameOnHole_string_setByUser));
        }
    }

    /** Zobrazeni info panelu skore jamky **/
    public void infoPanelScore(Score score) {

        actualInfoPanel = INFO_PANEL_SCORE;

        showLastPanelInfoRow();
        hideCaptureShotInfoPanel();

        /* Cislo jamky */
        tvRow1.setText(context.getString(R.string.GameOnHole_string_holeNumber));
        tvRow2.setText(String.valueOf(hole.getNumber()));

        /* Score */
        tvRow3.setText(context.getString(R.string.GameOnHole_string_score));
        tvRow4.setText(score.getScore()+"/"+score.getScore()); //TODO stable

        /* Paty */
        tvRow5.setText(context.getString(R.string.GameOnHole_string_puts));
        tvRow6.setText(String.valueOf(score.getPuts()));

        /* Trestne rany */
        tvRow7.setText(context.getString(R.string.GameOnHole_string_penaltyShots));
        tvRow8.setText(String.valueOf(score.getPenaltyShots()));
    }

    /** Zobrazeni informacniho panelu pro zadavani rany **/
    public void infoPanelCaptureShot() {

        actualInfoPanel = INFO_PANEL_CAPTURE_SHOT;

        showLastPanelInfoRow();
        showCaptureShotInfoPanel();

        /* Nacteni rany */
        Shot shot = shotCaptureDraw.getActualShot();

        /* Cislo rany */
        tvRow1.setText(context.getString(R.string.GameOnHole_string_shotNumber));
        tvRow2.setText(String.valueOf(shot.getNumber()) + ".");

        /* Hul */
        tvRow3.setText(context.getString(R.string.GameOnHole_string_club));
        tvRow4.setText(shot.getClubId() == -1 ?
                context.getString(R.string.GameOnHole_string_notSet) :
                dbi.getClub(shot.getClubId()).getName());

        /* Odkud */
        tvRow5.setText(context.getString(R.string.GameOnHole_string_from));
        tvRow6.setText(AreaType.getString(shot.getFromAreaType(),context));

        /* Kam */
        tvRow7.setText(context.getString(R.string.GameOnHole_string_to));
        tvRow8.setText(AreaType.getString(shot.getToAreaType(),context));

        /* Poloha mice */
        tvRow9.setText(context.getString(R.string.GameOnHole_string_ballArea));
        tvRow10.setText(BallPosition.getString(shot.getBallPosition(),context));

        /* Specifikace rany */
        tvRow11.setText(context.getString(R.string.GameOnHole_string_shotSpecification));
        tvRow12.setText(ShotSpecification.getString(shot.getSpecification(),context));
    }


    /** Zobrazeni radku 7,8 info panelu **/
    public void showLastPanelInfoRow() {
        if (trRow7.getVisibility() != android.view.View.VISIBLE)
            trRow7.setVisibility(android.view.View.VISIBLE);
        if (trRow8.getVisibility() != android.view.View.VISIBLE)
            trRow8.setVisibility(android.view.View.VISIBLE);
    }

    /** Skryti radku 7,8 info panelu **/
    public void hideLastPanelInfoRow() {
        if (trRow7.getVisibility() == android.view.View.VISIBLE)
            trRow7.setVisibility(android.view.View.GONE);
        if (trRow8.getVisibility() == android.view.View.VISIBLE)
            trRow8.setVisibility(android.view.View.GONE);
    }

    /** Zobrazeni radku potrebnych pri zadavani rany **/
    public void showCaptureShotInfoPanel() {
        if (trRow9.getVisibility() != android.view.View.VISIBLE)
            trRow9.setVisibility(android.view.View.VISIBLE);
        if (trRow10.getVisibility() != android.view.View.VISIBLE)
            trRow10.setVisibility(android.view.View.VISIBLE);
        if (trRow11.getVisibility() != android.view.View.VISIBLE)
            trRow11.setVisibility(android.view.View.VISIBLE);
        if (trRow12.getVisibility() != android.view.View.VISIBLE)
            trRow12.setVisibility(android.view.View.VISIBLE);
    }

    /** Schovani radku potrebnych pri zadavani rany **/
    public void hideCaptureShotInfoPanel() {
        if (trRow9.getVisibility() == android.view.View.VISIBLE)
            trRow9.setVisibility(android.view.View.GONE);
        if (trRow10.getVisibility() == android.view.View.VISIBLE)
            trRow10.setVisibility(android.view.View.GONE);
        if (trRow11.getVisibility() == android.view.View.VISIBLE)
            trRow11.setVisibility(android.view.View.GONE);
        if (trRow12.getVisibility() == android.view.View.VISIBLE)
            trRow12.setVisibility(android.view.View.GONE);
    }

    /*** ZPRACOVANI DOTYKU ***/

    /** Reakce na zakladni gesta **/
    public void displayTouchHandler() {

        tivCourseImage.setOnTouchListener(new android.view.View.OnTouchListener() {
            public boolean onTouch(android.view.View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        singleTapDetector.setDownPoint((int) event.getX(), (int) event.getY());
                        return true;
                    case (MotionEvent.ACTION_UP):
                        singleTapDetector.setUpPoint((int) event.getX(), (int) event.getY());
                        handleScreenTouch(event);
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        holeSwapper(event);
                        return true;
                }
                return true;
            }
        });
    }

    /** Reakce na kliknuti na displej **/
    public void handleScreenTouch(MotionEvent event) {

        /** Dotyk bude zpracovan pouze pokud se zaznamenava poloha **/
        if (!handleTouchCheck())
            return;

        /** Kontrola jestli slo o poklepnuti na obrazovku **/
        if (!singleTapDetector.isTap())
            return;

        /** Ziskani souradnic dotyku **/
        int x = (int) event.getX();
        int y = (int) event.getY();

        /** Prepocet na realnou hodnotu vzhledem k priblizeni **/
        PointF recalculatedPoint = tivCourseImage.transformCoordTouchToBitmap(x, y, false);

        /** Prepocet bodu v px na gps souradnice **/
        GpsCoordinates gpsCoordinates
                = GpsMethods.pxToGps(new android.graphics.Point((int) recalculatedPoint.x, (int) recalculatedPoint.y),
                                     gpsTopLeft, gpsBottomRight, view.getSizeX(), view.getSizeY());

        /** Vykreslovani pro mereni **/
        if (measureDraw != null)
            screenTouchMeasure((int)recalculatedPoint.x,(int)recalculatedPoint.y,gpsCoordinates);
    }

    /** Vykreslovani mereni **/
    public void screenTouchMeasure(int x,int y,GpsCoordinates gpsCoordinates) {

        Point point = new Point();
        point.setPixelX(x);
        point.setPixelY(y);
        point.setLatitude (gpsCoordinates.getLatitude ());
        point.setLongitude(gpsCoordinates.getLongitude());

        if (measureDraw.isFromSelection())
            measureDraw.setActualPoint(point);
        else
            measureDraw.setDestinationPoint(point);

        /* Pokud jsou definovany oba body */
        if (measureDraw.getActualPoint() != null && measureDraw.getDestinationPoint() != null) {
            measureDraw.drawMeasure();
        } else {
            measureDraw.drawPoints();
        }

        /* Aktualizace info panelu */
        infoPanelMeasure();
    }

    /** Tato metoda urci, zdali budou zpracovany dotyky na obrazovku **/
    public boolean handleTouchCheck() {
        if (measureDraw != null) {
            if (measureDraw.isFromSelection())
                return true;
            else if (measureDraw.isDestinationSelection())
                return true;
        }

        return false;
    }

    /** Rekce na tahly pohyb na obrazovce **/
    public void holeSwapper(MotionEvent event) {

        int swipe = SwipeDetector.isSwipe(context,
                singleTapDetector.getDownX(),
                singleTapDetector.getDownY(),
                (int) event.getX(), (int) event.getY(),
                singleTapDetector.getDisplayWidth(),
                singleTapDetector.getDisplayHeight());

        /* Presun jamky povolen pouze pokud neni priblizeno */
        if (tivCourseImage.getCurrentZoom() == 1) {
            if (swipe == SwipeDetector.LEFT_SWIPE)
                prevHole();
            else if (swipe == SwipeDetector.RIGHT_SWIPE)
                nextHole();
        }
    }

    /*** SKORE JAMKY ***/

    /** Zakladni inicializace zadavani skore jamky **/
    public void holeScore() {

        Score score = dbi.getScore(hole.getId(), UserPreferences.getMainUserId(context),game.getId());

        /** Skore je jiz zadano **/
        if (score != null)
            displayHoleScore(score);
        /** Pokud nebylo skore jeste zadano **/
        else
            goToHoleScore();
    }

    /** Spusteni aktivity - Skore jamky **/
    public void goToHoleScore() {
        Intent iHoleScore =  new Intent(context,HoleScore.class);
        iHoleScore.putExtra("EXTRA_HOLE_SCORE_IDGAME"      , game.getId());
        iHoleScore.putExtra("EXTRA_HOLE_SCORE_IDHOLE", hole.getId());
        iHoleScore.putExtra("EXTRA_HOLE_SCORE_NUM_OF_SHOTS", 2); //TODO vypocet poctu ran
        startActivity(iHoleScore);
    }

    /** Pripraveni aktivity na zobrazeni score infopanelu **/
    public void displayHoleScore(Score score) {

        /** Reinicializace bitmapy **/
        reinitBitmap();

        /** Zobrazeni infopanelu **/
        infoPanelScore(score);

        /** Reakce na klinuti na info panel */
        infoPanelScoreClickHandle();
    }

    /** Reakce na kliknuti na informacni panel **/
    public void infoPanelScoreClickHandle() {

        /* Zvoleni zdrojoveho bodu mereni */
        tlInfoPanel.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_SCORE)
                    goToHoleScore();
            }
        });

    }

    /*** ZAZNAM RANY ***/

    /** Inicializace zaznamu rany **/
    public void captureShot() {

        /* Reainicializace bitmapy */
        reinitBitmap();

        /* Inicializace objektu pro vykreslovani */
        shotCaptureDraw =  new ShotCaptureDraw(game,hole,tee,canvas,bitmap,tivCourseImage,dbi,dbr);

        /* Vygenerovani hodnot prvni rany - ta je pristupna jako "shotCaptureDraw.getActualShot()" */
        shotCaptureDraw.initFirstShot();

        /* Vykresleni prvni rany */
        shotCaptureDraw.drawActualShot();

        /* Inicializace informacniho panelu pro zadavani rany */
        infoPanelCaptureShot();

        /* Reakce na kliknuti na info panel */
        infoPanelCaptureShotHandle();
    }

    /** Reakce na kliknuti na polozky info panelu Zadani rany **/
    public void infoPanelCaptureShotHandle() {

        /* Zmena hole */
        trRow3.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectClub.dialog(context, dbi.getAllClubs(), shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow4.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectClub.dialog(context, dbi.getAllClubs(), shotCaptureDraw.getActualShot()).show();
            }
        });

       /* Zmena plochy odpalu */
        trRow5.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectFromAreaType.dialog(context, shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow6.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectFromAreaType.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });

        /* Zmena plochy dopadu */
        trRow7.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectToAreaType.dialog(context, shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow8.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectToAreaType.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });

        /* Zmena polohy mice */
        trRow9.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectBallPosition.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow10.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectBallPosition.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });


        /* Zmena specifikace rany */
        trRow11.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectSpecification.dialog(context, shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow12.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT)
                    SelectSpecification.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });
    }

    /*** POMOCNE METODY ***/

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

}
