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
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 3/3/2018.
 */

public class WineCellarPop extends Activity {

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

        setContentView(R.layout.wine_cellar_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Determine the URL of our get request
        String url = "http://35.183.3.83/api/Cellar/InCellar?userId=" + userId;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray cellarList) {

                        populateCellar(cellarList.length(), cellarList);

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

    void populateCellar(int cellarListSize, JSONArray cellarList)
    {
        //Access the scroll view so we can add wines to it
        LinearLayout wineCellarLinearLayout = findViewById(R.id.wineCellarLinearLayout);

        for(int cellarListIndex = 0; cellarListIndex < cellarListSize; cellarListIndex++)
        {
            final GridLayout wineGrid = new GridLayout(this);
            wineGrid.setRowCount(1);
            wineGrid.setColumnCount(4);

            final TextView wineNameTextView = new TextView(this);
            final TextView wineryNameTextView = new TextView(this);
            final Button viewNotesButton = new Button(this);
            final Button drinkWineButton = new Button(this);

            try {

                String wineID = cellarList.getJSONObject(cellarListIndex).getString("wineId");

                //Set text and style of textview and button components
                wineNameTextView.setText(cellarList.getJSONObject(cellarListIndex).getString("wineName"));
                wineNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                //wineryNameTextView.setText(cellarList.getJSONObject(cellarListIndex).getString("wineryName"));
                wineryNameTextView.setText("Sample Winery");
                wineryNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                viewNotesButton.setText("Notes");
                viewNotesButton.setTextSize(10);
                viewNotesButton.setGravity(Gravity.CENTER);
                drinkWineButton.setText("Drink");
                drinkWineButton.setTextSize(10);
                drinkWineButton.setGravity(Gravity.CENTER);

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
                GridLayout.LayoutParams viewNotesParams = new GridLayout.LayoutParams();
                viewNotesParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                viewNotesParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
                viewNotesParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                viewNotesParams.rowSpec = GridLayout.spec(0, 1);
                viewNotesParams.columnSpec = GridLayout.spec(2, 1);
                viewNotesParams.setGravity(Gravity.CENTER_HORIZONTAL);
                viewNotesButton.setLayoutParams(viewNotesParams);

                //Create a new onclick listener for the drink wine button
                viewNotesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //notespop


                    }
                });

                //Set layout parameters of wine's type
                GridLayout.LayoutParams drinkWineParams = new GridLayout.LayoutParams();
                drinkWineParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                drinkWineParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
                drinkWineParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                drinkWineParams.rowSpec = GridLayout.spec(0, 1);
                drinkWineParams.columnSpec = GridLayout.spec(3, 1);
                drinkWineButton.setLayoutParams(drinkWineParams);

                //Create a new onclick listener for the drink wine button
                drinkWineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //


                    }
                });

                //Add textviews and buttons to the grid layout
                wineGrid.addView(wineNameTextView);
                wineGrid.addView(wineryNameTextView);
                wineGrid.addView(viewNotesButton);
                wineGrid.addView(drinkWineButton);

                //Add the grid layout to the screen
                wineCellarLinearLayout.addView(wineGrid);

            }

            catch (JSONException e) {

                //Print "oh no!" in log if unsuccessful
                Log.d("Error.Response", "oh no!");

                //Create a toast message to indicate an error
                Context context = getApplicationContext();
                CharSequence text = "Error: Could not load your wine cellar";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }
    }
}
