package corkcollector.source;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*Map Setup*/

        //Restrict the map zoom
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(18.0f);

        //Restrict the map viewing range to the Niagara region
        LatLng southwest = new LatLng(43.1473, -79.1909);
        LatLng northeast = new LatLng(43.2652, -79.0547);
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //Centers and zooms the map to see the whole Niagara region
        LatLng nCenter = new LatLng(43.2000, -79.1150);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nCenter, 12.0f));

        /*Placing Pins on the map*/

        // Add a marker in Niagara on the lake
        LatLng niagara = new LatLng(43.2550, -79.0773);
        mMap.addMarker(new MarkerOptions().position(niagara).title("Marker in Niagara"));

        //mMap.setMyLocationEnabled(true);
    }
}
