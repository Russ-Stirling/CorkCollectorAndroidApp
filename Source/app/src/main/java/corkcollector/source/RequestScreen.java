package corkcollector.source;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
        RequestQueue queue = Volley.newRequestQueue(this);

        //Grab buttons from request screen
        Button getButton = findViewById(R.id.getButton);
        Button putButton = findViewById(R.id.putButton);
        Button postButton = findViewById(R.id.postButton);

        //Set listeners to send http requests when pressed
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Send get request
                String url ="http://www.google.com";

            }
        });

        putButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Send put request

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Send post request

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