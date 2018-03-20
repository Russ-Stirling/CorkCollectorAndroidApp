package corkcollector.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 3/3/2018.
 */

public class NotesPop extends Activity {

    Bundle extras;
    String wineName;
    String wineNotes;
    String userName;
    String authToken;
    String userId;
    String wineId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        //Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        final TextView wineTitleTextView = findViewById(R.id.wineTitleTextView);
        final EditText notesEditText = findViewById(R.id.notesEditText);
        final Button editButton = findViewById(R.id.editButton);
        final Button submitButton = findViewById(R.id.submitButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        extras = getIntent().getExtras();
        wineName = extras.getString("wineName");
        wineNotes = extras.getString("wineNotes");
        userName = extras.getString("USER_NAME");
        authToken = extras.getString("AUTH_TOKEN");
        userId = extras.getString("userId");
        wineId = extras.getString("wineId");

        try{
            wineTitleTextView.setText(wineName);
            notesEditText.setText(wineNotes);
        }
        catch(Exception e){
            wineTitleTextView.setText("Title Error!");
            notesEditText.setText("Notes Error!");
        }

        notesEditText.setEnabled(false);
        notesEditText.setTextColor(Color.BLACK);
        submitButton.setEnabled(false);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notesEditText.setEnabled(true);
                submitButton.setEnabled(true);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String notesText = notesEditText.getText().toString();

                String url = "http://35.183.3.83/api/cellar/comment";

                StringRequest notesPutRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                Context context = getApplicationContext();
                                CharSequence text = "Notes Updated!";
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
                                CharSequence text = "Error: Could not edit your notes";
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
                        params.put("Authorization", "Bearer "+ authToken);
                        params.put("newComment", notesText);
                        params.put("wineId", wineId);
                        params.put("UserId", userId);
                        return params;
                    }

                };

                queue.add(notesPutRequest);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
}
