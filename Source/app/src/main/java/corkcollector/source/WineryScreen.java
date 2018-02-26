package corkcollector.source;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WineryScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load the content layout of the winery screen
        setContentView(R.layout.activity_winery_screen);

        //Access the tasting menu and rate/review menu button objects
        Button tastingMenu = (Button) findViewById(R.id.viewMenuButton);
        Button rateReview = (Button) findViewById(R.id.rateReviewButton);

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
            String url = "http://35.183.3.83/api/winery?id=" + wineryID;

            //This is called when the app's get request goes through
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject winery) {

                            //Print response in log if successful
                            Log.d("Response", winery.toString());

                            try {

                                //Get the required parameters for the wine page
                                String address = winery.getString("Address");
                                String phoneNumber = winery.getString("PhoneNumber");
                                String name = winery.getString("WineryName");
                                int rating = winery.getInt("Rating");
                                JSONArray reviews = winery.getJSONArray("Reviews");

                                final int reviewNum = reviews.length();
                                String[] reviewAuthorArray = new String[reviewNum];
                                int[] reviewRatingArray = new int[reviewNum];
                                String[] reviewTextArray = new String[reviewNum];

                                for (int x = 0; x < reviewNum; x++)
                                {
                                    JSONObject review = reviews.getJSONObject(x);
                                    reviewAuthorArray[x] = review.getString("UserName");
                                    reviewRatingArray[x] = review.getInt("Rating");
                                    reviewTextArray[x] = review.getString("Text");
                                }

                                //Grab the required objects from the winery screen
                                TextView addressText = findViewById(R.id.wineryAddressText);
                                TextView phoneNumberText = findViewById(R.id.wineryPhoneNumberText);
                                TextView nameText = findViewById(R.id.wineryName);
                                RatingBar ratingBar = findViewById(R.id.wineryRatingBar);

                                TextView[] reviewAuthorObjects = new TextView[reviewNum];
                                RatingBar[] reviewRatingObjects = new RatingBar[reviewNum];
                                TextView[] reviewTextObjects = new TextView[reviewNum];

                                for (int x = 0; x < reviewNum; x++)
                                {
                                    String reviewAuthorID = "ratingAuthorText";
                                    String reviewRatingID = "individualRatingBar";
                                    String reviewTextID = "reviewText";

                                    if (x > 0)
                                    {
                                        reviewAuthorID += Integer.toString(x + 1);
                                        reviewRatingID += Integer.toString(x + 1);
                                        reviewTextID += Integer.toString(x + 1);

                                    }

                                    int reviewAuthorResID = getResources().getIdentifier(reviewAuthorID, "id", getPackageName());
                                    int reviewRatingResID = getResources().getIdentifier(reviewRatingID, "id", getPackageName());
                                    int reviewTextResID = getResources().getIdentifier(reviewTextID, "id", getPackageName());

                                    reviewAuthorObjects[x] = findViewById(reviewAuthorResID);
                                    reviewRatingObjects[x] = findViewById(reviewRatingResID);
                                    reviewTextObjects[x] = findViewById(reviewTextResID);
                                }

                                //Set the appropriate values
                                addressText.setText(address);
                                phoneNumberText.setText(phoneNumber);
                                nameText.setText(name);
                                ratingBar.setNumStars(rating);

                                for (int x = 0; x < reviewNum; x++)
                                {
                                    reviewAuthorObjects[x].setText(reviewAuthorArray[x]);
                                    reviewRatingObjects[x].setNumStars(reviewRatingArray[x]);
                                    reviewTextObjects[x].setText(reviewTextArray[x]);
                                }

                            }
                            catch (JSONException e) {

                                //Print "oh no!" in log if unsuccessful
                                Log.d("Error.Response", "oh no!");

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
            );

            //Add it to the queue and send it automatically
            queue.add(getRequest);

            //Set the onclick listener for the tasting menu
            tastingMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //startActivity(new Intent(WineryScreen.this, tastingMenuPop.class));

                    //Load the popup menu
                    Intent myIntent = new Intent(WineryScreen.this,
                            tastingMenuPop.class);

                    //Send over the winery ID
                    myIntent.putExtra("wineryID", wineryID);

                    startActivity(myIntent);
                }
            });

            //Set the onclick listener for the review menu
            rateReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(WineryScreen.this, RateReviewPop.class));
                }
            });

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
                Intent myIntent = new Intent(WineryScreen.this,
                        MapsScreen.class);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent2 = new Intent(WineryScreen.this,
                        WineryScreen.class);
                startActivity(myIntent2);
                break;
            case R.id.item3:
                Intent myIntent3 = new Intent(WineryScreen.this,
                        WineScreen.class);
                startActivity(myIntent3);
                break;
            case R.id.item4:
                Intent myIntent4 = new Intent(WineryScreen.this,
                        ProfileScreen.class);
                startActivity(myIntent4);
                break;
            case R.id.item6:
                Intent myIntent6 = new Intent(WineryScreen.this,
                        RequestScreen.class);
                startActivity(myIntent6);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
