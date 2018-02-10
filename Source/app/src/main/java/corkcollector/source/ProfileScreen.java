package corkcollector.source;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ProfileScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        Button tastedWines = (Button) findViewById(R.id.viewTastedWinesButton);

        tastedWines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileScreen.this, TastedWinesPop.class));
            }
        });

        Button visitedWineries = (Button) findViewById(R.id.viewVisitedWineriesButton);

        visitedWineries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileScreen.this, VisitedWineriesPop.class));
            }
        });

        Button wineCellar = (Button) findViewById(R.id.viewWineCellarButton);

        wineCellar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileScreen.this, WineCellarPop.class));
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
                Intent myIntent = new Intent(ProfileScreen.this,
                        MapsScreen.class);
                startActivity(myIntent);
                break;
            case R.id.item2:
                Intent myIntent2 = new Intent(ProfileScreen.this,
                        WineryScreen.class);
                startActivity(myIntent2);
                break;
            case R.id.item3:
                Intent myIntent3 = new Intent(ProfileScreen.this,
                        WineScreen.class);
                startActivity(myIntent3);
                break;
            case R.id.item4:
                Intent myIntent4 = new Intent(ProfileScreen.this,
                        ProfileScreen.class);
                startActivity(myIntent4);
                break;
            case R.id.item6:
                Intent myIntent6 = new Intent(ProfileScreen.this,
                        RequestScreen.class);
                startActivity(myIntent6);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
