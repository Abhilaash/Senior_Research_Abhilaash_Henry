package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.atap.tangoservice.Tango;

public class MainActivity extends ActionBarActivity {
    private Button bStart;
    private Button bHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityForResult(
                Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_MOTION_TRACKING),
                Tango.TANGO_INTENT_ACTIVITYCODE);
        startActivityForResult(
                Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_ADF_LOAD_SAVE),
                Tango.TANGO_INTENT_ACTIVITYCODE);

        bStart = (Button) findViewById(R.id.buttonStart);
        bHelp = (Button) findViewById(R.id.buttonHelp);
        bStart.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view){
                        Intent intent = new Intent(getApplicationContext(), PointCloudActivity.class);
                        startActivity(intent);
                    }
                }
        );

        bHelp.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view){
                        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity_main in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
