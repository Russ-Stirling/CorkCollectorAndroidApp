package corkcollector.source;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class WineScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wine_screen);
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
                Intent myIntent = new Intent(WineScreen.this,
                        MapsScreen.class);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent2 = new Intent(WineScreen.this,
                        WineryScreen.class);
                startActivity(myIntent2);
                break;
            case R.id.item3:
                Intent myIntent3 = new Intent(WineScreen.this,
                        WineScreen.class);
                startActivity(myIntent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
