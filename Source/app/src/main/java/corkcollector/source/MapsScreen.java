package corkcollector.source;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsScreen extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    //The map itself
    private GoogleMap mMap;

   //Bundle containing authentication token from login screen
    Bundle extras;
    String authToken;
    String userName;

    //
    double myLatitude;
    double myLongitude;
    LocationManager mLocationManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
        }
        @Override
        public void onStatusChanged(String a, int b, Bundle x) {}
        @Override
        public void onProviderEnabled(String a) {}
        @Override
        public void onProviderDisabled(String a) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Get authentication token from bundle
        extras = getIntent().getExtras();
        authToken = getIntent().getStringExtra("AUTH_TOKEN");
        userName = getIntent().getStringExtra("USER_NAME");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,
                    1000, mLocationListener);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,
                    1000, mLocationListener);
        }
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

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            mMap.setMyLocationEnabled(true);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        //Restrict the map zoom
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(18.0f);

        //Restrict the map viewing range to the Niagara region
        LatLng southwest = new LatLng(43.1473, -79.4909);
        LatLng northeast = new LatLng(43.2652, -79.0547);
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //Centers and zooms the map to see the whole Niagara region
        LatLng nCenter = new LatLng(43.2000, -79.1150);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nCenter, 12.0f));

        /*Placing Pins on the map*/

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Determine the URL of our get request
        String url = "http://35.183.3.83/api/Winery";

        //This is called when the app's get request goes through
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            //Array of pins that will be loaded from the database
                            final int wineryArraySize = response.length();
                            Marker[] markerArray = new Marker[wineryArraySize];

                            //Loop through the JSON array
                            for (int wineryArrayIndex = 0; wineryArrayIndex < wineryArraySize; wineryArrayIndex++)
                            {

                                //Grab the winery objects
                                JSONObject wineryObj = response.getJSONObject(wineryArrayIndex);

                                //Save the latitude, longitude and name
                                double lat = wineryObj.getDouble("latitude");
                                double lon = wineryObj.getDouble("longitude");
                                String name = wineryObj.getString("wineryName");

                                //Place it in the marker array and on the map
                                LatLng lalo = new LatLng(lat, lon);
                                markerArray[wineryArrayIndex] = mMap.addMarker(new MarkerOptions().position(lalo));

                                //Grab the custom marker template from layout library
                                final LayoutInflater factory = getLayoutInflater();
                                LinearLayout tv = (LinearLayout) factory.inflate(R.layout.pin_replacement, null, false);

                                //Attach the winery's title to the pin
                                TextView wineryBlurb = (TextView) tv.getChildAt(0);
                                wineryBlurb.setText(name);

                                //Adjust the size of the custom pin
                                tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

                                //Create a bitmap from the XML structure and use it to replace the pin image
                                tv.setDrawingCacheEnabled(true);
                                tv.buildDrawingCache();
                                Bitmap bm = tv.getDrawingCache();
                                markerArray[wineryArrayIndex].setIcon(BitmapDescriptorFactory.fromBitmap(bm));

                                //Get the winery ID
                                String wineryID = wineryObj.getString("wineryId");

                                //Add it to the marker
                                markerArray[wineryArrayIndex].setTag(wineryID);
                            }

                        }
                        catch (JSONException e) {

                            //Print "oh no!" in log if unsuccessful
                            Log.d("Error.Response", "oh no!");

                            //Create a toast message to indicate an error
                            Context context = getApplicationContext();
                            CharSequence text = "Error: Could not place winery on map";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Print "oh no!" in log if unsuccessful
                        Log.d("Error.Response", "oh no!");

                        //Create a toast message to indicate an error
                        Context context = getApplicationContext();
                        CharSequence text = "Error: Could not connect to database";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer "+authToken);
                return params;
            }
        };

        //Add it to the RequestQueue and send automatically
        queue.add(getRequest);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        /*Disabling POIs*/
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            // Post error message to log if unsuccessful
            if (!success) {
                Log.d("oh no!", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("oh no!", "Can't find style. Error: ", e);
        }

    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

           // Load the default winery screen
           Intent myIntent2 = new Intent(MapsScreen.this,
                   WineryScreen.class);

           //Pass the marker's wineryID to the class
           myIntent2.putExtra("wineryID", marker.getTag().toString());
           myIntent2.putExtra("AUTH_TOKEN", authToken);
           myIntent2.putExtra("USER_NAME", userName);
           myIntent2.putExtra("Latitude", myLatitude);
           myIntent2.putExtra("Longitude", myLongitude);

           startActivity(myIntent2);

           // Return false to indicate that we have not consumed the event and that we wish
           // for the default behavior to occur (which is for the camera to move such that the
           // marker is centered and for the marker's info window to open, if it has one).
           return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.item1:
                Intent myIntent = new Intent(MapsScreen.this,
                        MapsScreen.class);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent2 = new Intent(MapsScreen.this,
                        WineryScreen.class);
                startActivity(myIntent2);
                break;
            case R.id.item3:
                Intent myIntent3 = new Intent(MapsScreen.this,
                        WineScreen.class);
                startActivity(myIntent3);
                break;
            case R.id.item4:
                Intent myIntent4 = new Intent(MapsScreen.this,
                        ProfileScreen.class);
                myIntent4.putExtra("USER_NAME", userName);
                myIntent4.putExtra("AUTH_TOKEN", authToken);
                startActivity(myIntent4);
                break;
            case R.id.item6:
                Intent myIntent6 = new Intent(MapsScreen.this,
                        RequestScreen.class);
                startActivity(myIntent6);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
