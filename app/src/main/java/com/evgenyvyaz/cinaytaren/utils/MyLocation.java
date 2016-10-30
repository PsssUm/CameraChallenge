package com.evgenyvyaz.cinaytaren.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.evgenyvyaz.cinaytaren.R;
import com.evgenyvyaz.cinaytaren.preferences.Preferences;


/**
 * Created by X550V on 05.07.2016.
 */
public class MyLocation {
    public interface OnLocationListener {
        public void onComplete(Location location);

    }

   static Activity context;


    public static void getLocation(Activity activity, final OnLocationListener onLocationListener) {
        context = activity;
        String stateProvider = checkGpsEnable();
        if (stateProvider.equals("-1")) {
            return;
        }
        if (stateProvider.equals(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "Рекомендуется использовать WI-FI и мобильные сети для определения местоположения.", Toast.LENGTH_LONG).show();
        }


        final LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(stateProvider, 1,
                1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Preferences.setMyLat(context, location.getLatitude() + "");
                        System.out.println("lat = " + location.getLatitude());
                        Preferences.setMyLong(context, location.getLongitude() + "");

                        onLocationListener.onComplete(location);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.removeUpdates(this);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }



    private static String checkGpsEnable() {
        if (((Activity) context).isFinishing()) {
            return "-1";
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        String providerState = "-1";

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled) {
                providerState = LocationManager.GPS_PROVIDER;
            }

        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (network_enabled) {
                providerState = LocationManager.NETWORK_PROVIDER;
            }
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getString(R.string.search_heap_check_gps_title));
            dialog.setMessage(context.getString(R.string.search_heap_check_gps_desc));
            dialog.setPositiveButton((context.getString(R.string.search_heap_check_gps_settings)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.search_heap_check_gps_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
            return providerState;
        }
        return providerState;
    }
}