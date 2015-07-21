package cz.spurny.GpsApi;

/**
 * Objekt: GpsTracker.java
 * Popis:  Objekt obsahujici metody pro praci s modulem GPS s mobilnim zarizeni.
 *         Primarne pro ziskani aktualni polohy.
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsTracker  extends Service implements LocationListener {
    private final Context context;

    /* Priznak urcujici zdali je gps zapnuta */
    boolean isGPSEnabled = false;

    /* Priznak urcujici status gps */
    boolean canGetLocation = false;

    Location location;  // aktualni pozice
    double latitude;    // zemepisna sirka
    double longitude;   // zemepisna delka

    /* Nejmensi vzdalenost potrebna pro aktualizaci gps */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 metru

    /* Nejkratsi doba bezi aktualizacemi gps */
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;  // 10 sekund

    protected LocationManager locationManager;

    public GpsTracker(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            /* Zjisti gsp status */
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            /* Zjisti polohu pomoci gps */
            if (isGPSEnabled) {
                if (location == null) {

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /* Ukonceni prace s gps */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GpsTracker.this);
        }
    }

    /* Zjisteni zemepisne sirky */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /* Zjisteni zemepisne delky */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /* Priznak zdali mohu pouzivat GPS */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /* Metoda vracejici GPS souradnice */
    public GpsCoordinates getGpsCoordinates () {
        GpsCoordinates coordinates = new GpsCoordinates();

        coordinates.setLatitude (getLatitude() );
        coordinates.setLongitude(getLongitude());

        return coordinates;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
