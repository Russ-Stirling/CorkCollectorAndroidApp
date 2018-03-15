package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 3/3/2018.
 */

public class WineCellarQuantityPop extends Activity {

    Bundle extras;
    String authToken;
    String userName;
    String userID;
    String wineID;

    int wineQty;
    String notesText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wine_cellar_quanitity_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        Button submit = (Button) findViewById(R.id.submitToCellarButton);

        final EditText wineQtyEditText = findViewById(R.id.wineQtyEditText);
        final EditText wineNotesEditText = findViewById(R.id.wineNotesEditText);

        //Get the extra values bundled with the screen change
        extras = getIntent().getExtras();
        authToken = extras.getString("AUTH_TOKEN");
        userName = extras.getString("USER_NAME");

        //Grab the wine ID
        wineID = extras.getString("wineID");

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wineQty = Integer.parseInt(wineQtyEditText.getText().toString());
                notesText = wineNotesEditText.getText().toString();

                String url = "http://35.183.3.83/api/User/Profile?username="+ userName;

                //This is called when the app's get request goes through
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    //Grab the userID
                                    userID = response.getString("userId");
                                    queue.add(addToCellar());

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
                        params.put("userId", userID);
                        params.put("wineId", wineID);

                        return params;
                    }
                };

                //Add it to the RequestQueue and send automatically
                queue.add(getRequest);

                finish();
            }
        });
    }

    StringRequest addToCellar()
    {
        String url = "http://35.183.3.83/api/Cellar/New";

        StringRequest tastePostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Context context = getApplicationContext();
                        CharSequence text = "Added to cellar!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        //finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Print "oh no!" in log if unsuccessful
                        Log.d("Error.Response", "oh no!");

                        Context context = getApplicationContext();
                        CharSequence text = "Error: Could not update cellar";
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
                params.put("wineId", wineID);
                params.put("userId", userID);
                params.put("quantity", Integer.toString(wineQty));
                params.put("notes", notesText);
                return params;
            }

        };

        return tastePostRequest;
    }
}
