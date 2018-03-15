package corkcollector.source;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Bailey on 3/3/2018.
 */

public class NotesPop extends Activity {

    Bundle extras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        TextView wineTitleTextView = findViewById(R.id.wineTitleTextView);
        TextView notesTextView = findViewById(R.id.notesTextView);

        extras = getIntent().getExtras();
        String wineName = extras.getString("wineName");
        String wineNotes = extras.getString("wineNotes");

        try{
            wineTitleTextView.setText(wineName);
            notesTextView.setText(wineNotes);
        }
        catch(Exception e){
            notesTextView.setText("Title Error!");
            notesTextView.setText("Notes Error!");
        }



    }
}
