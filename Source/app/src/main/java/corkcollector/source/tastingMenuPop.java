package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

        setContentView(R.layout.tasting_menu_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Array of pins that will be loaded from the database
        final int tastingMenuSize = 2;
        final String[] wineIDArray = new String[tastingMenuSize];
        final TextView[] wineTextViewArray = new TextView[tastingMenuSize];

        RelativeLayout myRelativeLayout = (RelativeLayout) findViewById(R.id.popupMenuLayout);

        for(int tastingMenuIndex = 0; tastingMenuIndex < tastingMenuSize; tastingMenuIndex++)
        {
            final TextView tempView = new TextView(this);

            tempView.setText("TESTING");
            tempView.setHeight(50);

            myRelativeLayout.addView(tempView);

            wineTextViewArray[tastingMenuIndex] = tempView;
        }

        //Create an object in the winery list
        TextView wineryAwineA = (TextView) findViewById(R.id.wineryAWineA);

        //Set an on-click listener for the object
        wineryAwineA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(tastingMenuPop.this, WineScreen.class));
                //Pass the wine ID to the wine screen
            }
        });



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

                                for(int tastingMenuIndex = 0; tastingMenuIndex < tastingMenuSize; tastingMenuIndex++)
                                {

                                    //Grab the wine objects
                                    JSONObject wineObj = tastingMenu.getJSONObject(tastingMenuIndex);

                                    //Save the name and type
                                    String name = wineObj.getString("WineName");
                                    String type = wineObj.getString("WineType");

                                    //Save the ID in the array
                                    wineIDArray[tastingMenuIndex] = wineObj.getString("WineId");

                                }

                            }

                            catch (JSONException e) {

                                //Create a toast message to indicate an error
                                Context context = getApplicationContext();
                                CharSequence text = "Error: Could not place wines on menu";
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
}
