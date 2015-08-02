package cz.spurny.DatabaseInternal;

/**
 * Objekt: DatabaseHandlerInternal.java
 * Popis:  Metody pro praci z interni databazi aplikace. Tato databaze obsahuje uloznene
 *         vsechny hry, rany a udaje o hraci a jeho holich.
 * Autor:  Frantisek Spurny
 * Datum:  22.06.2015
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Settings.UserPreferences;

public class DatabaseHandlerInternal extends SQLiteOpenHelper{

    /** Kontext aplikace a reference na databazi **/
    private SQLiteDatabase database;
    private final Context context;

    /** Jmeno a verze databaze **/
    private static String    DB_NAME     = "golfStatDatabaseInternal";
    private static final int DB_VERSION  = 1;

    /** Retezcove konstanty slouzici pro tvorbu databaze (SQL prikazy) **/

    /* Jmena Tabulek */
    private static final String TABLE_GAME             = "resort";
    private static final String TABLE_GAME_COURSE      = "game_course";
    private static final String TABLE_GAME_PLAYER      = "game_players";
    private static final String TABLE_PLAYER           = "player";
    private static final String TABLE_CLUB             = "club";
    private static final String TABLE_SCORE            = "score";
    private static final String TABLE_SHOT             = "shot";

    /** Jednotlive atributy tabulek **/

    /* Tabulka HRA */
    private static final String PRIMARY_KEY_GAME            = "_id_game";
    private static final String KEY_DATE                    = "date";
    private static final String KEY_DESCRIPTION             = "description";
    private static final String KEY_WEATHER                 = "weather";
    private static final String KEY_WIND                    = "wind";
    private static final String KEY_WIND_POWER              = "wind_power";
    private static final String KEY_COURSE_TOUGHNESS        = "course_toughness";

    /* Tabulka HRA_HRISTE */
    /* Udrzuje vztah mezi hrou a hristi, kde se tato hra odehravala */
    private static final String PRIMARY_KEY_GAME_COURSE     = "_id_game_course";
    private static final String FOREIGN_KEY_GAME            = "id_game";
    private static final String FOREIGN_KEY_COURSE          = "id_course";
    private static final String FOREIGN_KEY_TEE             = "id_tee";

    /* Tabulka HRA_HRACI */
    /* Udrzuje vztah mezi hrou a hraci, kterou ji hraji */
    private static final String PRIMARY_KEY_GAME_PLAYER     = "_id_game_player";
    //private static final String FOREIGN_KEY_GAME          = "id_game";
    private static final String FOREIGN_KEY_PLAYER          = "id_player";

    /* Tabulka HRAC */
    private static final String PRIMARY_KEY_PLAYER          = "_id_player";
    private static final String KEY_NAME                    = "name";
    private static final String KEY_SURNAME                 = "surname";
    private static final String KEY_NICKNAME                = "nickname";
    private static final String KEY_HANDICAP                = "handicap";

    /* Tabulka HOLE */
    private static final String PRIMARY_KEY_CLUB            = "_id_club";
    //private static final String KEY_NAME                  = "name";
    private static final String KEY_MODEL                   = "model";
    private static final String KEY_SSL                     = "standard_stroke_length";
    private static final String KEY_ASL                     = "average_stroke_lenght";

    /* Tabulka SKORE */
    private static final String PRIMARY_KEY_SCORE           = "_id_score";
    //private static final String FOREIGN_KEY_GAME          = "id_game;"
    private static final String FOREIGN_KEY_HOLE            = "id_hole";
    //private static final String FOREIGN_KEY_PLAYER        = "id_player";
    private static final String KEY_SCORE                   = "score";
    private static final String KEY_PUTS                    = "puts";
    private static final String KEY_PENALTY_SHOTS           = "key_penalty_shots";

    /* Tabulka RANA */
    private static final String PRIMARY_KEY_SHOT            = "_idShot";
    //private static final String FOREIGN_KEY_GAME          = "id_game";
    //private static final String FOREIGN_KEY_HOLE          = "id_hole";
    private static final String FOREIGN_KEY_CLUB            = "id_club";
    private static final String KEY_NUMBER                  = "number";
    private static final String KEY_FROM_LATITUDE           = "from_latitude";
    private static final String KEY_FROM_LONGITUDE          = "from_Longitude";
    private static final String KEY_FROM_X                  = "from_x";
    private static final String KEY_FROM_Y                  = "from_y";
    private static final String KEY_FROM_AREA_TYPE          = "from_area_type";
    private static final String KEY_TO_LATITUDE             = "to_latitude";
    private static final String KEY_TO_LONGITUDE            = "to_lenght";
    private static final String KEY_TO_X                    = "to_x";
    private static final String KEY_TO_Y                    = "to_y";
    private static final String KEY_TO_AREA_TYPE            = "to_area_type";
    private static final String KEY_DISTANCE                = "distance";
    private static final String KEY_DEVIATION               = "deviation";
    private static final String KEY_BALL_POSITION           = "ball_position";
    private static final String KEY_SPECIFICATION           = "specification";

    /** Prikazy pro tvorbu tabulek **/

    /* Tabulka HRA */
    private static final String CREATE_TABLE_GAME = "CREATE TABLE " + TABLE_GAME
            + "(" + PRIMARY_KEY_GAME            + " INTEGER PRIMARY KEY,"
            + KEY_DATE                          + " TEXT,"
            + KEY_DESCRIPTION                   + " TEXT,"
            + KEY_WEATHER                       + " INTEGER,"
            + KEY_WIND                          + " INTEGER,"
            + KEY_WIND_POWER                    + " INTEGER,"
            + KEY_COURSE_TOUGHNESS              + " INTEGER" + ")";

    /* Tabulka HRA_HRISTE */
    private static final String CREATE_TABLE_GAME_COURSE = "CREATE TABLE " + TABLE_GAME_COURSE
            + "(" + PRIMARY_KEY_GAME_COURSE     + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_GAME                  + " INTEGER,"
            + FOREIGN_KEY_COURSE                + " INTEGER,"
            + FOREIGN_KEY_TEE                   + " INTEGER" + ")";

    /* Tabulka HRA_HRAC */
    private static final String CREATE_TABLE_GAME_PLAYER = "CREATE TABLE " + TABLE_GAME_PLAYER
            + "(" + PRIMARY_KEY_GAME_PLAYER     + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_GAME                  + " INTEGER,"
            + FOREIGN_KEY_PLAYER                + " INTEGER" + ")";

    /* Tabulka HRAC */
    private static final String CREATE_TABLE_PLAYER = "CREATE TABLE " + TABLE_PLAYER
            + "(" + PRIMARY_KEY_PLAYER          + " INTEGER PRIMARY KEY,"
            + KEY_NAME                          + " TEXT,"
            + KEY_SURNAME                       + " TEXT,"
            + KEY_NICKNAME                      + " TEXT,"
            + KEY_HANDICAP                      + " REAL" + ")";

    /* Tabulka HOLE */
    private static final String CREATE_TABLE_CLUB = "CREATE TABLE " + TABLE_CLUB
            + "(" + PRIMARY_KEY_CLUB            + " INTEGER PRIMARY KEY,"
            + KEY_NAME                          + " TEXT,"
            + KEY_MODEL                         + " TEXT,"
            + KEY_SSL                           + " REAL,"
            + KEY_ASL                           + " REAL" + ")";


    /* Tabulka SCORE */
    private static final String CREATE_TABLE_SCORE = "CREATE TABLE " + TABLE_SCORE
            + "(" + PRIMARY_KEY_SCORE           + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_GAME                  + " INTEGER,"
            + FOREIGN_KEY_HOLE                  + " INTEGER,"
            + FOREIGN_KEY_PLAYER                + " INTEGER,"
            + KEY_SCORE                         + " INTEGER,"
            + KEY_PUTS                          + " INTEGER,"
            + KEY_PENALTY_SHOTS                 + " INTEGER" + ")";

    /* Tabulka RANA */
    private static final String CREATE_TABLE_SHOT = "CREATE TABLE " + TABLE_SHOT
            + "(" + PRIMARY_KEY_SHOT            + " INTEGER PRIMARY KEY,"
            + FOREIGN_KEY_GAME                  + " INTEGER,"
            + FOREIGN_KEY_HOLE                  + " INTEGER,"
            + FOREIGN_KEY_CLUB                  + " INTEGER,"
            + KEY_NUMBER                        + " INTEGER,"
            + KEY_FROM_LATITUDE                 + " REAL,"
            + KEY_FROM_LONGITUDE                + " REAL,"
            + KEY_FROM_X  	                    + " INTEGER,"
            + KEY_FROM_Y	    	            + " INTEGER,"
            + KEY_FROM_AREA_TYPE                + " INTEGER,"
            + KEY_TO_LATITUDE                   + " REAL,"
            + KEY_TO_LONGITUDE                  + " REAL,"
            + KEY_TO_X	                        + " INTEGER,"
            + KEY_TO_Y    	                    + " INTEGER,"
            + KEY_TO_AREA_TYPE                  + " INTEGER,"
            + KEY_DISTANCE                      + " REAL,"
            + KEY_DEVIATION                     + " REAL,"
            + KEY_BALL_POSITION                 + " INTEGER,"
            + KEY_SPECIFICATION                 + " INTEGER" + ")";

    /** Tvorba databaze **/

    public DatabaseHandlerInternal(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

		/* Vykonani SQLite prikazu pro tvorbu tabulek */
        db.execSQL(CREATE_TABLE_GAME);
        db.execSQL(CREATE_TABLE_GAME_COURSE);
        db.execSQL(CREATE_TABLE_GAME_PLAYER);
        db.execSQL(CREATE_TABLE_PLAYER);
        db.execSQL(CREATE_TABLE_CLUB);
        db.execSQL(CREATE_TABLE_SCORE);
        db.execSQL(CREATE_TABLE_SHOT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {

		/* Odstraneni tabulek stare verze */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_PLAYER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOT);

		/* Vytvoreni tabulek nove verze */
        onCreate(db);
    }

    /*** Operace nad databazi ***/

    /** Tabulka HRA **/

    /* Tvorba nove hry */
    public long createGame (Game game) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, game.getDate());
        values.put(KEY_DESCRIPTION, game.getDescription());
        values.put(KEY_WEATHER, game.getWeather());
        values.put(KEY_WIND, game.getWind());
        values.put(KEY_WIND_POWER, game.getWindPower());
        values.put(KEY_COURSE_TOUGHNESS, game.getCourseToughness());

		/* vlozeni radku do tabulky */
        long idGame = db.insert(TABLE_GAME, null, values);

		/* navratova hodnota je ID nove polozky */
        return idGame;
    }

    /* Ziskani jedne polozky tabulky Hra */
    public Game getGame(long idGame) {

        String selectQuery = "SELECT  * FROM " + TABLE_GAME + " WHERE "
                + PRIMARY_KEY_GAME + " = " + idGame;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Game game = new Game();
        game.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_GAME)));
        game.setWind(c.getInt(c.getColumnIndex(KEY_WIND)));
        game.setWindPower(c.getInt(c.getColumnIndex(KEY_WIND_POWER)));
        game.setWeather(c.getInt(c.getColumnIndex(KEY_WEATHER)));
        game.setCourseToughness(c.getInt(c.getColumnIndex(KEY_COURSE_TOUGHNESS)));
        game.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
        game.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

        /* uvolneni kurzoru */
        c.close();

        return game;
    }

    /** Tabulka HRA_HRISTE **/

    /* Tvorba nove vazby hry na hriste */
    public long createGameCourse (GameCourse gameCourse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_GAME, gameCourse.getIdGame());
        values.put(FOREIGN_KEY_COURSE, gameCourse.getIdCourse());
        values.put(FOREIGN_KEY_TEE, gameCourse.getIdTee());

		/* vlozeni radku do tabulky */
        long idGameCourse = db.insert(TABLE_GAME_COURSE, null, values);

		/* navratova hodnota je ID nove polozky */
        return idGameCourse;
    }

    /* Ziskani vsech polozek tabulky Hrahriste v ramci jedne hry */
    public List<GameCourse> getAllGameCourseOfGame(int idGame) {

        List<GameCourse> gameCourses = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_GAME_COURSE + " WHERE "
                + FOREIGN_KEY_GAME + " = " + idGame;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                GameCourse gameCourse = new GameCourse();
                gameCourse.setId      (c.getInt(c.getColumnIndex(PRIMARY_KEY_GAME_COURSE)));
                gameCourse.setIdGame(c.getInt(c.getColumnIndex(FOREIGN_KEY_GAME)));
                gameCourse.setIdCourse(c.getInt(c.getColumnIndex(FOREIGN_KEY_COURSE)));
                gameCourse.setIdTee(c.getInt(c.getColumnIndex(FOREIGN_KEY_TEE)));

	            /* vlozeni objektu do seznamu */
                gameCourses.add(gameCourse);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return gameCourses;
    }

    /* Ziskani vsech jamek jedne hry */
    public List<Hole> getAllHolesOfGame (int idGame) {

        /* Pripojeni databaze resortu */
        DatabaseHandlerResort dbr = new DatabaseHandlerResort(context);

        List<Hole> holes = new ArrayList<>();
        List<GameCourse> gameCourses = getAllGameCourseOfGame(idGame);

        for (int i=0;i<gameCourses.size();i++) {

            List<Hole> courseHoles = dbr.getAllHolesOnCourse(gameCourses.get(i).getIdCourse());

            for (int j=0;j<courseHoles.size();j++) {
                holes.add(courseHoles.get(j));
            }
        }

        /* Uzavreni databaze resortu */
        dbr.close();

        return holes;
    }

    /* Ziskani poctu hrist dane hry */
    public int getNumOfGameCourses(int gameId) {
        return getAllGameCourseOfGame(gameId).size();
    }

    /** Tabulka HRA_HRAC **/

    /* Tvorba nove vazby hry na hrace */
    public long createGamePlayer (GamePlayer gamePlayer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_GAME, gamePlayer.getIdGame());
        values.put(FOREIGN_KEY_PLAYER, gamePlayer.getIdPlayer());

		/* vlozeni radku do tabulky */
        long idGamePlayer = db.insert(TABLE_GAME_PLAYER, null, values);

		/* navratova hodnota je ID nove polozky */
        return idGamePlayer;
    }

    /* Ziskani vsech polozek tabulky HraHrac v ramci jedne hry */
    public List<GamePlayer> getAllGamePlayerOfGame(int idGame) {

        List<GamePlayer> gamePlayers = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_GAME_PLAYER + " WHERE "
            + FOREIGN_KEY_GAME + " = " + idGame;

        /* Ziskani databaze pro cteni */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                GamePlayer gamePlayer = new GamePlayer();
                gamePlayer.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_GAME_PLAYER)));
                gamePlayer.setIdGame(c.getInt(c.getColumnIndex(FOREIGN_KEY_GAME)));
                gamePlayer.setIdPlayer(c.getInt(c.getColumnIndex(FOREIGN_KEY_PLAYER)));

	            /* vlozeni objektu do seznamu */
                gamePlayers.add(gamePlayer);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        /* uvolneni kurzoru */
        c.close();

        return gamePlayers;
    }

    /* Ziskani vsech spoluhracu v ramci jedne hry */
    public List<Player> getAllPlaymatesOfGame(int idGame) {
        List<Player> players         =  new ArrayList<>();
        List<GamePlayer> gamePlayers =  getAllGamePlayerOfGame(idGame);

        players.add(getMainPlayer());

        if (gamePlayers != null) {
            for (int i = 0; i < gamePlayers.size(); i++) {
                players.add(getPlayer(gamePlayers.get(i).getIdPlayer()));
            }
        }

        return players;
    }

    /** Tabulka HRAC **/

    /* Tvorba noveho hrace */
    public long createPlayer (Player player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_SURNAME, player.getSurname());
        values.put(KEY_NICKNAME, player.getNickname());
        values.put(KEY_HANDICAP, player.getHandicap());

		/* vlozeni radku do tabulky */
        long idPlayer = db.insert(TABLE_PLAYER, null, values);

		/* navratova hodnota je ID nove polozky */
        return idPlayer;
    }

    /* upraveni polozky tabulky hrac */
    public int updatePlayer(Player player) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_SURNAME, player.getSurname());
        values.put(KEY_NICKNAME, player.getNickname());
        values.put(KEY_HANDICAP, player.getHandicap());

	    /* aktualizace radku tabulky hole */
        return db.update(TABLE_PLAYER, values, PRIMARY_KEY_PLAYER + " = ?",
                new String[]{String.valueOf(player.getId())});
    }

    /* upraveni polozky tabulky hrac */
    public int updateMainPlayer(Player player) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_SURNAME, player.getSurname());
        values.put(KEY_NICKNAME, player.getNickname());
        values.put(KEY_HANDICAP, player.getHandicap());

	    /* aktualizace radku tabulky hole */
        return db.update(TABLE_PLAYER, values, PRIMARY_KEY_PLAYER + " = ?",
                new String[] { String.valueOf(UserPreferences.getMainUserId(context)) });
    }

    /* Ziskani zanamu hrace */
    public Player getPlayer(int playerId) {
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni

        /* id of main player */
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYER + " WHERE "
                + PRIMARY_KEY_PLAYER + " = " + playerId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Player player = new Player();
        player.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_PLAYER)));
        player.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        player.setSurname(c.getString(c.getColumnIndex(KEY_SURNAME)));
        player.setNickname(c.getString(c.getColumnIndex(KEY_NICKNAME)));
        player.setHandicap(c.getDouble(c.getColumnIndex(KEY_HANDICAP)));

        return player;
    }

    /* Ziskani zanamu "hlavniho" hrace */
    public Player getMainPlayer() {
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni

        /* id of main player */
        long playerId = UserPreferences.getMainUserId(context);
        if (playerId == -1) // nedefinovano
            return null;

        String selectQuery = "SELECT  * FROM " + TABLE_PLAYER + " WHERE "
                + PRIMARY_KEY_PLAYER + " = " + playerId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Player player = new Player();
        player.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_PLAYER)));
        player.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        player.setSurname(c.getString(c.getColumnIndex(KEY_SURNAME)));
        player.setNickname(c.getString(c.getColumnIndex(KEY_NICKNAME)));
        player.setHandicap(c.getDouble(c.getColumnIndex(KEY_HANDICAP)));

        return player;
    }

    /* Ziskani vsech hracu */
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Player player = new Player();
                player.setId      (c.getInt   (c.getColumnIndex(PRIMARY_KEY_PLAYER)));
                player.setName    (c.getString(c.getColumnIndex(KEY_NAME)));
                player.setSurname (c.getString(c.getColumnIndex(KEY_SURNAME)));
                player.setNickname(c.getString(c.getColumnIndex(KEY_NICKNAME)));
                player.setHandicap(c.getDouble(c.getColumnIndex(KEY_HANDICAP)));

	            /* vlozeni objektu do seznamu */
                players.add(player);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        return players;
    }

    /** Tabulka HOLE **/

    /* Pridani polozky do tabulky hole */
    public long createClub(Club club) {
        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        ContentValues values = new ContentValues();
        values.put(KEY_NAME  ,club.getName());
        values.put(KEY_MODEL, club.getModel());
        values.put(KEY_SSL, club.getStandardStrokeLength());
        values.put(KEY_ASL, club.getAverageStrokeLength());

		/* vlozeni radku do tabulky */
        long idClub = db.insert(TABLE_CLUB, null, values);

		/* navratova hodnota je ID nove polozky */
        return idClub;
    }

    /* Ziskani jedne polozky tabulky hole */
    public Club getClub(long idClub) {
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni

        String selectQuery = "SELECT  * FROM " + TABLE_CLUB + " WHERE "
                + PRIMARY_KEY_CLUB + " = " + idClub;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Club club = new Club();
        club.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_CLUB)));
        club.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        club.setModel(c.getString(c.getColumnIndex(KEY_MODEL)));
        club.setStandardStrokeLength(c.getDouble(c.getColumnIndex(KEY_SSL)));
        club.setAverageStrokeLength(c.getDouble(c.getColumnIndex(KEY_ASL)));

        return club;
    }

    /* Ziskani vsech polozek tabulky hole */
    public List<Club> getAllClubs() {
        List<Club> clubs = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CLUB;

        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {

                Club club = new Club();
                club.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_CLUB)));
                club.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                club.setModel(c.getString(c.getColumnIndex(KEY_MODEL)));
                club.setStandardStrokeLength(c.getDouble(c.getColumnIndex(KEY_SSL)));
                club.setAverageStrokeLength(c.getDouble(c.getColumnIndex(KEY_ASL)));

	            /* vlozeni objektu do seznamu */
                clubs.add(club);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        return clubs;
    }

    /* Odstraneni polozky tabulky hole */
    public void removeClub(long idClub) {

        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        db.delete(TABLE_CLUB, PRIMARY_KEY_CLUB + " = ?",
                new String[] { String.valueOf(idClub) });

    }

    /* upraveni polozky tabulky hole */
    public int updateClub(Club club) {

        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        ContentValues values = new ContentValues();
        values.put(KEY_NAME  ,club.getName());
        values.put(KEY_MODEL, club.getModel());
        values.put(KEY_SSL, club.getStandardStrokeLength());
        values.put(KEY_ASL, club.getAverageStrokeLength());

	    /* aktualizace radku tabulky hole */
        return db.update(TABLE_CLUB, values, PRIMARY_KEY_CLUB + " = ?",
                new String[] { String.valueOf(club.getId()) });
    }

    /** Tabulka Skore **/

    /* Pridani polozky do tabulky score */
    public long createScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_GAME  ,score.getGameId());
        values.put(FOREIGN_KEY_HOLE, score.getHoleId());
        values.put(FOREIGN_KEY_PLAYER, score.getPlayerId());
        values.put(KEY_SCORE, score.getScore());
        values.put(KEY_PUTS, score.getPuts());
        values.put(KEY_PENALTY_SHOTS, score.getPenaltyShots());

		/* vlozeni radku do tabulky */
        long idScore = db.insert(TABLE_SCORE, null, values);

		/* navratova hodnota je ID nove polozky */
        return idScore;
    }

    /* Ziskani jedne polozky tabulky Score */
    public Score getScore(long idScore) {
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni

        String selectQuery = "SELECT  * FROM " + TABLE_SCORE + " WHERE "
                + PRIMARY_KEY_SCORE + " = " + idScore;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Score score = new Score();
        score.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_SCORE)));
        score.setGameId(c.getInt(c.getColumnIndex(FOREIGN_KEY_GAME)));
        score.setHoleId(c.getInt(c.getColumnIndex(FOREIGN_KEY_HOLE)));
        score.setPlayerId(c.getInt(c.getColumnIndex(FOREIGN_KEY_PLAYER)));
        score.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
        score.setPuts(c.getInt(c.getColumnIndex(KEY_PUTS)));
        score.setPenaltyShots(c.getInt(c.getColumnIndex(KEY_PENALTY_SHOTS)));

        return score;
    }

    /* Ziskani jedne polozky tabulky Score */
    public Score getScore(long idHole,long idPlayer,long idGame) {
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni

        String selectQuery = "SELECT  * FROM " + TABLE_SCORE + " WHERE "
                          + FOREIGN_KEY_PLAYER + " = " + idPlayer
                + " AND " + FOREIGN_KEY_HOLE   + " = " + idHole
                + " AND " + FOREIGN_KEY_GAME   + " = " + idGame;


        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        if (c.getCount() <= 0) { return null; }

        Score score = new Score();
        score.setId(c.getInt(c.getColumnIndex(PRIMARY_KEY_SCORE)));
        score.setGameId(c.getInt(c.getColumnIndex(FOREIGN_KEY_GAME)));
        score.setHoleId(c.getInt(c.getColumnIndex(FOREIGN_KEY_HOLE)));
        score.setPlayerId(c.getInt(c.getColumnIndex(FOREIGN_KEY_PLAYER)));
        score.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
        score.setPuts(c.getInt(c.getColumnIndex(KEY_PUTS)));
        score.setPenaltyShots(c.getInt(c.getColumnIndex(KEY_PENALTY_SHOTS)));

        return score;
    }

    /* upraveni polozky tabulky skore */
    public int updateScore(Score score) {

        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_GAME  ,score.getGameId());
        values.put(FOREIGN_KEY_HOLE, score.getHoleId());
        values.put(FOREIGN_KEY_PLAYER, score.getPlayerId());
        values.put(KEY_SCORE, score.getScore());
        values.put(KEY_PUTS, score.getPuts());
        values.put(KEY_PENALTY_SHOTS, score.getPenaltyShots());

	    /* aktualizace radku tabulky hole */
        return db.update(TABLE_SCORE, values, PRIMARY_KEY_SCORE + " = ?",
                new String[] { String.valueOf(score.getId()) });
    }

    /** Tabulka RANA **/

    /* Pridani polozky do tabulky Rana */
    public long createShot(Shot shot) {
        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_GAME,    shot.getGameId());
        values.put(FOREIGN_KEY_HOLE,    shot.getHoleId());
        values.put(FOREIGN_KEY_CLUB,    shot.getClubId());
        values.put(KEY_FROM_LATITUDE,   shot.getFromLatitude());
        values.put(KEY_FROM_LONGITUDE,  shot.getFromlongitude());
        values.put(KEY_FROM_X,          shot.getFromX());
        values.put(KEY_FROM_Y,          shot.getFromY());
        values.put(KEY_FROM_AREA_TYPE,  shot.getFromAreaType());
        values.put(KEY_TO_LATITUDE,     shot.getToLatitude());
        values.put(KEY_TO_LONGITUDE,    shot.getToLongitude());
        values.put(KEY_TO_X,            shot.getToX());
        values.put(KEY_TO_Y,            shot.getToY());
        values.put(KEY_TO_AREA_TYPE,    shot.getToAreaType());
        values.put(KEY_DISTANCE,        shot.getDistance());
        values.put(KEY_DEVIATION,       shot.getDeviation());
        values.put(KEY_BALL_POSITION,   shot.getBallPosition());
        values.put(KEY_SPECIFICATION,   shot.getSpecification());

		/* vlozeni radku do tabulky */
        long idShot = db.insert(TABLE_SHOT, null, values);

		/* navratova hodnota je ID nove polozky */
        return idShot;
    }

    /* Ziskani jedne polozky tabulky Rana */
    public Shot getShot(long idShot) {
        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni

        String selectQuery = "SELECT  * FROM " + TABLE_SHOT + " WHERE "
                + PRIMARY_KEY_SHOT + " = " + idShot;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Shot shot = new Shot();
        shot.setId              (c.getInt   (c.getColumnIndex(PRIMARY_KEY_SHOT)));
        shot.setGameId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_GAME)));
        shot.setHoleId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_HOLE)));
        shot.setClubId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_CLUB)));
        shot.setFromLatitude    (c.getDouble(c.getColumnIndex(KEY_FROM_LATITUDE)));
        shot.setFromlongitude   (c.getDouble(c.getColumnIndex(KEY_FROM_LONGITUDE)));
        shot.setFromX           (c.getInt   (c.getColumnIndex(KEY_FROM_X)));
        shot.setFromY           (c.getInt   (c.getColumnIndex(KEY_FROM_Y)));
        shot.setFromAreaType    (c.getInt   (c.getColumnIndex(KEY_FROM_AREA_TYPE)));
        shot.setToLatitude      (c.getDouble(c.getColumnIndex(KEY_TO_LATITUDE)));
        shot.setToLongitude     (c.getDouble(c.getColumnIndex(KEY_TO_LONGITUDE)));
        shot.setToX             (c.getInt   (c.getColumnIndex(KEY_TO_X)));
        shot.setToY             (c.getInt   (c.getColumnIndex(KEY_TO_Y)));
        shot.setToAreaType      (c.getInt   (c.getColumnIndex(KEY_TO_AREA_TYPE)));
        shot.setDistance        (c.getDouble(c.getColumnIndex(KEY_DISTANCE)));
        shot.setDeviation       (c.getDouble(c.getColumnIndex(KEY_DEVIATION)));
        shot.setBallPosition    (c.getInt   (c.getColumnIndex(KEY_BALL_POSITION)));
        shot.setSpecification   (c.getInt   (c.getColumnIndex(KEY_SPECIFICATION)));

        return shot;
    }

    /* Ziskani vsech polozek tabulky Rana */
    public List<Shot> getAllShots() {
        List<Shot> shots = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHOT;

        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Shot shot = new Shot();
                shot.setId              (c.getInt   (c.getColumnIndex(PRIMARY_KEY_SHOT)));
                shot.setGameId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_GAME)));
                shot.setHoleId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_HOLE)));
                shot.setClubId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_CLUB)));
                shot.setFromLatitude    (c.getDouble(c.getColumnIndex(KEY_FROM_LATITUDE)));
                shot.setFromlongitude   (c.getDouble(c.getColumnIndex(KEY_FROM_LONGITUDE)));
                shot.setFromX           (c.getInt   (c.getColumnIndex(KEY_FROM_X)));
                shot.setFromY           (c.getInt   (c.getColumnIndex(KEY_FROM_Y)));
                shot.setFromAreaType    (c.getInt   (c.getColumnIndex(KEY_FROM_AREA_TYPE)));
                shot.setToLatitude      (c.getDouble(c.getColumnIndex(KEY_TO_LATITUDE)));
                shot.setToLongitude     (c.getDouble(c.getColumnIndex(KEY_TO_LONGITUDE)));
                shot.setToX             (c.getInt   (c.getColumnIndex(KEY_TO_X)));
                shot.setToY             (c.getInt   (c.getColumnIndex(KEY_TO_Y)));
                shot.setToAreaType      (c.getInt   (c.getColumnIndex(KEY_TO_AREA_TYPE)));
                shot.setDistance        (c.getDouble(c.getColumnIndex(KEY_DISTANCE)));
                shot.setDeviation       (c.getDouble(c.getColumnIndex(KEY_DEVIATION)));
                shot.setBallPosition    (c.getInt   (c.getColumnIndex(KEY_BALL_POSITION)));
                shot.setSpecification   (c.getInt   (c.getColumnIndex(KEY_SPECIFICATION)));

	            /* vlozeni objektu do seznamu */
                shots.add(shot);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        return shots;
    }

    /* Ziskani vsech polozek tabulky Rana konkretni jamky */
    public List<Shot> getAllShots(int idHole) {
        List<Shot> shots = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHOT + " WHERE "
                + FOREIGN_KEY_HOLE + " = " + idHole;

        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Shot shot = new Shot();
                shot.setId              (c.getInt   (c.getColumnIndex(PRIMARY_KEY_SHOT)));
                shot.setGameId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_GAME)));
                shot.setHoleId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_HOLE)));
                shot.setClubId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_CLUB)));
                shot.setFromLatitude    (c.getDouble(c.getColumnIndex(KEY_FROM_LATITUDE)));
                shot.setFromlongitude   (c.getDouble(c.getColumnIndex(KEY_FROM_LONGITUDE)));
                shot.setFromX           (c.getInt   (c.getColumnIndex(KEY_FROM_X)));
                shot.setFromY           (c.getInt   (c.getColumnIndex(KEY_FROM_Y)));
                shot.setFromAreaType    (c.getInt   (c.getColumnIndex(KEY_FROM_AREA_TYPE)));
                shot.setToLatitude      (c.getDouble(c.getColumnIndex(KEY_TO_LATITUDE)));
                shot.setToLongitude     (c.getDouble(c.getColumnIndex(KEY_TO_LONGITUDE)));
                shot.setToX             (c.getInt   (c.getColumnIndex(KEY_TO_X)));
                shot.setToY             (c.getInt   (c.getColumnIndex(KEY_TO_Y)));
                shot.setToAreaType      (c.getInt   (c.getColumnIndex(KEY_TO_AREA_TYPE)));
                shot.setDistance        (c.getDouble(c.getColumnIndex(KEY_DISTANCE)));
                shot.setDeviation       (c.getDouble(c.getColumnIndex(KEY_DEVIATION)));
                shot.setBallPosition    (c.getInt   (c.getColumnIndex(KEY_BALL_POSITION)));
                shot.setSpecification   (c.getInt   (c.getColumnIndex(KEY_SPECIFICATION)));

	            /* vlozeni objektu do seznamu */
                shots.add(shot);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        return shots;
    }

    /* Ziskani vsech polozek tabulky Rana konkretni hry */
    public List<Shot> getAllShots(int idHole,int idGame) {
        List<Shot> shots = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHOT + " WHERE "
                + FOREIGN_KEY_HOLE + " = " + idHole + " AND " + FOREIGN_KEY_GAME + " = " + idGame;

        SQLiteDatabase db = this.getReadableDatabase(); // ziskani databaze pro cteni
        Cursor c = db.rawQuery(selectQuery, null);

	    /* Tabulka je prazdna */
        if (c.getCount() <= 0) { return null; }

	    /* Postupny pruchod vsemi radky tabulky a jejich ulozeni do seznamu */
        if (c.moveToFirst()) { // presun na prvni prvek
            do {
                Shot shot = new Shot();
                shot.setId              (c.getInt   (c.getColumnIndex(PRIMARY_KEY_SHOT)));
                shot.setGameId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_GAME)));
                shot.setHoleId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_HOLE)));
                shot.setClubId          (c.getInt   (c.getColumnIndex(FOREIGN_KEY_CLUB)));
                shot.setFromLatitude    (c.getDouble(c.getColumnIndex(KEY_FROM_LATITUDE)));
                shot.setFromlongitude   (c.getDouble(c.getColumnIndex(KEY_FROM_LONGITUDE)));
                shot.setFromX           (c.getInt   (c.getColumnIndex(KEY_FROM_X)));
                shot.setFromY           (c.getInt   (c.getColumnIndex(KEY_FROM_Y)));
                shot.setFromAreaType    (c.getInt   (c.getColumnIndex(KEY_FROM_AREA_TYPE)));
                shot.setToLatitude      (c.getDouble(c.getColumnIndex(KEY_TO_LATITUDE)));
                shot.setToLongitude     (c.getDouble(c.getColumnIndex(KEY_TO_LONGITUDE)));
                shot.setToX             (c.getInt   (c.getColumnIndex(KEY_TO_X)));
                shot.setToY             (c.getInt   (c.getColumnIndex(KEY_TO_Y)));
                shot.setToAreaType      (c.getInt   (c.getColumnIndex(KEY_TO_AREA_TYPE)));
                shot.setDistance        (c.getDouble(c.getColumnIndex(KEY_DISTANCE)));
                shot.setDeviation       (c.getDouble(c.getColumnIndex(KEY_DEVIATION)));
                shot.setBallPosition    (c.getInt   (c.getColumnIndex(KEY_BALL_POSITION)));
                shot.setSpecification   (c.getInt   (c.getColumnIndex(KEY_SPECIFICATION)));

	            /* vlozeni objektu do seznamu */
                shots.add(shot);
            } while (c.moveToNext()); // presun na dalsi prvek
        }

        return shots;
    }

    /** Ziskani poctu zahranych ran na jamce **/
    public int getNumberOfShots(int idHole,int idGame) {
        List<Shot> shots = getAllShots(idHole,idGame);

        if (shots == null)
            return 0;
        else
            return shots.size();
    }

    /* upraveni polozky tabulky Rana */
    public int editShot(Shot shot) {

        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_GAME,    shot.getGameId());
        values.put(FOREIGN_KEY_HOLE,    shot.getHoleId());
        values.put(FOREIGN_KEY_CLUB,    shot.getClubId());
        values.put(KEY_FROM_LATITUDE,   shot.getFromLatitude());
        values.put(KEY_FROM_LONGITUDE,  shot.getFromlongitude());
        values.put(KEY_FROM_X,          shot.getFromX());
        values.put(KEY_FROM_Y,          shot.getFromY());
        values.put(KEY_FROM_AREA_TYPE,  shot.getFromAreaType());
        values.put(KEY_TO_LATITUDE,     shot.getToLatitude());
        values.put(KEY_TO_LONGITUDE,    shot.getToLongitude());
        values.put(KEY_TO_X,            shot.getToX());
        values.put(KEY_TO_Y,            shot.getToY());
        values.put(KEY_TO_AREA_TYPE,    shot.getToAreaType());
        values.put(KEY_DISTANCE,        shot.getDistance());
        values.put(KEY_DEVIATION,       shot.getDeviation());
        values.put(KEY_BALL_POSITION,   shot.getBallPosition());
        values.put(KEY_SPECIFICATION,   shot.getSpecification());

	    /* aktualizace radku tabulky hra */
        return db.update(TABLE_SHOT, values, PRIMARY_KEY_SHOT + " = ?",
                new String[] { String.valueOf(shot.getId()) });
    }

    /* Odstraneni polozky tabulky Rana */
    public void deleteShot(long idShot) {

        SQLiteDatabase db = this.getWritableDatabase(); // ziskani databaze pro zapis

        db.delete(TABLE_SHOT, PRIMARY_KEY_SHOT + " = ?",
                new String[] { String.valueOf(idShot) });

    }
}