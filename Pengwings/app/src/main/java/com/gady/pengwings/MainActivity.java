package com.gady.pengwings;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.graphics.Movie;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    Intent chatHeadService;
    TextView textViewInfo;
    GifView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatHeadService = new Intent(this, ChatHeadService.class);
        startService(chatHeadService);

        gifView = (GifView)findViewById(R.id.gifview);

     //remove
        textViewInfo = (TextView)findViewById(R.id.textinfo);

        String stringInfo = "";
        stringInfo += "Duration: " + gifView.getMovieDuration() + "\n";
        stringInfo += "W x H: "
                + gifView.getMovieWidth() + " x "
                + gifView.getMovieHeight() + "\n";

        textViewInfo.setText(stringInfo);
     //end of remove

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        System.out.println("Back Clicked");
        stopService(chatHeadService);
    }
}
