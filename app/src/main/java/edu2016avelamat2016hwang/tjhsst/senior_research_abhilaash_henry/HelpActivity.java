package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;

import java.util.logging.Handler;

/**
 * Created by henrywang on 11/4/15.
 */
public class HelpActivity extends ActionBarActivity{
    Vibrator vibrator;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);//1000 milliseconds or 1 second vibration
    }
}
