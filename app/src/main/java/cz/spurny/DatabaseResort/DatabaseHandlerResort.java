package cz.spurny.DatabaseResort;

/**
 * Objekt: DatabaseHandlerResort.java
 * Popis:  Objekt predstavujici SQL lite databazi obsahujici informace o jednotlivych resortech.
 *         Nachazeji se zde funkce pro tvorbu samotne databaze a pote hlavne operace "SELECT",
 *         ktere slouzi pro ziskavani dat z jednotlivych tabulek databaze.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

/** Importy **/
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.GameCourse;

public class DatabaseHandlerResort extends SQLiteOpenHelper {

    /** Kontext aplikace a reference na databazi **/
    private SQLiteDatabase database;
    private final Context context;

    /** Jmeno a verze databaze **/
    private static String    DB_NAME     = "golfStatDatabaseResort";
    private static final int DB_VERSION  = 1;

    /** Retezcove konstanty slouzici pro tvorbu databaze (SQL prikazy) **/

    /* Jmena Tabulek */
    private static final String TABLE_RESORT    = "resort";
    private static final String TABLE_COURSE    = "course";
    private static final String TABLE_TEE       = "tee";
    private static final String TABLE_HOLE      = "hole";
    private static final String TABLE_VIEW      = "view";
    private static final String TABLE_POINT     = "point";

    /** Jednotlive atributy tabulek **/

    /* Tabulka RESORT */
    private static final String PRIMARY_KEY_RESORT     = "_id";
    private static final String KEY_NAME               = "name";
    private static final String KEY_AREA               = "area";
    private static final String KEY_CITY               = "city";
    private static final String KEY_STREET             = "street";
    private static final String KEY_STREET_NUM         = "street_num";
    private static final String KEY_LATITUDE           = "latitude";
    private static final String KEY_LONGITUDE          = "longitude";
    private static final String KEY_COURSE_COUNT       = "course_count";

    /* Tabulka HRISTE */
    private static final String PRIMARY_KEY_COURSE     = "_id";
    private static final String FOREIGN_KEY_RESORT     = "id_resort";
    //private static final String KEY_NAME             = "name";
    private static final String KEY_HOLE_COUNT         = "hole_count";
    private static final String KEY_PAR                = "par";

    /* Tabulka Tee */
    private static final String PRIMARY_KEY_TEE        = "_id";
    private static final String FOREIGN_KEY_COURSE     = "id_course";
    private static final String KEY_KIND               = "kind";
    private static final String KEY_CR                 = "CR";
    private static final String KEY_SR                 = "SR";

    /* Tabulka Hole */
    private static final String PRIMARY_KEY_HOLE       = "_id";
    //private static final String FOREIGN_KEY_COURSE   = "id_course";
    private static final String KEY_NUMBER             = "number";
    //private static final String KEY_NAME             = "name";
    //private static final String KEY_PAR              = "par";
    private static final String KEY_HCP                = "hcp";
    private static final String KEY_VIEW_COUNT         = "view_count";

    /* Tabulka View */
    private static final String PRIMARY_KEY_VIEW       = "_id";
    private static final String FOREIGN_KEY_HOLE       = "id_hole";
    //private static final String KEY_NAME             = "name";
    //private static final String KEY_KIND             = "kind";
    private static final String KEY_SIZE_X             = "size_x";
    private static final String KEY_SIZE_Y             = "size_y";
    private static final String KEY_AZIMUT             = "azimut";
    private static final String KEY_IMAGE              = "image";

    /* Tabulka BOD */
    private static final String PRIMARY_KEY_POINT      = "_id";
    private static final String FOREIGN_KEY_VIEW       = "id_view";
    private static final String KEY_TYPE               = "type";
    //private static final String KEY_NAME             = "name";
    private static final String KEY_PIXEL_X            = "pixel_x";
    private static final String KEY_PIXEL_Y            = "pixel_y";
    //private static final String KEY_LATITUDE           = "latitude";
    //private static final String KEY_LONGITUDE          = "longitude";
    private static final String KEY_ELEVATION          = "elevation";

    /** Prikazy pro tvorbu tabulek **/

    /* Tabulka RESORT */
    private static final String CREATE_TABLE_RESORT = "CREATE TABLE " + TABLE_RESORT
            + "(" + PRIMARY_KEY_RESORT  + " INTEGER PRIMARY KEY,"
            + KEY_NAME                  + " TEXT,"
            + KEY_AREA                  + " TEXT,"
            + KEY_CITY                  + " TEXT,"
            + KEY_STREET                + " TEXT,"
            + KEY_STREET_NUM            + " INTEGER,"
            + KEY_LATITUDE              + " REAL,"
            + KEY_LONGITUDE             + " REAL,"
            + KEY_COURSE_COUNT          + " INTEGER" + ")";

    /* Tabulka HRISTE */
    private static final String CREATE_TABLE_COURSE = "CREATE TABLE " + TABLE_COURSE
            + "(" + PRIMARY_KEY_COURSE  + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_RESORT        + " INTEGER,"
            + KEY_NAME                  + " TEXT,"
            + KEY_HOLE_COUNT            + " INTEGER,"
            + KEY_PAR                   + " INTEGER" + ")";


    /* Tabulka Tee */
    private static final String CREATE_TABLE_TEE = "CREATE TABLE " + TABLE_TEE
            + "(" + PRIMARY_KEY_TEE     + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_COURSE        + " INTEGER,"
            + KEY_KIND                  + " TEXT,"
            + KEY_CR                    + " REAL,"
            + KEY_SR                    + " REAL" + ")";

    /* Tabulka Hole */
    private static final String CREATE_TABLE_HOLE = "CREATE TABLE " + TABLE_HOLE
            + "(" + PRIMARY_KEY_HOLE    + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_COURSE        + " INTEGER,"
            + KEY_NUMBER                + " INTEGER,"
            + KEY_NAME                  + " TEXT,"
            + KEY_PAR                   + " INTEGER,"
            + KEY_HCP                   + " INTEGER,"
            + KEY_VIEW_COUNT            + " INTEGER" + ")";

    /* Tabulka View */
    private static final String CREATE_TABLE_VIEW= "CREATE TABLE " + TABLE_VIEW
            + "(" + PRIMARY_KEY_VIEW    + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_HOLE          + " INTEGER,"
            + KEY_NAME                  + " TEXT,"
            + KEY_KIND                  + " TEXT,"
            + KEY_SIZE_X                + " INTEGER,"
            + KEY_SIZE_Y                + " INTEGER,"
            + KEY_AZIMUT                + " INTEGER,"
            + KEY_IMAGE                 + " BLOB" + ")";

    /* Tabulka BOD */
    private static final String CREATE_TABLE_POINT = "CREATE TABLE " + TABLE_POINT
            + "(" + PRIMARY_KEY_POINT   + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_VIEW          + " INTEGER,"
            + KEY_TYPE                  + " TEXT,"
            + KEY_NAME                  + " TEXT,"
            + KEY_PIXEL_X               + " INTEGER,"
            + KEY_PIXEL_Y               + " INTEGER,"
            + KEY_LATITUDE              + " REAL,"
            + KEY_LONGITUDE             + " REAL,"
            + KEY_ELEVATION             + " REAL" + ")";

    /** Definice zakladnich metod pro praci s databazi **/

    /* Konstruktor */
    public DatabaseHandlerResort(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /* Metoda vytvarejici prazdnou databazi a posleze ji prepise databazi, kterou chceme nacist */
    public void createDataBase(String dbFilePath) throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            // netreba nic vykonat databaze je jiz nactena
        }else{
            /* Bude vytvorena databaze ulozena v klasicke systemove slozce, tu budeme moci nasledne prepsat */
            this.getReadableDatabase();

            try {
                copyDataBase(dbFilePath);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /* Kontrola zdali databaze jiz existuje */
    public boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = context.getDatabasePath(DB_NAME).getPath();;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //databaze jeste neexistuje
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /* Prekopirovani databaze do klasicke systemove slozky */
    private void copyDataBase(String dbFilePath) throws IOException {

        /* Otevreni drive vytvorene databaze */
        InputStream myInput = new FileInputStream(dbFilePath);

        // Cesta k nove vytvorene databazi
        String outFileName = context.getDatabasePath(DB_NAME).getPath();

        //Otevreni prazdne databaze jako vystup
        OutputStream myOutput = new FileOutputStream(outFileName);

        //Kopirovani bajtu
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0) {
            myOutput.write(buffer, 0, length);
        }

        //zavreni souboru
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = context.getDatabasePath(DB_NAME).getPath();
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(database != null)
            database.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /** SELECT operace nad databazi **/

	/* Ziskani jedne polozky tabulky Resort */
    public Resort getResort(long idResort) {

        String selectQuery = "SELECT  * FROM " + TABLE_RESORT + " WHERE "
                + PRIMARY_KEY_RESORT + " = " + idResort;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Resort resort = new Resort();
        resort.setId         (c.getInt   (c.getColumnIndex(PRIMARY_KEY_RESORT)));
        resort.setName       (c.getString(c.getColumnIndex(KEY_NAME)));
        resort.setArea       (c.getString(c.getColumnIndex(KEY_AREA)));
        resort.setCity       (c.getString(c.getColumnIndex(KEY_CITY)));
        resort.setStreet     (c.getString(c.getColumnIndex(KEY_STREET)));
        resort.setStreetNum  (c.getInt   (c.getColumnIndex(KEY_STREET_NUM)));
        resort.setLatitude   (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        resort.setLatitude   (c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        resort.setCourseCount(c.getInt   (c.getColumnIndex(KEY_COURSE_COUNT)));

        /* uvolneni kurzoru */
        c.close();

        return resort;
    }

    /* Ziskani vsech polozek tabulky Resort */
    public List<Resort> getAllResorts() {

        List<Resort> resorts = new ArrayList<>();
        String selectQuery  = "SELECT  * FROM " + TABLE_RESORT;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Resort resort = new Resort();
                resort.setId         (c.getInt   (c.getColumnIndex(PRIMARY_KEY_RESORT)));
                resort.setName       (c.getString(c.getColumnIndex(KEY_NAME)));
                resort.setArea       (c.getString(c.getColumnIndex(KEY_AREA)));
                resort.setCity       (c.getString(c.getColumnIndex(KEY_CITY)));
                resort.setStreet     (c.getString(c.getColumnIndex(KEY_STREET)));
                resort.setStreetNum  (c.getInt   (c.getColumnIndex(KEY_STREET_NUM)));
                resort.setLatitude   (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                resort.setLatitude   (c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                resort.setCourseCount(c.getInt   (c.getColumnIndex(KEY_COURSE_COUNT)));

	            /* vlozeni objektu do seznamu */
                resorts.add(resort);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return resorts;
    }

    /* Ziskani jedne polozky tabulky Hriste */
    public Course getCourse(long idCourse) {

        String selectQuery = "SELECT  * FROM " + TABLE_COURSE + " WHERE "
                + PRIMARY_KEY_COURSE + " = " + idCourse;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Course course = new Course();
        course.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_COURSE)));
        course.setIdResort (c.getInt   (c.getColumnIndex(FOREIGN_KEY_RESORT)));
        course.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        course.setHoleCount(c.getInt   (c.getColumnIndex(KEY_HOLE_COUNT)));
        course.setPar      (c.getInt   (c.getColumnIndex(KEY_PAR)));

        /* uvolneni kurzoru */
        c.close();

        return course;
    }

    /* Ziskani jedne polozky tabulky Hriste obsahujiciho urcitou jamku */
    public Course getCourseWithHole(long idHole) {

        Hole hole = getHole(idHole);
        return getCourse(hole.getIdCourse());
    }

    /* Ziskani vsech polozek tabulky Hriste */
    public List<Course> getAllCourses() {

        List<Course> courses = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_COURSE;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Course course = new Course();
                course.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_COURSE)));
                course.setIdResort(c.getInt(c.getColumnIndex(FOREIGN_KEY_RESORT)));
                course.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                course.setHoleCount(c.getInt(c.getColumnIndex(KEY_HOLE_COUNT)));
                course.setPar(c.getInt(c.getColumnIndex(KEY_PAR)));

	            /* vlozeni objektu do seznamu */
                courses.add(course);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return courses;
    }

    /* Ziskani vsech polozek tabulky Hriste v ramci jednoho Resortu */
    public List<Course> getAllCoursesInResort(int idResort) {

        List<Course> courses = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_COURSE + " WHERE "
                + FOREIGN_KEY_RESORT + " = " + idResort;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Course course = new Course();
                course.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_COURSE)));
                course.setIdResort (c.getInt   (c.getColumnIndex(FOREIGN_KEY_RESORT)));
                course.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
                course.setHoleCount(c.getInt   (c.getColumnIndex(KEY_HOLE_COUNT)));
                course.setPar      (c.getInt   (c.getColumnIndex(KEY_PAR)));

	            /* vlozeni objektu do seznamu */
                courses.add(course);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return courses;
    }

    /* Ziskani jedne polozky tabulky odpaliste */
    public Tee getTee(long idTee) {

        String selectQuery = "SELECT  * FROM " + TABLE_TEE + " WHERE "
                + PRIMARY_KEY_TEE + " = " + idTee;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Tee tee = new Tee();
        tee.setId      (c.getInt   (c.getColumnIndex(PRIMARY_KEY_TEE)));
        tee.setIdCourse(c.getInt   (c.getColumnIndex(FOREIGN_KEY_COURSE)));
        tee.setKind    (c.getString(c.getColumnIndex(KEY_KIND)));
        tee.setCR      (c.getDouble(c.getColumnIndex(KEY_CR)));
        tee.setSR      (c.getDouble(c.getColumnIndex(KEY_SR)));

        /* uvolneni kurzoru */
        c.close();

        return tee;
    }


    /* Ziskani vsech polozek tabulky odpaliste */
    public List<Tee> getAllTees() {

        List<Tee> tees = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TEE;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
	 
	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }
	    
	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Tee tee = new Tee();
                tee.setId      (c.getInt   (c.getColumnIndex(PRIMARY_KEY_TEE)));
                tee.setIdCourse(c.getInt(c.getColumnIndex(FOREIGN_KEY_COURSE)));
                tee.setKind(c.getString(c.getColumnIndex(KEY_KIND)));
                tee.setCR(c.getDouble(c.getColumnIndex(KEY_CR)));
                tee.setSR(c.getDouble(c.getColumnIndex(KEY_SR)));
	            
	            /* vlozeni objektu do seznamu */
                tees.add(tee);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return tees;
    }

    /* Ziskani vsech polozek tabulky odpaliste v ramci jednoho hriste */
    public List<Tee> getAllTeesOnCourse(int idCourse) {

        List<Tee> tees = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TEE + " WHERE "
                + FOREIGN_KEY_COURSE + " = " + idCourse;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
	 
	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }
	    
	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Tee tee = new Tee();
                tee.setId      (c.getInt   (c.getColumnIndex(PRIMARY_KEY_TEE)));
                tee.setIdCourse(c.getInt(c.getColumnIndex(FOREIGN_KEY_COURSE)));
                tee.setKind(c.getString(c.getColumnIndex(KEY_KIND)));
                tee.setCR(c.getDouble(c.getColumnIndex(KEY_CR)));
                tee.setSR(c.getDouble(c.getColumnIndex(KEY_SR)));
	            
	            /* vlozeni objektu do seznamu */
                tees.add(tee);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return tees;
    }

    /* Ziskani odpaliste na zkladi idHry a idHriste */
    public Tee getTeeOfGameOnCourse(int idGame,int idCourse) {

        /* pripojeni interni databaze */
        DatabaseHandlerInternal dbi =  new DatabaseHandlerInternal(context);

        List<GameCourse> gameCourse = dbi.getAllGameCourseOfGame(idGame);
        if (gameCourse.get(0).getIdCourse() == idCourse) {
            dbi.close();
            return getTee(gameCourse.get(0).getIdTee());
        } else {
            dbi.close();
            return getTee(gameCourse.get(1).getIdTee());
        }

    }

    /* Ziskani jedne polozky tabulky Jamka */
    public Hole getHole(long idHole) {

        String selectQuery = "SELECT  * FROM " + TABLE_HOLE + " WHERE "
                + PRIMARY_KEY_HOLE + " = " + idHole;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Hole hole = new Hole();
        hole.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_HOLE)));
        hole.setIdCourse (c.getInt   (c.getColumnIndex(FOREIGN_KEY_COURSE)));
        hole.setNumber   (c.getInt   (c.getColumnIndex(KEY_NUMBER)));
        hole.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        hole.setPar      (c.getInt   (c.getColumnIndex(KEY_PAR)));
        hole.setHcp      (c.getInt   (c.getColumnIndex(KEY_HCP)));
        hole.setViewCount(c.getInt   (c.getColumnIndex(KEY_VIEW_COUNT)));

        /* uvolneni kurzoru */
        c.close();

        return hole;
    }

    /* Ziskani vsech polozek tabulky Jamka */
    public List<Hole> getAllHoles() {

        List<Hole> holes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_HOLE;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
	 
	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }
	    
	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Hole hole = new Hole();
                hole.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_HOLE)));
                hole.setIdCourse (c.getInt   (c.getColumnIndex(FOREIGN_KEY_COURSE)));
                hole.setNumber(c.getInt(c.getColumnIndex(KEY_NUMBER)));
                hole.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                hole.setPar(c.getInt(c.getColumnIndex(KEY_PAR)));
                hole.setHcp(c.getInt(c.getColumnIndex(KEY_HCP)));
                hole.setViewCount(c.getInt(c.getColumnIndex(KEY_VIEW_COUNT)));
	            
	            /* vlozeni objektu do seznamu */
                holes.add(hole);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return holes;
    }

    /* Ziskani vsech polozek tabulky Jamka v ramci jednoho hriste */
    public List<Hole> getAllHolesOnCourse(int idCourse) {

        List<Hole> holes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_HOLE + " WHERE "
                + FOREIGN_KEY_COURSE + " = " + idCourse;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
	 
	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }
	    
	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Hole hole = new Hole();
                hole.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_HOLE)));
                hole.setIdCourse (c.getInt   (c.getColumnIndex(FOREIGN_KEY_COURSE)));
                hole.setNumber   (c.getInt   (c.getColumnIndex(KEY_NUMBER)));
                hole.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
                hole.setPar      (c.getInt   (c.getColumnIndex(KEY_PAR)));
                hole.setHcp      (c.getInt   (c.getColumnIndex(KEY_HCP)));
                hole.setViewCount(c.getInt   (c.getColumnIndex(KEY_VIEW_COUNT)));
	            
	            /* vlozeni objektu do seznamu */
                holes.add(hole);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return holes;
    }

    /* Ziskani jedne polozky tabulky Zobrazeni */
    public View getView(long idView) {

        String selectQuery = "SELECT  * FROM " + TABLE_VIEW + " WHERE "
                + PRIMARY_KEY_VIEW + " = " + idView;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        View view = new View();
        view.setId    (c.getInt   (c.getColumnIndex(PRIMARY_KEY_VIEW)));
        view.setIdHole(c.getInt   (c.getColumnIndex(FOREIGN_KEY_HOLE)));
        view.setName  (c.getString(c.getColumnIndex(KEY_NAME)));
        view.setKind  (c.getString(c.getColumnIndex(KEY_KIND)));
        view.setSizeX (c.getInt   (c.getColumnIndex(KEY_SIZE_X)));
        view.setSizeY (c.getInt   (c.getColumnIndex(KEY_SIZE_Y)));
        view.setAzimut(c.getInt   (c.getColumnIndex(KEY_AZIMUT)));
        view.setImage (c.getBlob  (c.getColumnIndex(KEY_IMAGE)));

        /* uvolneni kurzoru */
        c.close();

        return view;
    }

    /* Ziskani vsech polozek tabulky Zobrazeni */
    public List<View> getAllViews() {

        List<View> views = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_VIEW;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                View view = new View();
                view.setId    (c.getInt   (c.getColumnIndex(PRIMARY_KEY_VIEW)));
                view.setIdHole(c.getInt   (c.getColumnIndex(FOREIGN_KEY_HOLE)));
                view.setName  (c.getString(c.getColumnIndex(KEY_NAME)));
                view.setKind  (c.getString(c.getColumnIndex(KEY_KIND)));
                view.setSizeX (c.getInt   (c.getColumnIndex(KEY_SIZE_X)));
                view.setSizeY (c.getInt   (c.getColumnIndex(KEY_SIZE_Y)));
                view.setAzimut(c.getInt   (c.getColumnIndex(KEY_AZIMUT)));
                view.setImage (c.getBlob  (c.getColumnIndex(KEY_IMAGE)));

	            /* vlozeni objektu do seznamu */
                views.add(view);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return views;
    }

    /* Ziskani vsech polozek tabulky Zobrazeni v ramci jedne jamky */
    public List<View> getAllViewsOfHole(int idHole) {

        List<View> views = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_VIEW + " WHERE "
                + FOREIGN_KEY_HOLE + " = " + idHole;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                View view = new View();
                view.setId    (c.getInt   (c.getColumnIndex(PRIMARY_KEY_VIEW)));
                view.setIdHole(c.getInt(c.getColumnIndex(FOREIGN_KEY_HOLE)));
                view.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                view.setKind(c.getString(c.getColumnIndex(KEY_KIND)));
                view.setSizeX(c.getInt(c.getColumnIndex(KEY_SIZE_X)));
                view.setSizeY(c.getInt(c.getColumnIndex(KEY_SIZE_Y)));
                view.setAzimut(c.getInt(c.getColumnIndex(KEY_AZIMUT)));
                view.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));

	            /* vlozeni objektu do seznamu */
                views.add(view);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return views;
    }

    /* Ziskani jedne polozky tabulky Bod */
    public Point getPoint(long idPoint) {

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + PRIMARY_KEY_POINT + " = " + idPoint;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani jedne polozky tabulky Bod */
    public Point getPointGreen(long idHole) {

        long idView = getAllViewsOfHole((int)idHole).get(0).getId();

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView
                + " AND " + KEY_TYPE + " = " + "\"CG\"" ;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani jedne polozky tabulky Bod */
    public Point getPointGreenStart(long idHole) {

        long idView = getAllViewsOfHole((int)idHole).get(0).getId();

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView
                + " AND " + KEY_TYPE + " = " + "\"BG\"" ;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani jedne polozky tabulky Bod */
    public Point getPointGreenEnd(long idHole) {

        long idView = getAllViewsOfHole((int)idHole).get(0).getId();

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView
                + " AND " + KEY_TYPE + " = " + "\"EG\"" ;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani jedne polozky tabulky Bod */
    public Point getPointNbDrive(long idHole) {

        long idView = getAllViewsOfHole((int)idHole).get(0).getId();

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView
                + " AND " + KEY_TYPE + " = " + "\"NBD\"" ;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani jedne polozky tabulky Bod */
    public Point getPointNb100(long idHole) {

        long idView = getAllViewsOfHole((int)idHole).get(0).getId();

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView
                + " AND " + KEY_TYPE + " = " + "\"NB100\"" ;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani bodu na kterem se nachazi dane odpaliste */
    public Point getTeePoint(long idHole,String teeName) {

        long idView = getAllViewsOfHole((int)idHole).get(0).getId();

        String type = null;

        switch (teeName) {
            case "cervena":
                type = "\"TR\"";
                break;
            case "modra":
                type = "\"TB\"";
                break;
            case "zelena":
                type = "\"TG\"";
                break;
            case "cerna":
                type = "\"TK\"";
                break;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView
                + " AND " + KEY_TYPE + " = " + type ;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Point point = new Point();
        point.setId       (c.getInt   (c.getColumnIndex(PRIMARY_KEY_POINT)));
        point.setIdView   (c.getInt   (c.getColumnIndex(FOREIGN_KEY_VIEW)));
        point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
        point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
        point.setPixelX   (c.getInt   (c.getColumnIndex(KEY_PIXEL_X)));
        point.setPixelY   (c.getInt   (c.getColumnIndex(KEY_PIXEL_Y)));
        point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

        /* uvolneni kurzoru */
        c.close();

        return point;
    }

    /* Ziskani vsech polozek tabulky Bod */
    public List<Point> getAllPoints() {

        List<Point> points = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_POINT;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Point point = new Point();
                point.setId       (c.getInt(c.getColumnIndex(PRIMARY_KEY_POINT)));
                point.setIdView   (c.getInt(c.getColumnIndex(FOREIGN_KEY_VIEW)));
                point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
                point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
                point.setPixelX   (c.getInt(c.getColumnIndex(KEY_PIXEL_X)));
                point.setPixelY   (c.getInt(c.getColumnIndex(KEY_PIXEL_Y)));
                point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

	            /* vlozeni objektu do seznamu */
                points.add(point);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return points;
    }

    /* Ziskani vsech polozek tabulky Zobrazeni v ramci jedne jamky */
    public List<Point> getAllPointsOfView(int idView) {

        List<Point> points = new ArrayList<>();
        String selectDotaz = "SELECT  * FROM " + TABLE_POINT + " WHERE "
                + FOREIGN_KEY_VIEW + " = " + idView;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectDotaz, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Point point = new Point();
                point.setId       (c.getInt(c.getColumnIndex(PRIMARY_KEY_POINT)));
                point.setIdView   (c.getInt(c.getColumnIndex(FOREIGN_KEY_VIEW)));
                point.setType     (c.getString(c.getColumnIndex(KEY_TYPE)));
                point.setName     (c.getString(c.getColumnIndex(KEY_NAME)));
                point.setPixelX   (c.getInt(c.getColumnIndex(KEY_PIXEL_X)));
                point.setPixelY   (c.getInt(c.getColumnIndex(KEY_PIXEL_Y)));
                point.setLatitude (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                point.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                point.setElevation(c.getDouble(c.getColumnIndex(KEY_ELEVATION)));

	            /* vlozeni objektu do seznamu */
                points.add(point);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return points;
    }

    /** Ukonceni prace s databazi **/
    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}