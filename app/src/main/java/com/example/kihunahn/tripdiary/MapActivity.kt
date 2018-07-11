package com.example.kihunahn.tripdiary

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.location.LocationListener
import android.location.LocationManager
import android.hardware.SensorManager
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Criteria
import android.location.Location
//import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import org.androidtown.tripdiary.CompassView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.*
import java.util.*
import java.text.SimpleDateFormat
import java.util.jar.Manifest

class MapActivity : AppCompatActivity() {
    var mapFragment: SupportMapFragment? = null
    var map: GoogleMap? = null

    var pathsList: ArrayList<LatLng> = ArrayList()
    private var mCompassView: CompassView? = null
    private var mSensorManager: SensorManager? = null
    private var mCompassEnabled: Boolean = false

    private var running: Boolean = false
    var route = ""
    var polylineOptions: PolylineOptions? = null

    //Bitmap bm;
    var comment = ""
    var fileName = ""
    var titleName = ""
    var latitudeMarker: Double = 0.toDouble()
    var longitudeMarker: Double = 0.toDouble()

    //사진
    var manager: LocationManager? = null
    var myLocationListener: LocationListener? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val FAM = findViewById(R.id.material_design_android_floating_action_menu) as FloatingActionMenu

        val commentBtn = findViewById(R.id.material_design_floating_action_menu_item1) as com.github.clans.fab.FloatingActionButton
        val cameraBtn = findViewById(R.id.material_design_floating_action_menu_item2) as com.github.clans.fab.FloatingActionButton
        val phoneBtn = findViewById(R.id.material_design_floating_action_menu_item3) as com.github.clans.fab.FloatingActionButton
        val startBtn = findViewById(R.id.material_design_floating_action_menu_item4) as com.github.clans.fab.FloatingActionButton
        val pauseBtn = findViewById(R.id.material_design_floating_action_menu_item5) as com.github.clans.fab.FloatingActionButton
        val endBtn = findViewById(R.id.material_design_floating_action_menu_item6) as com.github.clans.fab.FloatingActionButton

        commentBtn.colorNormal = 0
        commentBtn.colorPressed = 0
        commentBtn.shadowColor = 0

        cameraBtn.colorNormal = 0
        cameraBtn.colorPressed = 0
        cameraBtn.shadowColor = 0

        phoneBtn.colorNormal = 0
        phoneBtn.colorPressed = 0
        phoneBtn.shadowColor = 0

        startBtn.colorNormal = 0
        startBtn.colorPressed = 0
        startBtn.shadowColor = 0

        pauseBtn.colorNormal = 0
        pauseBtn.colorPressed = 0
        pauseBtn.shadowColor = 0

        endBtn.colorNormal = 0
        endBtn.colorPressed = 0
        endBtn.shadowColor = 0

        cameraBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            FAM.close(true)
        }

        phoneBtn.setOnClickListener {
            FAM.close(true)
        }
        commentBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            FAM.close(true)
        }
        startBtn.setOnClickListener {

            running = true
            polylineOptions = PolylineOptions()
            polylineOptions?.color(Color.RED)
            polylineOptions?.width(15f)
            //requestMyLocation()
            FAM.close(true)
        }
        pauseBtn.setOnClickListener {
            running = false
            //requestMyLocation()
            FAM.close(true)
        }
        endBtn.setOnClickListener {
            running = false
            manager?.removeUpdates(myLocationListener)
            //saveRoute()
            route = ""
            //updateDataFile()
            map?.clear()
            pathsList.clear()
            FAM.close(true)
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(OnMapReadyCallback { googleMap ->
            map = googleMap

            val permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                map?.isMyLocationEnabled = true
            }


            //requestMyLocation()
        })

        try {
            MapsInitializer.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 센서 관리자 객체 참조
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 나침반을 표시할 뷰 생성
        val sideBottom = true
        mCompassView = CompassView(this)
        mCompassView?.setVisibility(View.VISIBLE)

        val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        params.addRule(if (sideBottom) RelativeLayout.ALIGN_PARENT_BOTTOM else RelativeLayout.ALIGN_PARENT_TOP)

        (mapFragment?.getView() as ViewGroup).addView(mCompassView, params)

        mCompassEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            titleName = data.getStringExtra("Title")
            fileName = titleName + ".txt"

            /* DB부분
            val sdCard = Environment.getExternalStorageDirectory()
            val directory = File(sdCard.absolutePath + "/TripDiary")
            directory.mkdirs()
            val file = File(directory, "fileName.txt")
            try {
                val myOutWriter = OutputStreamWriter(FileOutputStream(file, true))
                myOutWriter.write(
                        titleName + ","
                                + Environment.getExternalStorageDirectory() + "/TripDiary/TripPicture/" + titleName + ".jpeg"
                                + "," + Environment.getExternalStorageDirectory() + "/TripDiary/TripComment/" + titleName + ".txt"
                                + "," + java.lang.Double.toString(latitudeMarker) + "," + java.lang.Double.toString(longitudeMarker) + "\n")

                myOutWriter.close()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "fileName.txt is not.", Toast.LENGTH_LONG).show()
            }

            try {
                val myReader = BufferedReader(
                        InputStreamReader(
                                FileInputStream(
                                        File(directory + "/TripComment", fileName))))
                var aDataRow = ""
                var aBuffer = ""
                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n"
                }
                comment = aBuffer
                myReader.close()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "comment file is not.", Toast.LENGTH_LONG).show()
            }
            */

            val drawable = Drawable.createFromPath(Environment.getExternalStorageDirectory().absolutePath + "/TripDiary/TripPicture/" + titleName + ".jpeg")
            var myLocationMarker = MarkerOptions()
            myLocationMarker.position(LatLng(latitudeMarker, longitudeMarker))
            myLocationMarker.title(titleName)
            myLocationMarker.snippet(comment)
            myLocationMarker.icon(BitmapDescriptorFactory.fromBitmap((drawable as BitmapDrawable).bitmap))
            map?.addMarker(myLocationMarker)?.showInfoWindow()
        }
    }

    fun getDate() {
        var dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        var date = Date()
        name = dateFormat.format(date)
    }

    private fun saveRoute() {
        var fileName: String = name + ".txt"
        var sdCard: File = Environment.getExternalStorageDirectory();
        var directory = File(sdCard.getAbsolutePath() + "/TripDiary");
        directory.mkdirs()

        var file = File(directory, fileName);
        try {
            var fOut = FileOutputStream(file)
            var osw = OutputStreamWriter(fOut)
            osw.write(route)
            osw.close()
        } catch (t: Throwable) {
        }
        Toast.makeText(getApplicationContext(), "Done writing SD " + fileName, Toast.LENGTH_SHORT).show()
    }

    private fun showGpsOptions() {
        val gpsOptionsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(gpsOptionsIntent)
    }

    private fun createGpsDisabledAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("GPS가 꺼져있습니다. 이용하실 수 없습니다.")
                .setCancelable(false)
                .setPositiveButton("GPS 켜기",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            showGpsOptions()
                        })
                .setNegativeButton("무시하기", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
        val alert = builder.create()
        alert.show()
    }

    private fun requestMyLocation() {
        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) != true) {
            createGpsDisabledAlert()
        }
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val bestProvider = manager?.getBestProvider(criteria, true)
        myLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitudeMarker = location.getLatitude()
                longitudeMarker = location.getLongitude()

                showCurrentLocation(location)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }
        try {
            val minTime: Long = 10000
            val minDistance = 0f
            manager?.requestLocationUpdates(
                    bestProvider,
                    minTime,
                    minDistance,
                    myLocationListener)

            val lastLocation = manager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                showCurrentLocation(lastLocation)
            }

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showCurrentLocation(location: Location) {
        val curPoint = LatLng(location.latitude, location.longitude)
        if (running) {
            drawLine(curPoint)
            val cur = location.latitude.toString() + "," + location.longitude + "\n"
            route += cur
        }
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15f))
    }

    private fun drawLine(p: LatLng) {
        pathsList.add(p)
        polylineOptions?.addAll(pathsList)
        map?.addPolyline(polylineOptions)
        pathsList.clear()
        pathsList.add(p)
    }

    override fun onPause() {
        super.onPause()

        if (map != null) {
            val permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                map?.isMyLocationEnabled = false
            }
        }

        if (mCompassEnabled) {
            mSensorManager!!.unregisterListener(mListener)
        }
    }

    override fun onResume() {
        super.onResume()

        if (map != null) {
            val permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                map?.isMyLocationEnabled = true
            }
        }

        if (mCompassEnabled) {
            mSensorManager!!.registerListener(mListener, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI)
        }
    }

    val mListener = object : SensorEventListener {
        var iOrientation = -1
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        // 센서의 값을 받을 수 있도록 호출되는 메소드
        override fun onSensorChanged(event: SensorEvent) {
            if (iOrientation < 0) {
                val winManage = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                iOrientation = winManage.defaultDisplay.rotation
            }

            mCompassView?.setAzimuth(event.values[0] + 90 * iOrientation)
            mCompassView?.invalidate()
        }
    }


    fun updateTripList(tripName: String) {
        val fileName = "list.txt"
        val sdCard = Environment.getExternalStorageDirectory()
        val directory = File(sdCard.getAbsolutePath() + "/TripDiary")
        directory.mkdirs()
        val file = File(directory, fileName)
        try {
            val fOut = FileOutputStream(file, true)
            val osw = OutputStreamWriter(fOut)
            osw.append("$tripName.txt\n")
            osw.close()
            fOut.close()
        } catch (t: Throwable) {
        }

    }

    fun updateDataFile() {
        val fileName = "data.txt"
        val sdCard = Environment.getExternalStorageDirectory()
        val directory = File(sdCard.getAbsolutePath() + "/TripDiary")
        var str = ""
        directory.mkdirs()
        var tripNum: Int

        val file = File(directory, fileName)
        try {
            val fIn = FileInputStream(file)
            val isr = InputStreamReader(fIn)
            val reader = BufferedReader(isr)
            str = reader.readLine()
            fIn.close()
            val fOut = FileOutputStream(file)
            val osw = OutputStreamWriter(fOut)

            tripNum = Integer.parseInt(str)
            tripNum++
            str = (tripNum).toString()
            osw.write(str)
            osw.close()
            fOut.close()
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

        } catch (e: java.io.FileNotFoundException) {
            str = createDataFile()
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            updateTripList(name!!)
        }
    }

    fun createDataFile(): String {
        val fileName = "data.txt"
        val sdCard = Environment.getExternalStorageDirectory()
        val directory = File(sdCard.getAbsolutePath() + "/TripDiary")
        directory.mkdirs()

        val file = File(directory, fileName)
        try {
            val fOut = FileOutputStream(file)
            val osw = OutputStreamWriter(fOut)
            osw.write("1")
            osw.close()
            fOut.close()
        } catch (t: Throwable) {
        }

        return "1"
    }

    companion object {
        private val TAG = "MapActivity"

        //Bitmap bm;
        var comment = ""
        var fileName = ""
        var titleName = ""
        var latitudeMarker: Double = 0.toDouble()
        var longitudeMarker: Double = 0.toDouble()

        var name: String? = null

    }
}