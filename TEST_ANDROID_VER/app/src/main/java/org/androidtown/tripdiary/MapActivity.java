package org.androidtown.tripdiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kms0080 on 2017-06-06.
 */

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";

    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;

    PolylineOptions polylineOptions;
    ArrayList<LatLng> pathsList = new ArrayList<>();
    private CompassView mCompassView;
    private SensorManager mSensorManager;
    private boolean mCompassEnabled;

    private boolean running;
    String route = "";
    String name = "";

    //Bitmap bm;
    static String comment="";
    static String fileName="";
    static String titleName="";
    static double latitudeMarker;
    static double longitudeMarker;

    FloatingActionMenu FAM;
    com.github.clans.fab.FloatingActionButton cameraBtn, phoneBtn, commentBtn, startBtn, pauseBtn, endBtn;
    //사진
    LocationManager manager;
    LocationListener myLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        commentBtn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        cameraBtn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        phoneBtn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
        startBtn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item4);
        pauseBtn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item5);
        endBtn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item6);

        commentBtn.setColorNormal(0);
        commentBtn.setColorPressed(0);
        commentBtn.setShadowColor(0);

        cameraBtn.setColorNormal(0);
        cameraBtn.setColorPressed(0);
        cameraBtn.setShadowColor(0);

        phoneBtn.setColorNormal(0);
        phoneBtn.setColorPressed(0);
        phoneBtn.setShadowColor(0);

        startBtn.setColorNormal(0);
        startBtn.setColorPressed(0);
        startBtn.setShadowColor(0);

        pauseBtn.setColorNormal(0);
        pauseBtn.setColorPressed(0);
        pauseBtn.setShadowColor(0);

        endBtn.setColorNormal(0);
        endBtn.setColorPressed(0);
        endBtn.setShadowColor(0);


        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                startActivityForResult(intent, 1);
                FAM.close(true);

            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent=new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(intent);
                FAM.close(true);
                */

            }
        });


        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MoneybookActivity.class);
                startActivity(intent);
                FAM.close(true);

            }

        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
                Toast.makeText(getApplicationContext(), "Start the trip", Toast.LENGTH_SHORT).show();
                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(15);
                requestMyLocation();
                FAM.close(true);
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                requestMyLocation();
                FAM.close(true);
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                manager.removeUpdates(myLocationListener);
                Toast.makeText(getApplicationContext(), "Stop the trip", Toast.LENGTH_SHORT).show();
                saveRoute();
                route="";
                updateDataFile();
                map.clear();
                pathsList.clear();
                FAM.close(true);
            }


        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");

                map = googleMap;
                map.setMyLocationEnabled(true);
                requestMyLocation();
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // 센서 관리자 객체 참조
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        // 나침반을 표시할 뷰 생성
        boolean sideBottom = true;
        mCompassView = new CompassView(this);
        mCompassView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(sideBottom ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.ALIGN_PARENT_TOP);

        ((ViewGroup)mapFragment.getView()).addView(mCompassView, params);

        mCompassEnabled = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            titleName = data.getStringExtra("Title");
            fileName=titleName+".txt";


            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");
            directory.mkdirs();
            File file = new File(directory, "fileName.txt");
            try {
                OutputStreamWriter myOutWriter = new OutputStreamWriter(new FileOutputStream(file, true));
                myOutWriter.write(
                        titleName+","
                        +Environment.getExternalStorageDirectory()+"/TripDiary/TripPicture/"+titleName+".jpeg"
                        +","+Environment.getExternalStorageDirectory()+"/TripDiary/TripComment/"+titleName+".txt"
                        +","+Double.toString(latitudeMarker)+","+Double.toString(longitudeMarker)+"\n");

                myOutWriter.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "fileName.txt is not.", Toast.LENGTH_LONG).show();
            }

            try {
                BufferedReader myReader=new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(directory+"/TripComment", fileName))));
                String aDataRow="";
                String aBuffer="";
                while((aDataRow=myReader.readLine())!=null){
                    aBuffer+=aDataRow+"\n";
                }
                comment=aBuffer;
                myReader.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "comment file is not.", Toast.LENGTH_LONG).show();
            }

            Drawable drawable=Drawable.createFromPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TripDiary/TripPicture/"+titleName+".jpeg");
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(latitudeMarker, longitudeMarker));
            myLocationMarker.title(titleName);
            myLocationMarker.snippet(comment);
            myLocationMarker.icon(BitmapDescriptorFactory.fromBitmap(((BitmapDrawable)drawable).getBitmap()));
            map.addMarker(myLocationMarker).showInfoWindow();

        }
    }

    public String getDate(){
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault());
        Date date = new Date();
        name = dateFormat.format(date);
        return name;
    }

    private void saveRoute() {

        name = getDate();
        String fileName = new String(name + ".txt");
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");
        directory.mkdirs();

        File file = new File(directory, fileName);
        try{
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(route);
            osw.close();
        } catch (Throwable t) {}
        Toast.makeText(getApplicationContext(), "Done writing SD " + fileName, Toast.LENGTH_SHORT).show();
    }

    private void showGpsOptions() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    private void createGpsDisabledAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS가 꺼져있습니다. 이용하실 수 없습니다.")
                .setCancelable(false)
                .setPositiveButton("GPS 켜기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showGpsOptions();
                            }
                        })
                .setNegativeButton("무시하기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void requestMyLocation() {

        manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createGpsDisabledAlert();
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = manager.getBestProvider(criteria, true);

        myLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitudeMarker=location.getLatitude();
                longitudeMarker=location.getLongitude();

                showCurrentLocation(location);
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
        };
        try {
            long minTime = 10000;
            float minDistance = 0;
            manager.requestLocationUpdates(
                    bestProvider,
                    minTime,
                    minDistance,
                    myLocationListener);

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);
            }

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
        if(running) {
            drawLine(curPoint);
            String cur = location.getLatitude() + "," + location.getLongitude() + "\n";
            route += cur;
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
    }


    private void drawLine(LatLng p){
        pathsList.add(p);
        polylineOptions.addAll(pathsList);
        map.addPolyline(polylineOptions);
        pathsList.clear();
        pathsList.add(p);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (map != null) {
            map.setMyLocationEnabled(false);
        }

        if(mCompassEnabled) {
            mSensorManager.unregisterListener(mListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.setMyLocationEnabled(true);
        }

        if(mCompassEnabled) {
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        }
    }


    private final SensorEventListener mListener = new SensorEventListener() {
        private int iOrientation = -1;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        // 센서의 값을 받을 수 있도록 호출되는 메소드
        public void onSensorChanged(SensorEvent event) {
            if (iOrientation < 0) {
                iOrientation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }

            mCompassView.setAzimuth(event.values[0] + 90 * iOrientation);
            mCompassView.invalidate();
        }
    };


    public void updateTripList(String tripName){
        String fileName = "list.txt";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");
        directory.mkdirs();
        File file = new File(directory, fileName);
        try{
            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.append(tripName + ".txt" +"\n");
            osw.close();
            fOut.close();
        } catch (Throwable t) {}
    }

    public void updateDataFile() {
        String fileName = "data.txt";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");
        String str ="";
        directory.mkdirs();
        int tripNum;

        File file = new File(directory, fileName);
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader reader = new BufferedReader(isr);
            str = reader.readLine();
            fIn.close();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            tripNum = Integer.parseInt(str);
            tripNum++;
            str = String.valueOf(tripNum);
            osw.write(str);
            osw.close();
            fOut.close();
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        } catch (java.io.FileNotFoundException e) {
            str = createDataFile();
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            updateTripList(name);
        }
    }
    public String createDataFile(){
        String fileName = "data.txt";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");
        directory.mkdirs();

        File file = new File(directory, fileName);
        try{
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write("1");
            osw.close();
            fOut.close();
        } catch (Throwable t) {}

        return "1";
    }
}