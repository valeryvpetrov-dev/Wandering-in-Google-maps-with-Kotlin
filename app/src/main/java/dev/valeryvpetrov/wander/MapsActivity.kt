package dev.valeryvpetrov.wander

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val mysFligeli = LatLng(81.858250, 59.112159)
        val zoomLevel = 14f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mysFligeli, zoomLevel))
        mMap.addMarker(MarkerOptions().position(mysFligeli).title("Mys Fligeli"))

        addOverlay(mMap, mysFligeli, R.drawable.ic_launcher, 100f)
        setOnMapLongClickListener(mMap)
        setOnPoiClickListener(mMap)
        setMapStyle(mMap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setOnMapLongClickListener(map: GoogleMap) {
        map.setOnMapLongClickListener {  latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                latLng.latitude, latLng.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_YELLOW
                    ))
            )
        }
    }

    private fun setOnPoiClickListener(map: GoogleMap) {
        map.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map
                .setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

            if (!success) Log.e(TAG, "Set map style failed.")
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun addOverlay(map: GoogleMap, position: LatLng, overlayResourceId: Int, overlaySize: Float) {
        val groundOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(overlayResourceId))
            .position(position, overlaySize)
        map.addGroundOverlay(groundOverlay)
    }
}
