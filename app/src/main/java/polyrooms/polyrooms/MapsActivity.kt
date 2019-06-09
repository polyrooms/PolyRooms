package polyrooms.polyrooms

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity() {

    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_maps)

        val viewModel = ViewModelProviders.of(this).get(DataStore::class.java)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.Builder().fromUrl("mapbox://styles/weewooweewoo/cjwb9t6n91sfb1cmhrizhpmi0")) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                val time = intent.extras.get("time") as Time
                println(time)

                /*

                Call this function rather than just setMarkers once database is up and running

                viewModel.getBuildings(time).observe(this, Observer { buildings ->
                    setMarkers(mapboxMap, buildings)
                }) */

                setMarkers(mapboxMap, null)
                mapboxMap.setOnMarkerClickListener { marker ->
                    Toast.makeText(this, marker.title, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("building", marker.title)
                    startActivity(intent)
                    return@setOnMarkerClickListener true
                }
            }
        }
    }

    private fun setMarkers(mapboxMap: MapboxMap, buildings: List<Building>?) {
        val iconFactory = IconFactory.getInstance(this)
        val greenMarker = iconFactory.fromResource(R.drawable.green_marker)
        val redMarker = iconFactory.fromResource(R.drawable.red_marker)
        val buildingsInfo = BuildingsInfo()
        val coordinates = buildingsInfo.getCoordinates()

        coordinates.forEach {
            (buildingNum, coordinates) -> mapboxMap.addMarker(MarkerOptions()
                .position(LatLng(coordinates.first, coordinates.second))
                .title(buildingNum.toString())
                .icon(greenMarker))
        }

        /*

        Use this code once database is up and running. For now the map just labels every building green

        buildings?.forEach { building ->
            val location: Pair<Double, Double>? = coordinates.get(building.buildingNumber.toInt())
            val usedMarker: com.mapbox.mapboxsdk.annotations.Icon

            if (location != null) {
                if (building.rooms.isNotEmpty()) {
                    usedMarker = greenMarker
                } else {
                    usedMarker = redMarker
                }
                mapboxMap.addMarker(MarkerOptions()
                        .position(LatLng(location.first, location.second))
                        .title(building.buildingNumber)
                        .icon(usedMarker))
            }
        }
        */
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView?.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
