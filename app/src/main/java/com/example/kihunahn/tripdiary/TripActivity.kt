package com.example.kihunahn.tripdiary

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.kihunahn.tripdiary.R.drawable.comment






class TripActivity: AppCompatActivity() {

    var mapFragment: SupportMapFragment? = null
    var map: GoogleMap? = null
    var myLocationMarker: MarkerOptions? = null
    var polylineOptions: PolylineOptions? = null

    var pathsList: ArrayList<LatLng> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = supportFragmentManager.findFragmentById(R.id.tripMap) as SupportMapFragment
        mapFragment?.getMapAsync(OnMapReadyCallback { googleMap ->
            map = googleMap
            val permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                map?.isMyLocationEnabled = true
            }
            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.RED)
            polylineOptions?.width(15f)
            //drawLine();

        })
    }

    fun drawLine() {

    }

    private fun showCurrentLocation(cur: LatLng) {
        val curPoint = LatLng(cur.latitude, cur.longitude)
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15f))
        //showMyLocationMarker(location);
    }
    private fun showMyLocationMarker(titleName:String,commentPath:String, latitude:Double, longitude:Double, picPath:String){
        val drawable = Drawable.createFromPath(picPath)
        val comment = ""
        if (myLocationMarker == null) {
            myLocationMarker = MarkerOptions()
            myLocationMarker?.position(LatLng(latitude, longitude))
            myLocationMarker?.title(titleName)
            myLocationMarker?.snippet(comment)
            myLocationMarker?.icon(BitmapDescriptorFactory.fromBitmap((drawable as BitmapDrawable).bitmap))
            map?.addMarker(myLocationMarker)?.showInfoWindow()
        } else {
            myLocationMarker?.position(LatLng(latitude, longitude))
        }
    }
}