package corkcollector.source;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 2018-01-29.
 * Test class to send HTTP Get, Put and Post Requests
 */

public class RequestScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_screen);

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Grab buttons from request screen
        Button getButton = findViewById(R.id.getButton);
        Button putButton = findViewById(R.id.putButton);
        Button postButton = findViewById(R.id.postButton);

        //Set listeners to send http requests when pressed
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Send get request to sample website
                String url = "http://httpbin.org/get?param1=hello";

                //Prepare the get Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {

                                //Print response in log if successful
                                Log.d("Response", response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                //Print "oh no!" in log if unsuccessful
                                Log.d("Error.Response", "oh no!");
                            }
                        }
                );

                //Add it to the RequestQueue and send automatically
                queue.add(getRequest);

            }
        });

        putButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Send put request to sample website
                String url = "http://httpbin.org/put";

                //Prepare the put request
                StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                                //Print response in log if successful
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                //Print "oh no!" in log if unsuccessful
                                Log.d("Error.Response", "oh no!");
                            }
                        }
                ) {

                    //Create sample data for the put request
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("name", "Alif");
                        params.put("domain", "http://itsalif.info");

                        return params;
                    }

                };

                //Add it to the request queue and send automatically
                queue.add(putRequest);

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Send post request to sample website
                String url = "http://httpbin.org/post";

                //Prepare the post request
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                                //Print response in log if successful
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                //Print "oh no!" in log if unsuccessful
                                Log.d("Error.Response", "oh no!");
                            }
                        }
                ) {

                    //Create sample data for the post request
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("name", "Alif");
                        params.put("domain", "http://itsalif.info");

                        return params;
                    }
                };

                //Add it to the request queue and send automatically
                queue.add(postRequest);

            }
        });
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
                Intent myIntent = new Intent(RequestScreen.this,
                        MapsScreen.class);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent2 = new Intent(RequestScreen.this,
                        WineryScreen.class);
                startActivity(myIntent2);
                break;
            case R.id.item3:
                Intent myIntent3 = new Intent(RequestScreen.this,
                        WineScreen.class);
                startActivity(myIntent3);
                break;
            case R.id.item6:
                Intent myIntent6 = new Intent(RequestScreen.this,
                        RequestScreen.class);
                startActivity(myIntent6);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}