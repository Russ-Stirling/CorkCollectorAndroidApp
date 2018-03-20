package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 1/20/2018.
 */

public class tastingMenuPop extends Activity{

    //Bundle containing authentication token from login screen
    Bundle extras;
    String authToken;
    String userName;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set styling of popup window
        setContentView(R.layout.tasting_menu_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Get the extra values bundled with the screen change
        extras = getIntent().getExtras();

        //If there are values
        if (extras != null)
        {
            //Grab the auth token
            authToken = extras.getString("AUTH_TOKEN");
            userName = extras.getString("USER_NAME");
            userId = extras.getString("userId");

            //Grab the winery ID
            final String wineryID = extras.getString("wineryID");

            //Determine the URL of our get request
            String url = "http://35.183.3.83/api/wine?wineryId="+ wineryID + "&onMenu=true";

            //This is called when the app's get request goes through
            JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>()
                    {
                        @Override
                        public void onResponse(JSONArray tastingMenu) {

                            try {

                                //Array of wines that will be loaded from the database
                                final int tastingMenuSize = tastingMenu.length();
                                final JSONObject[] wineryObjArray = new JSONObject[tastingMenuSize];

                                for(int tastingMenuIndex = 0; tastingMenuIndex < tastingMenuSize; tastingMenuIndex++)
                                {

                                    //Grab the wine objects
                                    wineryObjArray[tastingMenuIndex] = tastingMenu.getJSONObject(tastingMenuIndex);

                                    //If this is the last wine in the array
                                    if(tastingMenuIndex == (tastingMenuSize -1))
                                    {
                                        //Call the population function
                                        populateTastingMenu(tastingMenuSize, wineryObjArray);
                                    }

                                }

                            }

                            catch (JSONException e) {

                                //Create a toast message to indicate an error
                                Context context = getApplicationContext();
                                CharSequence text = "Error: Could not retrieve wines from database";
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

            //Add it to the queue and send it automatically
            queue.add(getRequest);

        }

    }

    //Populates the tasting menu popup window when passed an array of JSON wine objects
    void populateTastingMenu(int tastingMenuSize, JSONObject[] wineryObjArray)
    {

        //Access the relative layout so we can add wines to it
        //RelativeLayout myRelativeLayout = findViewById(R.id.popupMenuLayout);

        //Access the scroll view so we can add wines to it
        LinearLayout tastingMenuLinearLayout = findViewById(R.id.tastingMenuLinearLayout);

        //Loop through the tasting menu
        for(int tastingMenuIndex = 0; tastingMenuIndex < tastingMenuSize; tastingMenuIndex++)
        {
            //Create a new text view object
            //final TextView tempView = new TextView(this);

            //Create a new grid layout for the wine & 3 text views to store its info
            final GridLayout wineGrid = new GridLayout(this);
            wineGrid.setRowCount(1);
            wineGrid.setColumnCount(3);

            final TextView wineNameTextView = new TextView(this);
            final TextView wineTypeTextView = new TextView(this);
            final TextView wineYearTextView = new TextView(this);

            try
            {

                //If the wine is on the tasting menu
                if(wineryObjArray[tastingMenuIndex].getBoolean("onTastingMenu"))
                {
                    //Set text and style of textview components
                    wineNameTextView.setText(wineryObjArray[tastingMenuIndex].getString("wineName"));
                    wineNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    wineTypeTextView.setText(wineryObjArray[tastingMenuIndex].getString("wineType"));
                    wineTypeTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                    wineYearTextView.setText(Integer.toString((wineryObjArray[tastingMenuIndex].getInt("bottlingYear"))));
                    wineYearTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                    //Set layout parameters of wine's name
                    GridLayout.LayoutParams wineNameParams = new GridLayout.LayoutParams();
                    wineNameParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    wineNameParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
                    wineNameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    wineNameParams.rowSpec = GridLayout.spec(0, 1);
                    wineNameParams.columnSpec = GridLayout.spec(0, 1);
                    wineNameParams.setGravity(Gravity.CENTER_HORIZONTAL);
                    wineNameTextView.setLayoutParams(wineNameParams);

                    //Set layout parameters of wine's type
                    GridLayout.LayoutParams wineTypeParams = new GridLayout.LayoutParams();
                    wineTypeParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    wineTypeParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                    wineTypeParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    wineTypeParams.rowSpec = GridLayout.spec(0, 1);
                    wineTypeParams.columnSpec = GridLayout.spec(1, 1);
                    wineNameParams.setGravity(Gravity.CENTER_HORIZONTAL);
                    wineTypeTextView.setLayoutParams(wineTypeParams);

                    //Set layout parameters of wine's type
                    GridLayout.LayoutParams wineYearParams = new GridLayout.LayoutParams();
                    wineYearParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    wineYearParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    wineYearParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    wineYearParams.rowSpec = GridLayout.spec(0, 1);
                    wineYearParams.columnSpec = GridLayout.spec(2, 1);
                    wineYearTextView.setLayoutParams(wineYearParams);

                    //Add textviews to the grid layout
                    wineGrid.addView(wineNameTextView);
                    wineGrid.addView(wineTypeTextView);
                    wineGrid.addView(wineYearTextView);

                    //Add the grid layout to the screen
                    tastingMenuLinearLayout.addView(wineGrid);

                    //Create a new onclick listener for the wine grid object
                    final String tempID = wineryObjArray[tastingMenuIndex].getString("wineId");
                    wineGrid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Load the wine screen
                            Intent myIntent = new Intent(tastingMenuPop.this,
                                    WineScreen.class);

                            //Send over the wineID
                            myIntent.putExtra("wineID", tempID);
                            myIntent.putExtra("AUTH_TOKEN", authToken);
                            myIntent.putExtra("USER_NAME", userName);
                            myIntent.putExtra("userId", userId);

                            //Start the wine screen activity
                            startActivity(myIntent);

                        }
                    });
                }
            }

            catch (JSONException e)
            {
                //Create a toast message to indicate an error
                Context context = getApplicationContext();
                CharSequence text = "Error: Could not place wines on menu";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }

    }
}


