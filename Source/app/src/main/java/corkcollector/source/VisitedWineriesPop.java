package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 2/10/2018.
 */

public class VisitedWineriesPop extends Activity {

    Bundle extras;
    String authToken;
    String userName;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extras = getIntent().getExtras();
        authToken = getIntent().getStringExtra("AUTH_TOKEN");
        userName = getIntent().getStringExtra("USER_NAME");
        userId = getIntent().getStringExtra("USER_ID");

        setContentView(R.layout.visited_wineries_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Determine the URL of our get request
        String url = "http://35.183.3.83/api/Checkin/List?userId="+ userId;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray visitList) {

                        populateVisitedWineries(visitList.length(), visitList);

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
    }

    void populateVisitedWineries(int visitListSize, JSONArray visitList)
    {
        //Access the scroll view so we can add wineries to it
        LinearLayout visitedWineriesLinearLayout = findViewById(R.id.visitedWineriesLinearLayout);

        for(int visitListIndex = 0; visitListIndex < visitListSize; visitListIndex++)
        {
            final GridLayout wineryGrid = new GridLayout(this);
            wineryGrid.setRowCount(1);
            wineryGrid.setColumnCount(2);

            final TextView wineryNameTextView = new TextView(this);
            final TextView visitDateTextView = new TextView(this);

            try {

                //Set text and style of textview components
                wineryNameTextView.setText(visitList.getJSONObject(visitListIndex).getString("wineryName"));
                wineryNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                String visitDate = visitList.getJSONObject(visitListIndex).getString("visitTime");
                visitDate = visitDate.substring(0, 10);
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String formattedDate = "Error";

                try{
                    Date date = originalFormat.parse(visitDate);
                    formattedDate = targetFormat.format(date);
                }

                catch (ParseException e){
                    e.printStackTrace();
                }

                visitDateTextView.setText(formattedDate);
                //visitDateTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                //Set layout parameters of winery's name
                GridLayout.LayoutParams wineryNameParams = new GridLayout.LayoutParams();
                wineryNameParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                wineryNameParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
                wineryNameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                wineryNameParams.rowSpec = GridLayout.spec(0, 1);
                wineryNameParams.columnSpec = GridLayout.spec(0, 1);
                wineryNameParams.setGravity(Gravity.CENTER_HORIZONTAL);
                wineryNameTextView.setLayoutParams(wineryNameParams);

                //Set layout parameters of visit date
                GridLayout.LayoutParams visitDateParams = new GridLayout.LayoutParams();
                visitDateParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                visitDateParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                visitDateParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                visitDateParams.rowSpec = GridLayout.spec(0, 1);
                visitDateParams.columnSpec = GridLayout.spec(1, 1);
                //visitDateParams.setGravity(Gravity.CENTER_HORIZONTAL);
                visitDateTextView.setLayoutParams(visitDateParams);

                //Add textviews to the grid layout
                wineryGrid.addView(wineryNameTextView);
                wineryGrid.addView(visitDateTextView);

                //Add the grid layout to the screen
                visitedWineriesLinearLayout.addView(wineryGrid);

                //Create a new onclick listener for the wine grid object
                final String tempID = visitList.getJSONObject(visitListIndex).getString("wineryId");
                wineryGrid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Load the wine screen
                        Intent myIntent = new Intent(VisitedWineriesPop.this,
                                WineryScreen.class);

                        //Send over the wineID
                        myIntent.putExtra("wineryID", tempID);
                        myIntent.putExtra("AUTH_TOKEN", authToken);
                        myIntent.putExtra("USER_NAME", userName);
                        myIntent.putExtra("Latitude", extras.getDouble("latitude"));
                        myIntent.putExtra("Longitude", extras.getDouble("longitude"));

                        //Start the wine screen activity
                        startActivity(myIntent);

                    }
                });


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


    }
}
