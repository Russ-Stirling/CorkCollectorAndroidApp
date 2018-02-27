package corkcollector.source;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WineScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wine_screen);

        Button rateReview = findViewById(R.id.rateReviewButton);

        rateReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WineScreen.this, RateReviewPop.class));
            }
        });

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Get the extra values bundled with the screen change
        Bundle extras = getIntent().getExtras();

        //If there are values
        if(extras != null)
        {
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
                                String wineName = wine.getString("WineName");
                                String wineType = wine.getString("WineType");
                                JSONArray reviews = wine.getJSONArray("Reviews");

                                //Series of arrays for review parameters
                                final int reviewNum = reviews.length();
                                String[] reviewAuthorArray = new String[reviewNum];
                                int[] reviewRatingArray = new int[reviewNum];
                                String[] reviewTextArray = new String[reviewNum];

                                //Grab the details of each review individually
                                for (int x = 0; x < reviewNum; x++)
                                {
                                    JSONObject review = reviews.getJSONObject(x);
                                    reviewAuthorArray[x] = review.getString("UserName");
                                    reviewRatingArray[x] = review.getInt("Rating");
                                    reviewTextArray[x] = review.getString("Text");
                                }

                                //Grab the required objects from the winery screen
                                TextView nameText = findViewById(R.id.wineNameText);
                                TextView typeYearText = findViewById(R.id.wineTypeYearText);

                                //Create new arrays to grab review components from wine screen
                                TextView[] reviewAuthorObjects = new TextView[reviewNum];
                                RatingBar[] reviewRatingObjects = new RatingBar[reviewNum];
                                TextView[] reviewTextObjects = new TextView[reviewNum];

                                //Loop through review xml objects
                                for (int x = 0; x < reviewNum; x++)
                                {
                                    //Strings to label the review components
                                    String reviewAuthorID = "wineRatingAuthorText";
                                    String reviewRatingID = "wineIndividualRatingBar";
                                    String reviewTextID = "wineReviewText";

                                    //The string names change depending on their number
                                    if (x > 0)
                                    {
                                        reviewAuthorID += Integer.toString(x + 1);
                                        reviewRatingID += Integer.toString(x + 1);
                                        reviewTextID += Integer.toString(x + 1);
                                    }

                                    //Access and store review component objects
                                    int reviewAuthorResID = getResources().getIdentifier(reviewAuthorID, "id", getPackageName());
                                    int reviewRatingResID = getResources().getIdentifier(reviewRatingID, "id", getPackageName());
                                    int reviewTextResID = getResources().getIdentifier(reviewTextID, "id", getPackageName());

                                    //Save them in permanent arrays
                                    reviewAuthorObjects[x] = findViewById(reviewAuthorResID);
                                    reviewRatingObjects[x] = findViewById(reviewRatingResID);
                                    reviewTextObjects[x] = findViewById(reviewTextResID);
                                }

                                //Set the appropriate values for the page
                                nameText.setText(wineName);
                                typeYearText.setText(wineType);

                                //Dynamically set the review values
                                for (int x = 0; x < reviewNum; x++)
                                {
                                    reviewAuthorObjects[x].setText(reviewAuthorArray[x]);
                                    reviewRatingObjects[x].setNumStars(reviewRatingArray[x]);
                                    reviewTextObjects[x].setText(reviewTextArray[x]);
                                }

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
            );

            //Add it to the queue and send it automatically
            queue.add(getRequest);
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
