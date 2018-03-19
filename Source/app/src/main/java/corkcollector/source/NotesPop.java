package corkcollector.source;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

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

                //TODO: put request

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
