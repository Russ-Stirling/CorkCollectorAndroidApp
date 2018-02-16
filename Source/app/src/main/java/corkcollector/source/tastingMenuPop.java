package corkcollector.source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Bailey on 1/20/2018.
 */

public class tastingMenuPop extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tasting_menu_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        //If the array object has been passed

        //Populate the tasting menu list with the passed array

        //Create an object in the winery list
        TextView wineryAwineA = (TextView) findViewById(R.id.wineryAWineA);

        //Set an on-click listener for the object
        wineryAwineA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(tastingMenuPop.this, WineScreen.class));
                //Pass the wine ID to the wine screen
            }
        });
    }
}
