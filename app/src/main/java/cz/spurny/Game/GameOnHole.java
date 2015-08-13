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
import cz.spurny.Dialogs.CaptureShotPointSelectionMethod;
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

/** Potlacena varovani **/
@SuppressWarnings("unused")

public class GameOnHole extends ActionBarActivity {

    /** Aktualne zobrazeny info panel **/
    int actualInfoPanel;

    /** Konstanty urcujici info panel **/
    final int INFO_PANEL_BASIC        = 0;
    final int INFO_PANEL_MEASURE      = 1;
    final int INFO_PANEL_SCORE        = 2;
    final int INFO_PANEL_CAPTURE_SHOT = 3;

    /** Konstanta prro volani aktivit pro ziskani vysledku **/
    final int HOLE_SCORE_REQUEST = 1;

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
    private MenuItem miMeasuring,miCaptureShot,miHoleScore,miCurrentScore,miScoreCard,miSaveGame;
    private MenuItem miSaveShot,miChangeFrom,miChangeTo,miExitCaptureShot;

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

    /** Reakce na ukonceni aktivit volanych za ucelem ziskani vysledku **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == HOLE_SCORE_REQUEST)
            holeScoreFinished();
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
        tlInfoPanel       = (TableLayout)    findViewById(R.id.GameOnHole_tableLayout_infoPanel);
        tivCourseImage    = (TouchImageView) findViewById(R.id.GameOnHole_touchImageView_courseImage);
        tvRow1            = (TextView)       findViewById(R.id.GameOnHole_textView_row1);
        tvRow2            = (TextView)       findViewById(R.id.GameOnHole_textView_row2);
        tvRow3            = (TextView)       findViewById(R.id.GameOnHole_textView_row3);
        tvRow4            = (TextView)       findViewById(R.id.GameOnHole_textView_row4);
        tvRow5            = (TextView)       findViewById(R.id.GameOnHole_textView_row5);
        tvRow6            = (TextView)       findViewById(R.id.GameOnHole_textView_row6);
        tvRow7            = (TextView)       findViewById(R.id.GameOnHole_textView_row7);
        tvRow8            = (TextView)       findViewById(R.id.GameOnHole_textView_row8);
        tvRow9            = (TextView)       findViewById(R.id.GameOnHole_textView_row9);
        tvRow10           = (TextView)       findViewById(R.id.GameOnHole_textView_row10);
        tvRow11           = (TextView)       findViewById(R.id.GameOnHole_textView_row11);
        tvRow12           = (TextView)       findViewById(R.id.GameOnHole_textView_row12);
        trRow1            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row1);
        trRow2            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row2);
        trRow3            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row3);
        trRow4            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row4);
        trRow5            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row5);
        trRow6            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row6);
        trRow7            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row7);
        trRow8            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row8);
        trRow9            = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row9);
        trRow10           = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row10);
        trRow11           = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row11);
        trRow12           = (TableRow)       findViewById(R.id.GameOnHole_tableRow_row12);
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

        game   = dbi.getGame(idGame);
        hole   = dbr.getHole(idHole);
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
        int x = (int)(actualPositionDrawable.getIntrinsicWidth() / 1.75);
        int y = (int) (actualPositionDrawable.getIntrinsicHeight() / 1.75);

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

        List<GpsCoordinates> gpsCoordinates = GpsMethods.gpsOfView(pxA, pxB, gpsA, gpsB, view.getSizeX(), view.getSizeY());

        gpsTopLeft     = gpsCoordinates.get(0);
        gpsBottomRight = gpsCoordinates.get(1);
    }

    /** Reinicializace aktivity **/
    public void reinitActivity() {

        /* Uz se nemeri ani nezaznamenava rana */
        measureDraw     = null;
        shotCaptureDraw = null;

        /* Zobrazeni zakladniho infopanelu */
        infoPanelBasic();

        /* Zobrazeni hlavniho menu */
        visibilityMainMenu       (true);
        visibilityCaptureShotMenu(false);
    }

    /*** MENU AKTIVITY ***/

    /** Zobrazeni menu nabidky **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_on_hole_menu_layout, menu);

        initMenuItems(menu);

        /* Zobrazeni hlavniho menu a schovani submenu zadavani rany */
        visibilityMainMenu       (true);
        visibilityCaptureShotMenu(false);

        return true;
    }

    /** Pripojeni prvku menu **/
    public void initMenuItems(Menu menu) {
        miCaptureShot     = menu.findItem(R.id.GameOnHoleMenu_item_captureShot);
        miChangeFrom      = menu.findItem(R.id.GameOnHoleMenu_item_changeFrom);
        miChangeTo        = menu.findItem(R.id.GameOnHoleMenu_item_changeTo);
        miCurrentScore    = menu.findItem(R.id.GameOnHoleMenu_item_currentScore);
        miExitCaptureShot = menu.findItem(R.id.GameOnHoleMenu_item_exitCaptureShot);
        miHoleScore       = menu.findItem(R.id.GameOnHoleMenu_item_holeScore);
        miMeasuring       = menu.findItem(R.id.GameOnHoleMenu_item_measuring);
        miSaveShot        = menu.findItem(R.id.GameOnHoleMenu_item_saveShot);
        miScoreCard       = menu.findItem(R.id.GameOnHoleMenu_item_scoreCard);
        miSaveGame        = menu.findItem(R.id.GameOnHoleMenu_item_saveGame);
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
            case R.id.GameOnHoleMenu_item_changeFrom: // zmena bodu odpalu
                CaptureShotPointSelectionMethod.dialog(context,shotCaptureDraw,true,view).show();
                return true;
            case R.id.GameOnHoleMenu_item_changeTo: // zmena bodu dopadu
                CaptureShotPointSelectionMethod.dialog(context,shotCaptureDraw,false,view).show();
                return true;
            case R.id.GameOnHoleMenu_item_saveShot: // ulozeni rany
                shotCaptureDraw.saveActualShot();
                return true;
            case R.id.GameOnHoleMenu_item_exitCaptureShot:
                reinitActivity();
                return true;
            case R.id.GameOnHoleMenu_item_currentScore:
                currentScore();
                return true;
            case R.id.GameOnHoleMenu_item_scoreCard:
                scoreCard();
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
        Intent iPrevHole = new Intent(context, GameOnHole.class);
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
        Intent iNextHole = new Intent(context, GameOnHole.class);
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_IDGAME", game.getId());
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_IDHOLE", holes.get(nextIndex).getId());
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_LENGHT_ARRAY", holeLengthArray);
        iNextHole.putExtra("EXTRA_GAME_ON_HOLE_INDEX", nextIndex);
        startActivity(iNextHole);
    }

    /** Zobrazeni/Skryti hlavniho menu **/
    public void visibilityMainMenu(boolean visible) {
        miMeasuring   .setVisible(visible);
        miHoleScore   .setVisible(visible);
        miCurrentScore.setVisible(visible);
        miCaptureShot .setVisible(visible);
        miScoreCard   .setVisible(visible);
        miSaveGame    .setVisible(visible);
    }

    /** Zobrazeni/Skryti hlavniho menu **/
    public void visibilityCaptureShotMenu(boolean visible) {
        miSaveShot       .setVisible(visible);
        miChangeFrom     .setVisible(visible);
        miChangeTo       .setVisible(visible);
        miExitCaptureShot.setVisible(visible);
    }

    /*** REZIM MERENI ***/

    /** Hlavni obsluzna metoda rezimu mereni **/
    public void measuring() {

        /* Neprobiha zaznam rany */
        shotCaptureDraw = null;

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
        Shot shot;
        if (shotCaptureDraw.getActualShot() != null)
            shot = shotCaptureDraw.getActualShot();
        else
            shot = shotCaptureDraw.getLastPlayedShot();

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
            screenTouchMeasure((int) recalculatedPoint.x, (int) recalculatedPoint.y, gpsCoordinates);
        if (shotCaptureDraw != null)
            screenTouchCaptureShot((int) recalculatedPoint.x, (int) recalculatedPoint.y, gpsCoordinates);
    }

    /** Vykreslovani mereni **/
    public void screenTouchMeasure(int x,int y,GpsCoordinates gpsCoordinates) {

        Point point = new Point();
        point.setPixelX(x);
        point.setPixelY(y);
        point.setLatitude(gpsCoordinates.getLatitude());
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

    /** Vykreslovani zadavani rany **/
    public void screenTouchCaptureShot(int x,int y,GpsCoordinates gpsCoordinates) {

        /* Tvorba noveho bodu */
        Point point = new Point();
        point.setPixelX(x);
        point.setPixelY(y);
        point.setLatitude(gpsCoordinates.getLatitude());
        point.setLongitude(gpsCoordinates.getLongitude());

        /* Puvodni typ plochy dopadu */
        int startAreaType = shotCaptureDraw.getActualShot().getToAreaType();

        /* Zmena polohy v objektu rany */
        if (shotCaptureDraw.isDestinationSelection()) {
            setPointTo(point, shotCaptureDraw.getActualShot());

            /* Pokud se novy bod nenachazi na greenu nastavime polohu na freeway */
            if (shotCaptureDraw.isOnGreen(point))
                shotCaptureDraw.getActualShot().setToAreaType(AreaType.GREEN);
            else
                shotCaptureDraw.getActualShot().setToAreaType(AreaType.FAIRWAY);

        } else
            setPointFrom(point, shotCaptureDraw.getActualShot());

        /* Pokud doslo k zmene typu plochy dopadu aktualizujeme informacni panel */
        if (startAreaType != shotCaptureDraw.getActualShot().getToAreaType())
            infoPanelCaptureShot();

        /* Prepocet vzdalenosti bodu a zmena hole na vhodnou */
        shotCaptureDraw.calculateShotDistance(shotCaptureDraw.getActualShot());
        shotCaptureDraw.determineClub        (shotCaptureDraw.getActualShot());

        /* Prekresleni */
        reinitBitmap();
        shotCaptureDraw.drawShotList();
        shotCaptureDraw.drawActualShot();
    }

    /** Tato metoda urci, zdali budou zpracovany dotyky na obrazovku **/
    public boolean handleTouchCheck() {
        if (measureDraw != null) {
            if (measureDraw.isFromSelection())
                return true;
            else if (measureDraw.isDestinationSelection())
                return true;
        }

        if (shotCaptureDraw != null) {
            if (shotCaptureDraw.isFromSelection())
                return true;
            else if (shotCaptureDraw.isDestinationSelection())
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

        int numberOfShots = dbi.getNumberOfShots(hole.getId(),game.getId());

        /* Default hodnota pokud nebyla zadana zadna rana */
        if (numberOfShots == 0)
            numberOfShots = 2;

        iHoleScore.putExtra("EXTRA_HOLE_SCORE_IDGAME", game.getId());
        iHoleScore.putExtra("EXTRA_HOLE_SCORE_IDHOLE", hole.getId());
        iHoleScore.putExtra("EXTRA_HOLE_SCORE_NUM_OF_SHOTS", numberOfShots);
        startActivityForResult(iHoleScore, HOLE_SCORE_REQUEST);
    }

    /** Pripraveni aktivity na zobrazeni score infopanelu **/
    public void displayHoleScore(Score score) {

        /** Reinicializace bitmapy **/
        reinitBitmap();

        /** Vykresleni zadanych ran **/
        drawShots();

        /** Zobrazeni infopanelu **/
        infoPanelScore(score);

        /** Reakce na klinuti na info panel */
        infoPanelScoreClickHandle();
    }

    /** Vykresleni vsech doposud zadanych ran, pokud je ulozeno skore **/
    public void drawShots() {

        ShotCaptureDraw draw = new ShotCaptureDraw(game,hole,tee,canvas,bitmap,view,tivCourseImage,dbi,dbr,context);
        draw.drawShotList();
        draw.drawPuts();
        draw = null;
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

    /** Ukonceni zadavani skore jamky **/
    public void holeScoreFinished() {

        if (shotCaptureDraw != null && shotCaptureDraw.isLastShot() == true) {
            shotCaptureDraw.drawPuts();
        }
    }

    /*** ZAZNAM RANY ***/

    /** Inicializace zaznamu rany **/
    public void captureShot() {

        /* Neprobiha mereni */
        measureDraw = null;

       /* Skryti hlavniho menu a zobrazeni submenu zadavani rany */
        visibilityMainMenu       (false);
        visibilityCaptureShotMenu(true);

        /* Reainicializace bitmapy */
        reinitBitmap();

        /* Inicializace objektu pro vykreslovani */
        shotCaptureDraw = new ShotCaptureDraw(game,hole,tee,canvas,bitmap,view,tivCourseImage,dbi,dbr,context);

        /* Vygenerovani hodnot prvni rany - ta je pristupna jako "shotCaptureDraw.getActualShot()" */
        if (shotCaptureDraw.getShotList().size() == 0)
            shotCaptureDraw.initFirstShot();

        /* Vsechny rany uz byli zadany */
        else if (shotCaptureDraw.isLastShot())
            shotCaptureDraw.holeFinished();

        /* Vygenerovani nasledujici rany */
        else
            shotCaptureDraw.initShot(shotCaptureDraw.getLastPlayedShot());

        /* Vykresleni predchozich ran */
        shotCaptureDraw.drawShotList();

        /* Vykresleni aktualni rany */
        shotCaptureDraw.drawActualShot();

        /* Vykresleni patu */
        shotCaptureDraw.drawPuts();

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
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectClub.dialog(context, dbi.getAllClubs(), shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow4.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectClub.dialog(context, dbi.getAllClubs(), shotCaptureDraw.getActualShot()).show();
            }
        });

       /* Zmena plochy odpalu */
        trRow5.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectFromAreaType.dialog(context, shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow6.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectFromAreaType.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });

        /* Zmena plochy dopadu */
        trRow7.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectToAreaType.dialog(context, shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow8.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectToAreaType.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });

        /* Zmena polohy mice */
        trRow9.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectBallPosition.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow10.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectBallPosition.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });


        /* Zmena specifikace rany */
        trRow11.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectSpecification.dialog(context, shotCaptureDraw.getActualShot()).show();
            }
        });
        trRow12.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (actualInfoPanel == INFO_PANEL_CAPTURE_SHOT && !shotCaptureDraw.isLastShot())
                    SelectSpecification.dialog(context,shotCaptureDraw.getActualShot()).show();
            }
        });
    }

    /** Nastaveni bodu odpalu **/
    public static void setPointFrom(Point point,Shot shot) {
        shot.setFromX(point.getPixelX());
        shot.setFromY(point.getPixelY());
        shot.setFromLatitude(point.getLatitude());
        shot.setFromlongitude(point.getLongitude());
    }

    /** Nastaveni bodu dopadu **/
    public static void setPointTo(Point point,Shot shot) {
        shot.setToX        (point.getPixelX());
        shot.setToY(point.getPixelY());
        shot.setToLatitude(point.getLatitude());
        shot.setToLongitude(point.getLongitude());
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

    /** Zobrazeni prubezneho skore **/
    public void currentScore() {
        Intent iCurrentScore = new Intent(context,CurrentScore.class);

        iCurrentScore.putExtra("EXTRA_CURRENT_SCORE_IDGAME", game.getId());
        startActivity(iCurrentScore);
    }

    /** Zobrazeni karty skore **/
    public void scoreCard() {
        Intent iScoreCard = new Intent(context,ScoreCard.class);

        iScoreCard.putExtra("EXTRA_SCORE_CARD_IDGAME", game.getId());
        startActivity(iScoreCard);
    }

    /*** GETTERS AND SETTERS ***/
    public int getActualInfoPanel() {
        return actualInfoPanel;
    }

    public void setActualInfoPanel(int actualInfoPanel) {
        this.actualInfoPanel = actualInfoPanel;
    }

    public int getINFO_PANEL_BASIC() {
        return INFO_PANEL_BASIC;
    }

    public int getINFO_PANEL_MEASURE() {
        return INFO_PANEL_MEASURE;
    }

    public int getINFO_PANEL_SCORE() {
        return INFO_PANEL_SCORE;
    }

    public int getINFO_PANEL_CAPTURE_SHOT() {
        return INFO_PANEL_CAPTURE_SHOT;
    }

    public GpsCoordinates getGpsTopLeft() {
        return gpsTopLeft;
    }

    public void setGpsTopLeft(GpsCoordinates gpsTopLeft) {
        this.gpsTopLeft = gpsTopLeft;
    }

    public GpsCoordinates getGpsBottomRight() {
        return gpsBottomRight;
    }

    public void setGpsBottomRight(GpsCoordinates gpsBottomRight) {
        this.gpsBottomRight = gpsBottomRight;
    }

    public SingleTapDetector getSingleTapDetector() {
        return singleTapDetector;
    }

    public void setSingleTapDetector(SingleTapDetector singleTapDetector) {
        this.singleTapDetector = singleTapDetector;
    }

    public Drawable getActualPositionDrawable() {
        return actualPositionDrawable;
    }

    public void setActualPositionDrawable(Drawable actualPositionDrawable) {
        this.actualPositionDrawable = actualPositionDrawable;
    }

    public Drawable getDestinationPositionDrawable() {
        return destinationPositionDrawable;
    }

    public void setDestinationPositionDrawable(Drawable destinationPositionDrawable) {
        this.destinationPositionDrawable = destinationPositionDrawable;
    }

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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public MeasureDraw getMeasureDraw() {
        return measureDraw;
    }

    public void setMeasureDraw(MeasureDraw measureDraw) {
        this.measureDraw = measureDraw;
    }

    public ShotCaptureDraw getShotCaptureDraw() {
        return shotCaptureDraw;
    }

    public void setShotCaptureDraw(ShotCaptureDraw shotCaptureDraw) {
        this.shotCaptureDraw = shotCaptureDraw;
    }

    public double[] getHoleLengthArray() {
        return holeLengthArray;
    }

    public void setHoleLengthArray(double[] holeLengthArray) {
        this.holeLengthArray = holeLengthArray;
    }

    public int getHoleIndex() {
        return holeIndex;
    }

    public void setHoleIndex(int holeIndex) {
        this.holeIndex = holeIndex;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Hole getHole() {
        return hole;
    }

    public void setHole(Hole hole) {
        this.hole = hole;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Tee getTee() {
        return tee;
    }

    public void setTee(Tee tee) {
        this.tee = tee;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DatabaseHandlerResort getDbr() {
        return dbr;
    }

    public void setDbr(DatabaseHandlerResort dbr) {
        this.dbr = dbr;
    }

    public DatabaseHandlerInternal getDbi() {
        return dbi;
    }

    public void setDbi(DatabaseHandlerInternal dbi) {
        this.dbi = dbi;
    }

    public TableLayout getTlInfoPanel() {
        return tlInfoPanel;
    }

    public void setTlInfoPanel(TableLayout tlInfoPanel) {
        this.tlInfoPanel = tlInfoPanel;
    }

    public TouchImageView getTivCourseImage() {
        return tivCourseImage;
    }

    public void setTivCourseImage(TouchImageView tivCourseImage) {
        this.tivCourseImage = tivCourseImage;
    }

    public TextView getTvRow1() {
        return tvRow1;
    }

    public void setTvRow1(TextView tvRow1) {
        this.tvRow1 = tvRow1;
    }

    public TextView getTvRow2() {
        return tvRow2;
    }

    public void setTvRow2(TextView tvRow2) {
        this.tvRow2 = tvRow2;
    }

    public TextView getTvRow3() {
        return tvRow3;
    }

    public void setTvRow3(TextView tvRow3) {
        this.tvRow3 = tvRow3;
    }

    public TextView getTvRow4() {
        return tvRow4;
    }

    public void setTvRow4(TextView tvRow4) {
        this.tvRow4 = tvRow4;
    }

    public TextView getTvRow5() {
        return tvRow5;
    }

    public void setTvRow5(TextView tvRow5) {
        this.tvRow5 = tvRow5;
    }

    public TextView getTvRow6() {
        return tvRow6;
    }

    public void setTvRow6(TextView tvRow6) {
        this.tvRow6 = tvRow6;
    }

    public TextView getTvRow7() {
        return tvRow7;
    }

    public void setTvRow7(TextView tvRow7) {
        this.tvRow7 = tvRow7;
    }

    public TextView getTvRow8() {
        return tvRow8;
    }

    public void setTvRow8(TextView tvRow8) {
        this.tvRow8 = tvRow8;
    }

    public TextView getTvRow9() {
        return tvRow9;
    }

    public void setTvRow9(TextView tvRow9) {
        this.tvRow9 = tvRow9;
    }

    public TextView getTvRow10() {
        return tvRow10;
    }

    public void setTvRow10(TextView tvRow10) {
        this.tvRow10 = tvRow10;
    }

    public TextView getTvRow11() {
        return tvRow11;
    }

    public void setTvRow11(TextView tvRow11) {
        this.tvRow11 = tvRow11;
    }

    public TextView getTvRow12() {
        return tvRow12;
    }

    public void setTvRow12(TextView tvRow12) {
        this.tvRow12 = tvRow12;
    }

    public TableRow getTrRow1() {
        return trRow1;
    }

    public void setTrRow1(TableRow trRow1) {
        this.trRow1 = trRow1;
    }

    public TableRow getTrRow2() {
        return trRow2;
    }

    public void setTrRow2(TableRow trRow2) {
        this.trRow2 = trRow2;
    }

    public TableRow getTrRow3() {
        return trRow3;
    }

    public void setTrRow3(TableRow trRow3) {
        this.trRow3 = trRow3;
    }

    public TableRow getTrRow4() {
        return trRow4;
    }

    public void setTrRow4(TableRow trRow4) {
        this.trRow4 = trRow4;
    }

    public TableRow getTrRow5() {
        return trRow5;
    }

    public void setTrRow5(TableRow trRow5) {
        this.trRow5 = trRow5;
    }

    public TableRow getTrRow6() {
        return trRow6;
    }

    public void setTrRow6(TableRow trRow6) {
        this.trRow6 = trRow6;
    }

    public TableRow getTrRow7() {
        return trRow7;
    }

    public void setTrRow7(TableRow trRow7) {
        this.trRow7 = trRow7;
    }

    public TableRow getTrRow8() {
        return trRow8;
    }

    public void setTrRow8(TableRow trRow8) {
        this.trRow8 = trRow8;
    }

    public TableRow getTrRow9() {
        return trRow9;
    }

    public void setTrRow9(TableRow trRow9) {
        this.trRow9 = trRow9;
    }

    public TableRow getTrRow10() {
        return trRow10;
    }

    public void setTrRow10(TableRow trRow10) {
        this.trRow10 = trRow10;
    }

    public TableRow getTrRow11() {
        return trRow11;
    }

    public void setTrRow11(TableRow trRow11) {
        this.trRow11 = trRow11;
    }

    public TableRow getTrRow12() {
        return trRow12;
    }

    public void setTrRow12(TableRow trRow12) {
        this.trRow12 = trRow12;
    }

    public MenuItem getMiMeasuring() {
        return miMeasuring;
    }

    public void setMiMeasuring(MenuItem miMeasuring) {
        this.miMeasuring = miMeasuring;
    }

    public MenuItem getMiCaptureShot() {
        return miCaptureShot;
    }

    public void setMiCaptureShot(MenuItem miCaptureShot) {
        this.miCaptureShot = miCaptureShot;
    }

    public MenuItem getMiHoleScore() {
        return miHoleScore;
    }

    public void setMiHoleScore(MenuItem miHoleScore) {
        this.miHoleScore = miHoleScore;
    }

    public MenuItem getMiCurrentScore() {
        return miCurrentScore;
    }

    public void setMiCurrentScore(MenuItem miCurrentScore) {
        this.miCurrentScore = miCurrentScore;
    }

    public MenuItem getMiSaveShot() {
        return miSaveShot;
    }

    public void setMiSaveShot(MenuItem miSaveShot) {
        this.miSaveShot = miSaveShot;
    }

    public MenuItem getMiChangeFrom() {
        return miChangeFrom;
    }

    public void setMiChangeFrom(MenuItem miChangeFrom) {
        this.miChangeFrom = miChangeFrom;
    }

    public MenuItem getMiChangeTo() {
        return miChangeTo;
    }

    public void setMiChangeTo(MenuItem miChangeTo) {
        this.miChangeTo = miChangeTo;
    }

    public MenuItem getMiExitCaptureShot() {
        return miExitCaptureShot;
    }

    public void setMiExitCaptureShot(MenuItem miExitCaptureShot) {
        this.miExitCaptureShot = miExitCaptureShot;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }
}
