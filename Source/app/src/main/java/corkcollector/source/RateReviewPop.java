package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 1/29/2018.
 */

public class RateReviewPop extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Grab bundled parameters
        final String userName = getIntent().getStringExtra("USER_NAME");
        final String authToken = getIntent().getStringExtra("AUTH_TOKEN");
        final String subjectId = getIntent().getStringExtra("subjectID");
        final String routeParam = getIntent().getStringExtra("ROUTE_PARAM");
        final String userId = getIntent().getStringExtra("userId");
        final String type = getIntent().getStringExtra("type");

        //Pull up the popup window
        setContentView(R.layout.rate_review_popup_window);

        //Create an HTTP request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Adjust window size based on device metrics
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        final EditText userEditText = findViewById(R.id.editText);
        final RatingBar userRatingBar = findViewById(R.id.ratingBar);
        final TextView rateReviewTitleText = findViewById(R.id.rateReviewTitleText);
        final Button deleteButton = findViewById(R.id.deleteButton);

        if(type.equals("edit")){

            //Set the review text and number to its pre-existing value
            final String reviewText = getIntent().getStringExtra("reviewText");
            userEditText.setText(reviewText);

            final int reviewRating = getIntent().getIntExtra("reviewRating", 0);
            userRatingBar.setRating(reviewRating);

            rateReviewTitleText.setText("Edit Review");
        }

        else{

            deleteButton.setEnabled(false);
        }

        //Grab the review submission button and set an on-click listener
        Button submit = findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Grab the rating text and stars of the review
                final String userReviewText = userEditText.getText().toString();
                final int rating = (int) userRatingBar.getRating();

                if(type.equals("edit")){

                    String url = "http://35.183.3.83/api/"+routeParam+"/review";

                    StringRequest reviewPutRequest = new StringRequest(Request.Method.PUT, url,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Review Updated!";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    recreate();
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Context context = getApplicationContext();
                                    CharSequence text = "Error: Could not update your review";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    finish();
                                }
                            }
                    ){
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", "Bearer "+ authToken);
                            params.put("SubjectId", subjectId);
                            params.put("Rating", Integer.toString(rating));
                            params.put("Text", userReviewText);
                            params.put("UserId", userId);
                            params.put("UserName", userName);
                            return params;
                        }

                    };

                    queue.add(reviewPutRequest);

                }

                else if (type.equals("new")){

                    //Assemble the post request and add it to the queue
                    StringRequest reviewRequest = createPostRequest(userReviewText, rating, userName, subjectId, authToken, routeParam);
                    queue.add(reviewRequest);
                }

                else{

                    Context context = getApplicationContext();
                    CharSequence text = "Error: Something went wrong...";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: make a delete request as well

            }
        });
    }

    private StringRequest createPostRequest(final String reviewText, final int rating, final String userName, final String subjectId, final String reqAuthToken, String routeParam)
    {
        String url = "http://35.183.3.83/api/"+routeParam+"/review";

        StringRequest loginPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Context context = getApplicationContext();
                        CharSequence text = "Review posted!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Print "oh no!" in log if unsuccessful
                        Log.d("Error.Response", "oh no!");

                        Context context = getApplicationContext();
                        CharSequence text = "Error: Could not post your review";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        finish();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer "+ reqAuthToken);
                params.put("SubjectId", subjectId);
                params.put("Rating", Integer.toString(rating));
                params.put("Text", reviewText);
                params.put("UserId", "testId");
                params.put("UserName", userName);
                return params;
            }

        };

        return loginPostRequest;
    }







}
