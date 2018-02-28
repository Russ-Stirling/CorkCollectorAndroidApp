package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
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

/**
 * Created by Bailey on 1/20/2018.
 */

public class tastingMenuPop extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set styling of popup window
        setContentView(R.layout.tasting_menu_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Get the extra values bundled with the screen change
        Bundle extras = getIntent().getExtras();

        //If there are values
        if (extras != null)
        {
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

            );

            //Add it to the queue and send it automatically
            queue.add(getRequest);

        }

    }

    //Populates the tasting menu popup window when passed an array of JSON wine objects
    void populateTastingMenu(int tastingMenuSize, JSONObject[] wineryObjArray)
    {

        //Access the relative layout so we can add wines to it
        RelativeLayout myRelativeLayout = findViewById(R.id.popupMenuLayout);

        //Loop through the tasting menu
        for(int tastingMenuIndex = 0; tastingMenuIndex < tastingMenuSize; tastingMenuIndex++)
        {
            //Create a new text view object
            final TextView tempView = new TextView(this);

            try
            {

                //TODO: Expand these once the database has been updated
                //Give the wine a title and description
                String tempText = wineryObjArray[tastingMenuIndex].getString("WineName") + " | " +
                        wineryObjArray[tastingMenuIndex].getString("WineType");
                tempView.setText(tempText);

                //Set the style of the text
                tempView.setTypeface(Typeface.SANS_SERIF);

                //Grab display metrics and measurements in pixels
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                int height = dm.heightPixels;

                //Set the text view to the horizontal center of the popup menu
                tempView.setWidth(width);
                tempView.setGravity(Gravity.CENTER_HORIZONTAL);

                //Incrementally spread out the text view vertically through the popup menu
                float yPos = (float) ((height * 0.15) + tastingMenuIndex * (height * 0.075));
                tempView.setY(yPos);

                //Add it to the screen
                myRelativeLayout.addView(tempView);

                //Create a new onclick listener for the wine object
                final String tempID = wineryObjArray[tastingMenuIndex].getString("WineId");
                tempView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Load the wine screen
                        Intent myIntent = new Intent(tastingMenuPop.this,
                                WineScreen.class);

                        //Send over the wineID
                        myIntent.putExtra("wineID", tempID);

                        //Start the wine screen activity
                        startActivity(myIntent);

                    }
                });

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


