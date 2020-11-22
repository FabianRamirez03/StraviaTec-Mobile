package com.example.straviatec

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.straviatec.dataBase.Actividad
import com.example.straviatec.dataBase.ActividadDBHelper
import com.example.straviatec.dataBase.CarreraDBHelper
import com.example.straviatec.dataBase.RetoDBHelper
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToLong

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locat:Location
    lateinit var polyLineOptions: PolylineOptions
    lateinit var url:String
    lateinit var tipo:String
    var points = mutableListOf<Location>()
    val PERMISSION_ID = 1010
    var counter = 0
    var start = false
    var flag = false
    var seconds = 0
    var minutes = 0
    var hours = 0
    var stopText = ""
    var lastEscalado = 0.0
    var totalEscalado = 0.0
    var init = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        url = intent.getStringExtra("url").toString()
        tipo = intent.getStringExtra("Tipo").toString()
        var id = getIntent().getExtras()?.getInt("id")!!
        var usuario = getIntent().getExtras()?.getInt("usuario")!!
        Log.w("TIPO",tipo)
        stopWatch.setOnClickListener{
            start = !start
        }

        if (tipo != "actividad"){
            nombreAct.visibility = View.INVISIBLE
            spinner1.visibility = View.INVISIBLE
        }
        else{
            var items = listOf<String>("Correr", "Nadar", "Ciclismo","Senderismo","Kayak","Caminata")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items)
            spinner1.adapter = adapter
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fixedRateTimer("timer", false, 0L,  1000) {
            runOnUiThread {
                if (start){
                    flag = true
                    RequestPermission()
                    getLastLocation()
                    showGpx()
                    distance.text = distance().toString() + "Km"
                    velProm.text = speed().toString() + "Km/h"
                    escalado()
                    //Mostrar Tiempo en Cronometro
                    seconds++
                    if (seconds % 60 ==0 && seconds != 0){
                        minutes++
                        seconds = 0
                    }
                    if (minutes % 60 ==0 && minutes != 0){
                        hours++
                        minutes = 0
                    }
                    stopText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    stopWatch.text = "$stopText \n STOP"
                }
                else if (flag){
                    flag = false
                    var file = File(this@MapsActivity.filesDir,"rutas.gpx")
                    var gpx = GPX.writePath(file,"gpxLoc",points)
                    if(tipo == "reto"){
                        println("ENTRAAAA")
                        val retoDb = RetoDBHelper(this@MapsActivity)
                        val reto = retoDb.getReto(id)
                        reto.recorrido = gpx

                        //Kilometraje
                        if (!reto.kilometraje.isNullOrEmpty()){
                            val distancia = distance()
                            val kilometraje = (reto.kilometraje?.toFloatOrNull()?.plus(distancia))
                            if (kilometraje != null) {
                                reto.kilometraje = ((Math.round(kilometraje * 1000) / 1000.0).toString())
                            }
                        }
                        else{
                            reto.kilometraje = (distance().toInt()).toString()
                        }

                        //Altura
                        if (!reto.altura.isNullOrEmpty()){
                            val altura = (reto.altura?.toFloatOrNull()?.plus(totalEscalado))
                            if (altura != null) {
                                val alt = totalEscalado.toInt()
                                Log.e("ALTURA",alt.toString())
                                reto.altura = alt.toString()
                            }
                        }
                        else{
                            reto.altura = (totalEscalado.toInt()).toString()
                        }

                        //Duracion
                        if(!reto.duracion.isNullOrEmpty()){
                            val time = LocalTime.parse(reto.duracion, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
                            hours += time.hour
                            minutes += time.minute
                            seconds += time.second
                            if (seconds >=60 ){
                                minutes++
                                seconds = seconds % 60
                            }
                            if (minutes >= 60){
                                hours++
                                minutes = minutes % 60
                            }

                        }
                        println(reto.tipoReto)
                        if(reto.tipoReto == "fondo"){
                            if (reto.objetivo.toFloat() <= reto.kilometraje.toFloat()){
                                reto.completitud = true
                            }
                        }
                        else if(reto.tipoReto == "altitud"){
                            println("ALTITUUUUUD")
                            if (reto.objetivo.toFloat() <= reto.altura.toFloat()){
                                reto.completitud = true
                                Log.e("COMP", "TRUE")
                            }
                            else{
                                Log.e("COMP", "False")
                            }
                        }
                        reto.duracion = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        Log.w("GPX",reto.recorrido)
                        retoDb.updateReto(reto,id)
                        Log.e("Duracion", retoDb.getReto(id).duracion)
                    }
                    else if (tipo == "carrera"){
                        val carreraDb = CarreraDBHelper(this@MapsActivity)
                        val carrera = carreraDb.getCarrera(id)
                        carrera.recorrido = gpx
                        carrera.kilometraje = distance().toInt().toString()
                        carrera.altura = totalEscalado.toInt().toString()
                        carrera.duracion = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        carrera.completitud = true
                        carreraDb.updateCarrera(carrera,id)

                    }
                    //Crear Actividad en la base de datos
                    else if (tipo == "actividad"){
                        val actividadDb = ActividadDBHelper(this@MapsActivity)
                        actividadDb.createActividad(actividadDb.readableDatabase,
                        Actividad(usuario,actividad.text.toString(),distance().toString(),"0",gpx,spinner1.selectedItem.toString(),String.format("%02d:%02d:%02d", hours, minutes, seconds),
                            LocalDateTime.now(),false)
                        )
                        Log.e("ACTIVIDAD", "ENTRA")
                        Log.e("ACTIVIDAD", actividadDb.getListaActividades(usuario)[0].recorrido)
                    }
                    val intent = Intent(this@MapsActivity,FeedActivity::class.java)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            }
        }
        setupListeners()
    }
    //Funcion para calcular la velocidad promedio
    fun speed():Double{
        if (points.size< 2){
            return 0.0
        }
        var speed = 0.0
        var cont = 0
        for (i in 0 until points.size - 1){
            cont++
            val radius =  6371e3
            val lat1 = points[i].latitude
            val lat2 = points[i+1].latitude
            val lon1 = points[i].longitude
            val lon2 = points[i+1].longitude
            val theta = deg2rad(lon2 - lon1)
            val phi = deg2rad(lat2 - lat1)
            var dist = (Math.sin(phi/2) * Math.sin(phi/2) +
                    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                    Math.sin(theta/2) * Math.sin(theta/2))
            dist = 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1-dist))
            dist = (dist * radius)/1000
            if (dist > 0.0){
                speed += dist*3600
            }
        }
        return Math.round((speed/cont) * 1000.0) / 1000.0
    }
    //Funcion para calcular la distancia recorrida
    fun distance(): Double {
        if (points.size< 2){
            return 0.0
        }
        var total = 0.0
        for (i in 0 until points.size - 1){
            val radius =  6371e3
            val lat1 = points[i].latitude
            val lat2 = points[i+1].latitude
            val lon1 = points[i].longitude
            val lon2 = points[i+1].longitude
            val theta = deg2rad(lon2 - lon1)
            val phi = deg2rad(lat2 - lat1)
            var dist = (Math.sin(phi/2) * Math.sin(phi/2) +
                    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                    Math.sin(theta/2) * Math.sin(theta/2))
            dist = 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1-dist))
            dist = (dist * radius)/1000
            total += dist
        }
        val totalDeci:Double = Math.round(total * 1000.0) / 1000.0
        return totalDeci
    }
    fun escalado(){
        if (init){
            if (points.size > 2){
                if (locat.altitude > lastEscalado){
                    totalEscalado += locat.altitude - lastEscalado
                }
                lastEscalado = locat.altitude
            }
            else{
                lastEscalado = locat.altitude
            }
        }
}

    //Funcion que muestra el recorrido del atleta
    fun showGpx(){
        val poly = PolylineOptions()
        for (i in 0 until points.size){
           poly.add(LatLng(points[i].latitude, points[i].longitude))
        }
        poly.width(5F)
        poly.color(Color.BLUE)
        mMap.addPolyline(poly)
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()
    }

    //Funcion que verifica si se tiene el permiso
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    //Función que habilita la ubicación
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true

        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location:Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        locat = location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locat.latitude,locat.longitude),18.0f))
                        getCityName(locat.latitude,locat.longitude, locat.altitude)
                        Log.d("Debug:" ,"Your Location:"+ location.longitude)
                        points.add(location)
                        init = true
                    }
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location", Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }

    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            locat = lastLocation
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locat.latitude,locat.longitude),18.0f))
            getCityName(lastLocation.latitude,lastLocation.longitude, lastLocation.altitude)
            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
        }
    }

    private fun getCityName(lat: Double,long: Double, alt: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())


        try {
            var Adress = geoCoder.getFromLocation(lat,long,3)
            ciudad.visibility = View.VISIBLE
            cityName = Adress.get(0).locality
            countryName = Adress.get(0).countryName
        }
        catch (e:Exception){
            ciudad.visibility = View.INVISIBLE
        }

        Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName +  "Altitude: " + alt)
        val retStr = cityName +  ", " + countryName  +"\n" + " Altitud: " + alt.toInt() + " m s. n. m."
        ciudad.text = retStr
        return retStr
    }

    fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    //Funciones de spotify
    private fun setupListeners() {
        SpotifyService.suscribeToChanges {
            SpotifyService.getImage(it.imageUri){
                imageView.setImageBitmap(it)
            }
        }
        playButton.setOnClickListener {
            SpotifyService.play("spotify:playlist:3CgjYW0HGFA90jwMavXicr")
        }

        pauseButton.setOnClickListener {
            SpotifyService.pause()
        }
        resumeButton.setOnClickListener {
            SpotifyService.resume()
        }
    }
    override fun onStop() {
        super.onStop()
        SpotifyService.disconnect()
    }
    

}