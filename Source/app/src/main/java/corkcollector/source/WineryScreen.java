package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WineryScreen extends AppCompatActivity {

    //Bundle containing authentication token from login screen
    Bundle extras;
    String authToken;
    String userName;
    String wineryID;
    String userID;
    double myLatitude;
    double myLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load the content layout of the winery screen
        setContentView(R.layout.activity_winery_screen);

        //Access the tasting menu and rate/review menu button objects
        Button tastingMenu = findViewById(R.id.viewMenuButton);
        final Button rateReview = findViewById(R.id.rateReviewButton);
        final Button checkInButton = findViewById(R.id.checkInButton);

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Get the extra values bundled with the screen change
        extras = getIntent().getExtras();

        //Grab our lat and long
        myLatitude = extras.getDouble("latitude");
        myLongitude = extras.getDouble("longitude");

        //If there are values
        if (extras != null)
        {

            //Grab the auth token
            authToken = extras.getString("AUTH_TOKEN");
            userName = extras.getString("USER_NAME");

            //Grab the winery ID
            wineryID = extras.getString("wineryID");
            userID = extras.getString("userId");

            //Determine the URL of our get request
            String url = "http://35.183.3.83/api/winery?id=" + wineryID;

            //This is called when the app's get request goes through
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject winery) {

                            try {

                                //Get the required parameters for the winery page
                                String address = winery.getString("address");
                                String phoneNumber = winery.getString("phoneNumber");
                                String name = winery.getString("wineryName");
                                JSONArray reviews = winery.getJSONArray("reviews");
                                Boolean hasMenu = winery.getBoolean("hasMenu");
                                double rawRating;
                                double rating;
                                try{
                                    rawRating = winery.getDouble("rating");
                                    rating = (int) rawRating;
                                }
                                catch(Exception e){
                                    rating = 0;
                                }
								final double wineryLatitude = winery .getDouble("latitude");
                                final double wineryLongitude = winery .getDouble("longitude");

                                //Grab the required objects from the winery screen
                                TextView addressText = findViewById(R.id.wineryAddressText);
                                TextView phoneNumberText = findViewById(R.id.wineryPhoneNumberText);
                                TextView nameText = findViewById(R.id.wineryName);
                                RatingBar ratingBar = findViewById(R.id.wineryRatingBar);

                                //Set the appropriate values for the page
                                addressText.setText(address);
                                phoneNumberText.setText(phoneNumber);
                                nameText.setText(name);
                                ratingBar.setRating((float)rating);

                                //Populate the review section
                                populateReviews(reviews.length(), reviews);

                                //If there is no tasting menu, disable the button
                                rateReview.setEnabled(hasMenu);

                                //Set an on-click listener for the check-in button
                                checkInButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //Find the absolute difference in latitude and longitude between us and the winery
                                        double currentLatitude = myLatitude;
                                        double currentLongitude = myLongitude;
                                        double latDiff = Math.abs(currentLatitude) - Math.abs(wineryLatitude);
                                        double longDiff = Math.abs(currentLongitude) - Math.abs(wineryLongitude);
                                        latDiff = Math.abs(latDiff);
                                        longDiff = Math.abs(longDiff);

                                        //If the current location is reasonably close to the winery
                                        if((latDiff < 0.005) && (longDiff < 0.005))
                                        {
                                            //Create a toast message to indicate an error
                                            Context context = getApplicationContext();
                                            CharSequence text = "Checking in...";
                                            int duration = Toast.LENGTH_SHORT;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();

                                            queue.add(checkIn());

                                        }
                                        else
                                        {
                                            //Create a toast message to indicate an error
                                            Context context = getApplicationContext();
                                            CharSequence text = "Not in range of winery!";
                                            int duration = Toast.LENGTH_SHORT;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        }


                                    }
                                });

                            }

                            catch (JSONException e) {

                                //Create a toast message to indicate an error
                                Context context = getApplicationContext();
                                CharSequence text = "Error: Could not load this winery";
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

            //Set the onclick listener for the tasting menu
            tastingMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Load the popup menu
                    Intent myIntent = new Intent(WineryScreen.this,
                            tastingMenuPop.class);

                    //Send over the winery ID
                    myIntent.putExtra("wineryID", wineryID);
                    myIntent.putExtra("AUTH_TOKEN", authToken);
                    myIntent.putExtra("USER_NAME", userName);
                    myIntent.putExtra("userId", userID);

                    startActivity(myIntent);
                }
            });

            //Set the onclick listener for the review button
            rateReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent myIntent = new Intent(WineryScreen.this,
                            RateReviewPop.class);
                    //Send over the winery ID
                    myIntent.putExtra("subjectID", wineryID);
                    myIntent.putExtra("AUTH_TOKEN", authToken);
                    myIntent.putExtra("USER_NAME", userName);
                    myIntent.putExtra("ROUTE_PARAM", "winery");
                    myIntent.putExtra("userId", userID);
                    myIntent.putExtra("type", "new");

                    //startActivity(myIntent);
                    startActivityForResult(myIntent, 1);

                }
            });

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                recreate();
            }
            if (resultCode == RESULT_CANCELED) {
                recreate();
            }
        }
    }

    void populateReviews(int reviewArraySize, JSONArray reviewObjArray)
    {
        //Access the layout for all reviews
        LinearLayout reviewMainLinearLayout = findViewById(R.id.wineryReviewLinearLayout);

        //Loop through the array of reviews
        for(int reviewArrayIndex = 0; reviewArrayIndex < reviewArraySize; reviewArrayIndex++)
        {
            //Create new view objects (rating bar is loaded from template for style reasons)
            final TextView reviewAuthor = new TextView(this);
            final RatingBar reviewRating = (RatingBar) getLayoutInflater().inflate(R.layout.ratingbar_template, null);
            final TextView reviewContent = new TextView(this);

            try
            {
                //Grab the winery object and use it to access its parameters
                JSONObject reviewObject = reviewObjArray.getJSONObject(reviewArrayIndex);

                //Set value and style of author's name
                reviewAuthor.setText(reviewObject.getString("userName"));
                reviewAuthor.setTextColor(Color.BLACK);
                reviewAuthor.setTypeface(null, Typeface.BOLD);
                reviewAuthor.setLines(1);
                reviewAuthor.setMaxLines(1);

                //Set layout parameters of author's name
                GridLayout.LayoutParams reviewAuthorParams = new GridLayout.LayoutParams();
                reviewAuthorParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                reviewAuthorParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                reviewAuthorParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
                reviewAuthorParams.rowSpec = GridLayout.spec(0, 1);
                reviewAuthorParams.columnSpec = GridLayout.spec(0, 1);
                reviewAuthor.setLayoutParams(reviewAuthorParams);

                //Set value of rating bar
                final int numStars = reviewObject.getInt("rating");
                reviewRating.setRating(numStars);

                //Set layout parameters of rating bar
                GridLayout.LayoutParams reviewRatingParams = new GridLayout.LayoutParams();
                reviewRatingParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
                reviewRatingParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                reviewRatingParams.topMargin  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                reviewRatingParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
                reviewRatingParams.rowSpec = GridLayout.spec(0, 1);
                reviewRatingParams.columnSpec = GridLayout.spec(1, 1);
                reviewRating.setLayoutParams(reviewRatingParams);

                //Set value and style of review content
                final String reviewText = reviewObject.getString("text");
                reviewContent.setText(reviewText);
                reviewContent.setLines(5);
                reviewContent.setMaxLines(5);

                //Set layout parameters of review content
                GridLayout.LayoutParams reviewContentParams = new GridLayout.LayoutParams();
                reviewContentParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                reviewContentParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                reviewContentParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                reviewContentParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                reviewContentParams.rowSpec = GridLayout.spec(1, 1);
                reviewContentParams.columnSpec = GridLayout.spec(0, 2);
                reviewContent.setLayoutParams(reviewContentParams);

                //Add new review box to the linear layout
                GridLayout reviewGrid = new GridLayout(this);
                reviewGrid.setColumnCount(2);
                reviewGrid.setRowCount(2);

                //Add the components of the review to the grid layout
                reviewGrid.addView(reviewAuthor);
                reviewGrid.addView(reviewRating);
                reviewGrid.addView(reviewContent);

                //Add the grid layout to the review section
                reviewMainLinearLayout.addView(reviewGrid);

                //Make review editable if it belongs to the user
                if(userName.equals(reviewObject.getString("userName"))){

                    reviewContent.setTextColor(Color.BLACK);

                    reviewGrid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent myIntent = new Intent(WineryScreen.this,
                                    RateReviewPop.class);
                            //Send over the winery ID
                            myIntent.putExtra("subjectID", wineryID);
                            myIntent.putExtra("AUTH_TOKEN", authToken);
                            myIntent.putExtra("USER_NAME", userName);
                            myIntent.putExtra("ROUTE_PARAM", "winery");
                            myIntent.putExtra("userId", userID);
                            myIntent.putExtra("type", "edit");
                            myIntent.putExtra("reviewText", reviewText);
                            myIntent.putExtra("reviewRating", numStars);
                            //startActivity(myIntent);
                            startActivityForResult(myIntent, 1);

                        }
                    });
                }

            }

            catch(JSONException e)
            {
                //Create a toast message to indicate an error
                Context context = getApplicationContext();
                CharSequence text = "Error: Could not load reviews from database";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

    }

    StringRequest checkIn()
    {
        String url = "http://35.183.3.83/api/Checkin/New";

        StringRequest tastePostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Context context = getApplicationContext();
                        CharSequence text = "Check-in posted!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Print "oh no!" in log if unsuccessful
                        Log.d("Error.Response", "oh no!");

                        Context context = getApplicationContext();
                        CharSequence text = "Error: Could not check-in";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ authToken);
                params.put("wineryId", wineryID);
                params.put("userId", userID);
                params.put("latitude", Double.toString(myLatitude));
                params.put("longitude", Double.toString(myLongitude));
                return params;
            }

        };

        return tastePostRequest;
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
                Intent myIntent = new Intent(WineryScreen.this,
                        MapsScreen.class);
                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent4 = new Intent(WineryScreen.this,
                        ProfileScreen.class);
                myIntent4.putExtra("USER_NAME", userName);
                myIntent4.putExtra("AUTH_TOKEN", authToken);
                myIntent4.putExtra("userId", userID);
                startActivity(myIntent4);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
