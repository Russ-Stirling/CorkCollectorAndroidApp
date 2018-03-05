package corkcollector.source;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WineScreen extends AppCompatActivity {

    //Bundle containing authentication token from login screen
    Bundle extras;
    String authToken;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wine_screen);

        Button addToCellar = findViewById(R.id.addToCellarButton);

        addToCellar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WineScreen.this, WineCellarQuantityPop.class));
            }
        });

        Button rateReview = findViewById(R.id.rateReviewButton);

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Get the extra values bundled with the screen change
        extras = getIntent().getExtras();

        //If there are values
        if(extras != null)
        {
            //Grab the auth token
            authToken = extras.getString("AUTH_TOKEN");
            userName = extras.getString("USER_NAME");

            //Grab the wine ID
            final String wineID = extras.getString("wineID");

            //Determine the URL of our get request
            String url = "http://35.183.3.83/api/Wine?id=" + wineID;

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject wine) {

                            try
                            {
                                //TODO: Expand these once the database has been updated
                                //Get the required parameters for the winery page
                                String wineName = wine.getString("wineName");
                                String wineType = wine.getString("wineType");
                                int wineYear = wine.getInt("bottlingYear");
                                JSONArray reviews = wine.getJSONArray("reviews");

                                //Grab the required objects from the winery screen
                                TextView nameText = findViewById(R.id.wineNameText);
                                TextView typeYearText = findViewById(R.id.wineTypeYearText);

                                //Set the appropriate values for the page
                                nameText.setText(wineName);
                                typeYearText.setText(wineType + " " + Integer.toString(wineYear));

                                //Populate the review component of the screen
                                populateReviews(reviews.length(), reviews);

                            }

                            catch (JSONException e)
                            {
                                //Create a toast message to indicate an error
                                Context context = getApplicationContext();
                                CharSequence text = "Error: Could not load this wine";
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

            rateReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent myIntent = new Intent(WineScreen.this,
                            RateReviewPop.class);
                    //Send over the winery ID
                    myIntent.putExtra("wineryID", wineID);
                    myIntent.putExtra("AUTH_TOKEN", authToken);
                    myIntent.putExtra("USER_NAME", userName);

                    startActivity(myIntent);
                }
            });
        }
    }

    void populateReviews(int reviewArraySize, JSONArray reviewObjArray)
    {

        //Access the layout for all reviews
        LinearLayout reviewMainLinearLayout = findViewById(R.id.wineReviewLinearLayout);

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
                reviewRating.setRating(reviewObject.getInt("rating"));

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
                reviewContent.setText(reviewObject.getString("text"));
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
                Intent myIntent = new Intent(WineScreen.this,
                        MapsScreen.class);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent2 = new Intent(WineScreen.this,
                        WineryScreen.class);
                startActivity(myIntent2);
                break;
            case R.id.item3:
                Intent myIntent3 = new Intent(WineScreen.this,
                        WineScreen.class);
                startActivity(myIntent3);
                break;
            case R.id.item4:
                Intent myIntent4 = new Intent(WineScreen.this,
                        ProfileScreen.class);
                startActivity(myIntent4);
                break;
            case R.id.item6:
                Intent myIntent6 = new Intent(WineScreen.this,
                        RequestScreen.class);
                startActivity(myIntent6);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
