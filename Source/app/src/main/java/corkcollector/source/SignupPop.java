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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bailey on 3/3/2018.
 */

public class SignupPop extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        Button submit = (Button) findViewById(R.id.signUpButton);

        final EditText nameEditText = findViewById(R.id.nameEntryBox);
        final EditText usernameEditText = findViewById(R.id.usernameEntryBox);
        final EditText emailEditText = findViewById(R.id.emailEntryBox);
        final EditText passwordEditText = findViewById(R.id.firstPasswordEntryBox);
        final EditText passwordEditText2 = findViewById(R.id.reenterPasswordEntryBox);

        //Instantiate the request queue
        final RequestQueue queue = Volley.newRequestQueue(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = nameEditText.getText().toString();
                final String username = usernameEditText.getText().toString();
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String password2 = passwordEditText2.getText().toString();

                if(password.length() < 6){

                    Context context = getApplicationContext();
                    CharSequence text = "Password must be 6 characters or more";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                else if(!password.equals(password2)){

                    Context context = getApplicationContext();
                    CharSequence text = "Your passwords do not match";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                else{

                    String url = "http://35.183.3.83/api/User/Register";

                    StringRequest registerPostRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "You are now a member!";
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

                                    Context context = getApplicationContext();
                                    CharSequence text = "Error: Username already exists";
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
                            params.put("userName", username);
                            params.put("password", password);
                            params.put("confirmPassword", password2);
                            params.put("name", name);
                            params.put("email", email);
                            return params;
                        }

                    };

                    queue.add(registerPostRequest);
                }
            }
        });
    }
}
