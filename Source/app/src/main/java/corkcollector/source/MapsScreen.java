package corkcollector.source;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsScreen extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    //The map itself
    private GoogleMap mMap;

    //The pins that will be stored and placed on the map
    private Marker mNiagara;
    private Marker mTest;

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

        // TO DO: Here is where all the loading requests will take place

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Send get request to sample website
        String url = "http://35.183.3.83/api/Winery"; //Grab the array of wineries as a JSON object instead, then do a for each on it

        //Prepare the get Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {

                        //Print response in log if successful
                        Log.d("Response", response.toString());

                        try {

                            //for (int i = 0; i < response.length(); i++)
                            //{

                            //}

                            JSONObject wineryOne = response.getJSONObject(0);
                            double lat = wineryOne.getDouble("Latitude");
                            double lon = wineryOne.getDouble("Longitude");
                            String name = wineryOne.getString("WineryName");

                            LatLng lalo = new LatLng(lat, lon);
                            mTest = mMap.addMarker(new MarkerOptions().position(lalo).title(name));



                        }
                        catch (JSONException e) {
                            Log.d("Error.Response", "oh no!");

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

                        Context context = getApplicationContext();
                        CharSequence text = "Error: Could not connect to database";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                }
        );

        //Add it to the RequestQueue and send automatically
        queue.add(getRequest);

        // Need to grab latitude, longitude and some sort of ID number
        // Iterate through DB and generate all pins dynamically
        // Is there some way to cache them?

        // Add a marker in Niagara on the lake
        LatLng lNiagara = new LatLng(43.2550, -79.0773);
        mNiagara = mMap.addMarker(new MarkerOptions().position(lNiagara).title("Marker in Niagara"));

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
