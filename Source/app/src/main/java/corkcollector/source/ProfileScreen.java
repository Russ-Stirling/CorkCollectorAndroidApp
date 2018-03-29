package corkcollector.source;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileScreen extends AppCompatActivity {

    //Bundle containing authentication token from login screen
    Bundle extras;
    String authToken;
    String userName;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get authentication token from bundle
        extras = getIntent().getExtras();
        authToken = getIntent().getStringExtra("AUTH_TOKEN");
        userName = getIntent().getStringExtra("USER_NAME");
        userId = getIntent().getStringExtra("userId");

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Determine the URL of our get request
        String url = "http://35.183.3.83/api/User/Profile?username="+ userName;

        //This is called when the app's get request goes through
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            //name, username, userid, email, friends, checkins, tastings, personal comments, cellar bottles
                            String name = response.getString("name");
                            JSONArray tastedList = response.getJSONArray("tastings");
                            JSONArray visitedList = response.getJSONArray("checkIns");
                            JSONArray friendList = response.getJSONArray("friends");
                            String dateJoined = response.getString("dateJoined");

                            int tastedQty = tastedList.length();
                            int visitedQty = visitedList.length();
                            int friendQty = friendList.length();

                            //userId = response.getString("userId");

                            TextView nameText = findViewById(R.id.userFullNameText);
                            nameText.setText(name);
                            TextView tastedNumText = findViewById(R.id.winesTastedInfoText);
                            tastedNumText.setText(Integer.toString(tastedQty));
                            TextView wineriesVisitedText = findViewById(R.id.wineriesVisitedInfoText);
                            wineriesVisitedText.setText(Integer.toString(visitedQty));
                            TextView dateText = findViewById(R.id.memberSinceInfoText);
                            dateText.setText(dateJoined);


                        }
                        catch (JSONException e) {

                            //Print "oh no!" in log if unsuccessful
                            Log.d("Error.Response", "oh no!");

                            //Create a toast message to indicate an error
                            Context context = getApplicationContext();
                            CharSequence text = "Error: Could not load your user profile";
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
                params.put("Authorization", "Bearer "+ authToken);
                return params;
            }
        };

        //Add it to the RequestQueue and send automatically
        queue.add(getRequest);

        //Fiddling with layout

        setContentView(R.layout.activity_profile_screen);

        Button tastedWines = (Button) findViewById(R.id.viewTastedWinesButton);

        tastedWines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ProfileScreen.this,
                        TastedWinesPop.class);

                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                myIntent.putExtra("userId", userId);

                startActivity(myIntent);
            }
        });

        Button recommendedWines = (Button) findViewById(R.id.viewRecommendedWinesButton);

        recommendedWines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ProfileScreen.this,
                        RecommendedWinesPop.class);

                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                myIntent.putExtra("userId", userId);

                startActivity(myIntent);
            }
        });

        Button visitedWineries = (Button) findViewById(R.id.viewVisitedWineriesButton);

        visitedWineries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ProfileScreen.this,
                        VisitedWineriesPop.class);

                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                myIntent.putExtra("userId", userId);
                myIntent.putExtra("Latitude", extras.getDouble("latitude"));
                myIntent.putExtra("Longitude", extras.getDouble("longitude"));

                startActivity(myIntent);
            }
        });

        Button wineCellar = (Button) findViewById(R.id.viewWineCellarButton);

        wineCellar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ProfileScreen.this,
                        WineCellarPop.class);

                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                myIntent.putExtra("userId", userId);

                startActivity(myIntent);
            }
        });
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
                Intent myIntent = new Intent(ProfileScreen.this,
                        MapsScreen.class);
                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                startActivity(myIntent);
                break;
            case R.id.item2:
                recreate();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
