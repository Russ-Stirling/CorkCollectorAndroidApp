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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 2/10/2018.
 */

public class TastedWinesPop extends Activity {

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
        userId = getIntent().getStringExtra("userId");

        setContentView(R.layout.tasted_wines_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Determine the URL of our get request
        String url = "http://35.183.3.83/api/Tasting/List?userId="+ userId;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray tastingList) {

                        populateTastedWines(tastingList.length(), tastingList);

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

    void populateTastedWines(int tastedListSize, JSONArray tastingList)
    {

        //Access the scroll view so we can add wines to it
        LinearLayout tastedWinesLinearLayout = findViewById(R.id.tastedWinesLinearLayout);

        for(int tastingListIndex = 0; tastingListIndex < tastedListSize; tastingListIndex++)
        {
            final GridLayout wineGrid = new GridLayout(this);
            wineGrid.setRowCount(1);
            wineGrid.setColumnCount(4);

            final TextView wineNameTextView = new TextView(this);
            final TextView wineryNameTextView = new TextView(this);
            final TextView wineTypeTextView = new TextView(this);
            final TextView wineYearTextView = new TextView(this);

            try {

                //Set text and style of textview components
                wineNameTextView.setText(tastingList.getJSONObject(tastingListIndex).getString("wineName"));
                wineNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                wineryNameTextView.setText(tastingList.getJSONObject(tastingListIndex).getString("wineryName"));
                wineryNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                wineTypeTextView.setText(tastingList.getJSONObject(tastingListIndex).getString("wineType"));
                wineTypeTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                wineYearTextView.setText(Integer.toString(tastingList.getJSONObject(tastingListIndex).getInt("bottlingYear")));
                wineYearTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                //Set layout parameters of wine's name
                GridLayout.LayoutParams wineNameParams = new GridLayout.LayoutParams();
                wineNameParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                wineNameParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135, getResources().getDisplayMetrics());
                wineNameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                wineNameParams.rowSpec = GridLayout.spec(0, 1);
                wineNameParams.columnSpec = GridLayout.spec(0, 1);
                wineNameParams.setGravity(Gravity.CENTER_HORIZONTAL);
                wineNameTextView.setLayoutParams(wineNameParams);

                //Set layout parameters of winery's name
                GridLayout.LayoutParams wineryNameParams = new GridLayout.LayoutParams();
                wineryNameParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                wineryNameParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 99, getResources().getDisplayMetrics());
                wineryNameParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                wineryNameParams.rowSpec = GridLayout.spec(0, 1);
                wineryNameParams.columnSpec = GridLayout.spec(1, 1);
                wineryNameParams.setGravity(Gravity.CENTER_HORIZONTAL);
                wineryNameTextView.setLayoutParams(wineryNameParams);

                //Set layout parameters of wine's type
                GridLayout.LayoutParams wineTypeParams = new GridLayout.LayoutParams();
                wineTypeParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                wineTypeParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 99, getResources().getDisplayMetrics());
                wineTypeParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                wineTypeParams.rowSpec = GridLayout.spec(0, 1);
                wineTypeParams.columnSpec = GridLayout.spec(2, 1);
                wineNameParams.setGravity(Gravity.CENTER_HORIZONTAL);
                wineTypeTextView.setLayoutParams(wineTypeParams);

                //Set layout parameters of wine's type
                GridLayout.LayoutParams wineYearParams = new GridLayout.LayoutParams();
                wineYearParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                wineYearParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
                wineYearParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                wineYearParams.rowSpec = GridLayout.spec(0, 1);
                wineYearParams.columnSpec = GridLayout.spec(3, 1);
                wineYearTextView.setLayoutParams(wineYearParams);

                //Add textviews to the grid layout
                wineGrid.addView(wineNameTextView);
                wineGrid.addView(wineryNameTextView);
                wineGrid.addView(wineTypeTextView);
                wineGrid.addView(wineYearTextView);

                //Add the grid layout to the screen
                tastedWinesLinearLayout.addView(wineGrid);

                //Create a new onclick listener for the wine grid object
                final String tempID = tastingList.getJSONObject(tastingListIndex).getString("wineId");
                wineGrid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Load the wine screen
                        Intent myIntent = new Intent(TastedWinesPop.this,
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
