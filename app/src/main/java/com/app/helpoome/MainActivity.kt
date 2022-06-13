package com.app.helpoome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        var name = ""
        var address = ""
        var time = ""
        var wc = ""
        var dp = ""
        var call = ""
    }

    lateinit var myAdpater: MyAdapater
    val datas = mutableListOf<DataClass>()
//    lateinit var dbHelper: DBHelper
//    lateinit var sqlDB: SQLiteDatabase
//    var imm: InputMethodManager? = null

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val REQUEST_PERMISSION_CODE = 1
    val DEFAULT_ZOOM_LEVEL = 17f
    val CITY_HALL = LatLng(37.340378, 126.733848)
    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        dbHelper = DBHelper.getInstance(this)

        tabHost.setup()

        var tabSpecMap = tabHost.newTabSpec("tabMap").setIndicator("화장실 찾기")
        tabSpecMap.setContent(R.id.tabMap)
        tabHost.addTab(tabSpecMap)

        var tabSpecMy = tabHost.newTabSpec("tabMy").setIndicator("My 저장")
        tabSpecMy.setContent(R.id.tabMy)
        tabHost.addTab(tabSpecMy)

        tabHost.currentTab = 0

        mapView.onCreate(savedInstanceState)

        if (checkPermissions()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
        myLocationButton.setOnClickListener { onMyLocationButtonClick() }
        initRecycler()
    }

    private fun initRecycler() {
        myAdpater = MyAdapater(this)
        recyView.adapter = myAdpater

        datas.apply {
            add(DataClass(name = "정왕역", address = "경기 시흥시 마유로418번길 15"))
            add(DataClass(name = "한국공학대학교", address = "경기도 시흥시 산기대학로 237"))

            myAdpater.datas = datas
            myAdpater.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initMap()
    }

    private fun checkPermissions(): Boolean {

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun initMap() {
        mapView.getMapAsync {

            googleMap = it
            it.uiSettings.isMyLocationButtonEnabled = false

            when {
                checkPermissions() -> {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
                    it.isMyLocationEnabled = true
                }
                else -> {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {

        val locationProvider: String = LocationManager.GPS_PROVIDER
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocation: Location = locationManager.getLastKnownLocation(locationProvider)!!

        return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    }

    private fun onMyLocationButtonClick() {
        when {
            checkPermissions() -> googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL)
            )
            else -> Toast.makeText(applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    val API_KEY = "553c3eaf75784139889dbf32e160aa9d" //공공데이터 API_KEY
    var task: ToiletReadTask? = null
    var toilets = JSONArray()

    val bitmap by lazy {
        val bitmap = ResourcesCompat.getDrawable(resources,
            R.drawable.ic_baseline_person_pin_circle_24,
            null)?.toBitmap()
        Bitmap.createScaledBitmap(bitmap!!, 100, 100, false)
    }

    fun JSONArray.merge(anotherArray: JSONArray) {
        for (i in 0 until anotherArray.length()) {
            this.put(anotherArray.get(i))
        }
    }

    fun readData(startIndex: Int, lastIndex: Int): JSONObject {
        val url =
            URL("https://openapi.gg.go.kr/Publtolt?KEY=" + "${API_KEY}&Type=json&pIndex=${startIndex}&pSize=${lastIndex}")
        val connection = url.openConnection()
        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONObject(data)
    }

    @SuppressLint("StaticFieldLeak")
    inner class ToiletReadTask : AsyncTask<Void, JSONArray, String>() {

        override fun onPreExecute() {
            googleMap?.clear()
            toilets = JSONArray()
        }

        override fun doInBackground(vararg params: Void?): String {

            val step = 1000
            var startIndex = 3
            var lastIndex = step

            do {
                if (isCancelled) break

                val jsonObject = readData(startIndex, lastIndex)
                val check1 = jsonObject.getJSONArray("Publtolt")
                val rows = check1.getJSONObject(1).getJSONArray("row")

                toilets.merge(rows)
                publishProgress(rows)

                startIndex += 1

            } while (startIndex < 10)
            return "complete"
        }

        override fun onProgressUpdate(vararg values: JSONArray?) {
            val array = values[0]
            array?.let {
                for (i in 0 until array.length()) {
                    val json = array.getJSONObject(i)
                    var lat: String? = json.getString("REFINE_WGS84_LAT") ?: continue

                    if (lat != "" && lat != "null") {
                        if (json.getString("REFINE_LOTNO_ADDR").contains("경기도 시흥시", true)) {
                            addMarkers(array.getJSONObject(i))
                        }
                    }

                    googleMap?.setOnMarkerClickListener { m ->
                        val bottomSheet = BottomFragment()
                        // 마커는 좌표값
//                        Toast.makeText(applicationContext, "${m.title}", Toast.LENGTH_SHORT).show()
                        name = m.title!!.split('&')[0]
                        address = m.title!!.split('&')[1]
                        time = m.title!!.split('&')[2]
                        wc = m.title!!.split('&')[3]
                        call = m.title!!.split('&')[5]

                        if(m.title!!.split('&')[4] == "0"){
                            dp = "N"
                        } else {
                            dp = "Y"
                        }

                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                        true
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        task?.cancel(true)
        task = ToiletReadTask()
        task?.execute()
    }

    override fun onStop() {
        super.onStop()
        task?.cancel(true)
        task = null
    }

    fun addMarkers(toilet: JSONObject) {
        googleMap?.apply {
            addMarker(
                MarkerOptions()
                    .position(LatLng(toilet.getDouble("REFINE_WGS84_LAT"),
                        toilet.getDouble("REFINE_WGS84_LOGT")))
                    .title(toilet.getString("PBCTLT_PLC_NM") + "&" +
                            toilet.getString("REFINE_LOTNO_ADDR") + "&" +
                            toilet.getString("MALE_FEMALE_TOILET_YN") + "&" +
                            toilet.getString("MALE_FEMALE_TOILET_YN") + "&" +
                            toilet.getInt("MALE_DSPSN_WTRCLS_CNT") + toilet.getInt("MALE_DSPSN_UIL_CNT") + toilet.getInt(
                        "FEMALE_DSPSN_WTRCLS_CNT") + "&" +
                            toilet.getString("MANAGE_INST_TELNO"))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

            )
        }
    }
}