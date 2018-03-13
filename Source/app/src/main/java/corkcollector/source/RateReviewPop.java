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

        //Grab the review submission button and set an on-click listener
        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Grab the text content of the review
                EditText userEditText = findViewById(R.id.editText);
                String userReviewText = userEditText.getText().toString();

                //Grab the rating number of the review
                RatingBar userRatingBar = findViewById(R.id.ratingBar);
                int rating = (int) userRatingBar.getRating();


                //Assemble the post request and add it to the queue
                StringRequest reviewRequest = createPostRequest(userReviewText, rating, userName, subjectId, authToken, routeParam);
                queue.add(reviewRequest);
                finish();
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
