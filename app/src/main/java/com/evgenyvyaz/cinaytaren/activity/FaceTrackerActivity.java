/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evgenyvyaz.cinaytaren.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.evgenyvyaz.cinaytaren.R;
import com.evgenyvyaz.cinaytaren.YandexRequests;
import com.evgenyvyaz.cinaytaren.camera.GraphicOverlay;
import com.evgenyvyaz.cinaytaren.camera.preview.CameraSourcePreview;
import com.evgenyvyaz.cinaytaren.preferences.Preferences;
import com.evgenyvyaz.cinaytaren.utils.MyLocation;
import com.evgenyvyaz.cinaytaren.utils.view.IconsView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "FaceTracker";
    private SensorManager mSensorManager;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private IconsView view;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
     private float realDegrees;
    private ArrayList<Pair<String, Map<String, Double>>> organizations;
   // private SensorEventListener sensorEventListener;
    private boolean isZnegative = false;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.face_tracker_activity);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        view = (IconsView) findViewById(R.id.view);


        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        }

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
      //  mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       /* sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[2] < 0) {
                    isZnegative = true;
                } else if (event.values[2] > 0){
                    isZnegative = false;
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };*/
       // mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        loadMyLocation();

    }

    public void loadMyLocation(){
        MyLocation.getLocation(this, new MyLocation.OnLocationListener() {
            @Override
            public void onComplete(Location location) {
                System.out.println("lat1 = " + location.getLatitude());
                YandexRequests.requestGetPlaces(new YandexRequests.OnYandexPlacesListener() {
                    @Override
                    public void success(JSONObject jsonObject) {
                        if(jsonObject == null){
                            Toast.makeText(getApplicationContext(),"Проверьте интернет соединение...возможно закончились бесплатные запросы к API yandex", Toast.LENGTH_LONG).show();
                        }
                        organizations = new ArrayList<>();
                        try {
                            JSONArray features = jsonObject.getJSONArray("features");
                            for(int i = 0; i< features.length(); i++){
                                if(features.getJSONObject(i).has("properties") && features.getJSONObject(i).getJSONObject("properties").has("CompanyMetaData") && features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").has("Categories") && features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").length() != 0 && features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").optJSONObject(0).has("class")){

                                    String orgType = features.getJSONObject(i).getJSONObject("properties").getJSONObject("CompanyMetaData").getJSONArray("Categories").optJSONObject(0).getString("class");
                                    JSONArray geometry = features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");

                                    Map<String,Double> values = new HashMap<>();
                                    values.put("angle",getAngle(geometry.getDouble(1),geometry.getDouble(0),Double.parseDouble(Preferences.getMyLat(getApplicationContext())),Double.parseDouble(Preferences.getMyLong(getApplicationContext()))));
                                    values.put("distance", distance(geometry.getDouble(1),geometry.getDouble(0),Double.parseDouble(Preferences.getMyLat(getApplicationContext())),Double.parseDouble(Preferences.getMyLong(getApplicationContext()))));

                                    Pair<String,Map<String,Double>> org = new Pair<>(orgType, values);

                                    organizations.add(org);
                                    view.setTypes(organizations);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure() {
                        System.out.println("request fail = ");
                    }
                }, getApplicationContext());
                //  loadContent(Preferences.Settings.getMyLat(getApplicationContext()), Preferences.Settings.getMyLong(getApplicationContext()));

            }
        });
    }
    public static double distance(double lat1, double lon1, double lat2, double lon2)
    {
        double dLat = (double) Math.toRadians(lat2 - lat1);
        double dLon = (double) Math.toRadians(lon2 - lon1);
        double a =
                (double) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        double d = 6371 * c;

        return Math.round(d * 1000);
    }

    public double getAngle(double startLat, double startLng, double endLat, double endLng) throws JSONException {
        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);
        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        detector.setProcessor( new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                .build());
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
       /* mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);*/
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
      /*  mSensorManager.registerListener(sensorEventListener, mAccelerometer,
                SensorManager.SENSOR_DELAY_GAME);*/
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
       //mSensorManager.unregisterListener(sensorEventListener, mAccelerometer);
        mSensorManager.unregisterListener(this);
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }
   /* @Override
    public void onSensorChanged(final SensorEvent event) {

                float[] values = null;
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    values = event.values;
                    System.out.println("request values x = " + values[0] + " y = " + values[1] + " z = " + values[2]);
                }
                if (event.sensor == mAccelerometer) {

                    System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
                    mLastAccelerometerSet = true;
                } else if (event.sensor == mMagnetometer) {
                    System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                    mLastMagnetometerSet = true;
                }
                if (mLastAccelerometerSet && mLastMagnetometerSet) {
                    SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
                    SensorManager.getOrientation(mR, mOrientation);
                    float azimuthInRadians = mOrientation[0];
                    float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;

                    mCurrentDegree = azimuthInDegress;
                    realDegrees = azimuthInDegress;
                    if (values != null) {
                        if (values[2] < 0f) {
                            if (realDegrees > 180f){
                                mCurrentDegree = mCurrentDegree - 180;

                            } else if (realDegrees < 180){
                                mCurrentDegree = mCurrentDegree + 180f;
                            }

                        }

                    }
                    view.setDegrees((double)mCurrentDegree);



                }


    }*/
   /*@Override
   public void onSensorChanged(SensorEvent event) {

       // get the angle around the z-axis rotated
       float degree = Math.round(event.values[0]);


       mCurrentDegree = degree;

           if (isZnegative) {
               if (realDegrees > 180f){
                   mCurrentDegree = mCurrentDegree - 180;

               } else if (realDegrees < 180){
                   mCurrentDegree = mCurrentDegree + 180f;
               }

           }


       view.setDegrees((double)mCurrentDegree);

   }*/
   @Override
   public void onSensorChanged(SensorEvent event) {
       float degree = Math.round(event.values[0]);

       mCurrentDegree = degree - 180;
       if(mCurrentDegree < 0){
           mCurrentDegree = 360 + mCurrentDegree;
       }
//        L.d("currentDegree = " + currentDegree);
       view.setDegrees((double)mCurrentDegree);
   }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;


        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;

        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {

        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {

        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {

        }
    }
}
