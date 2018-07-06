package org.androidtown.tripdiary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Double.parseDouble;

/**
 * Created by kms0080 on 2017-06-06.
 */

public class TripActivity extends AppCompatActivity {

    private static final String TAG = "TripActivity";


    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;

    PolylineOptions polylineOptions;
    ArrayList<LatLng> pathsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tripMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");

                map = googleMap;
                map.setMyLocationEnabled(true);
                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(15);
                drawLine();

                try {
                    BufferedReader myReader = new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(
                                            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TripDiary","fileName.txt"))));

                    String titleName="";
                    String picPath = "";
                    String commentPath = "";
                    Double latitude;
                    Double longitude;
                    String aDataRow = "";
                    String aBuffer = "";


                    while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                        StringTokenizer tokens = new StringTokenizer(aBuffer, ",");
                        titleName=tokens.nextToken();
                        picPath = tokens.nextToken();
                        commentPath = tokens.nextToken();
                        latitude = parseDouble(tokens.nextToken());
                        longitude = parseDouble(tokens.nextToken());
                        showMyLocationMarker(titleName, commentPath, latitude, longitude, picPath);
                    }
                    myReader.close();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "fileName file is not.", Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void drawLine(){
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("fileName");
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");

        directory.mkdirs();
        File file = new File(directory, fileName);
        pathsList.clear();

        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            if(fIn != null) {
                BufferedReader reader = new BufferedReader(isr);
                String str = "";
                LatLng tempPoint = new LatLng(37.566535,126.977969);

                str=reader.readLine();
                tempPoint = new LatLng(Double.parseDouble(str.substring(0, str.indexOf(','))),
                        Double.parseDouble(str.substring(str.indexOf(',')+1)));
                showCurrentLocation(tempPoint);

                while ((str = reader.readLine()) != null) {
                    tempPoint = new LatLng(Double.parseDouble(str.substring(0, str.indexOf(','))),
                            Double.parseDouble(str.substring(str.indexOf(',')+1)));

                    pathsList.add(tempPoint);
                }

            }
            fIn.close();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }catch (Throwable t) {
            t.printStackTrace();
        }

        polylineOptions.addAll(pathsList);
        map.addPolyline(polylineOptions);
    }

    private void showCurrentLocation(LatLng cur) {
        LatLng curPoint = new LatLng(cur.latitude, cur.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        //showMyLocationMarker(location);
    }

    private void showMyLocationMarker(String titleName, String commentPath, double latitude, double longitude, String picPath) {

        String comment="";

        try {
            BufferedReader myReader=new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(commentPath))));
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

        Drawable drawable=Drawable.createFromPath(picPath);

        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(latitude, longitude));
            myLocationMarker.title(titleName);
            myLocationMarker.snippet(comment);
            myLocationMarker.icon(BitmapDescriptorFactory.fromBitmap(((BitmapDrawable)drawable).getBitmap()));
            map.addMarker(myLocationMarker).showInfoWindow();
        } else {
            myLocationMarker.position(new LatLng(latitude, longitude));
        }
    }
}