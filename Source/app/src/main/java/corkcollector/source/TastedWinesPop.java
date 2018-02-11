package corkcollector.source;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

/**
 * Created by Bailey on 2/10/2018.
 */

public class TastedWinesPop extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tasted_wines_popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));
    }
}
