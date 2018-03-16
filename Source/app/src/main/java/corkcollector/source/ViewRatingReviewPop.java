package corkcollector.source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by Bailey on 3/16/2018.
 */

public class ViewRatingReviewPop extends Activity {

    //Bundle containing authentication token from login screen
    Bundle extras;
    String authToken;
    String userName;
    String wineID;
    String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_rating_review_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .9));

        Button editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Hopefully this does what you want it to.. entrusting it to you Dan :D

                //I think here is where we will need to send it the info we already have in this rating/review.
                Intent myIntent = new Intent(ViewRatingReviewPop.this,
                        RateReviewPop.class);
                //Send over the winery ID
                myIntent.putExtra("subjectID", wineID);
                myIntent.putExtra("AUTH_TOKEN", authToken);
                myIntent.putExtra("USER_NAME", userName);
                myIntent.putExtra("ROUTE_PARAM", "wine");

                startActivity(myIntent);
            }
        });
    }
}
