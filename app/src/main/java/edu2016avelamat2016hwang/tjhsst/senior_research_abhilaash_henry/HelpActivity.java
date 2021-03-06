package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.logging.Handler;

/**
 * Created by henrywang on 11/4/15.
 */
public class HelpActivity extends ActionBarActivity{
    Vibrator vibrator;
    private Button normalVibration;
    private Button softVibration;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(1000);//1000 milliseconds or 1 second vibration


        normalVibration = (Button) findViewById(R.id.button1);
        softVibration = (Button) findViewById(R.id.button2);

        normalVibration.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancel();
//                long[] pattern = {0, //start immediately
//                        1000, //20 ms
//                        0}; //0 ms pause
//                vibrator.vibrate(pattern, -1);
                vibrator.vibrate(1000);
            }
        });

        softVibration.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancel();
//                int x = 1000;
//                long[] pattern;
//                //long length = (long)1000/( (long)(250)/x + (long)(25)/x);//
//                pattern = new long[x+1];
//                //Log.i("Length", ""+length );
//                //pattern = new long[(int)length-1];
//                for(int y = 1; y < x; y+=2)
//                {
//                    pattern[y] = (long) 2500 / x;
//                    pattern[y + 1] = (long) 250 / x;
//                }
//                //Log.i("Pattern",pattern.toString());
//                vibrator.vibrate(pattern, -1);
                long[] pattern = genVibratorPattern((float)0.50, 1000);
                vibrator.vibrate(pattern, -1);
            }
        });
    }
    //Create pattern method
    public long[] createPattern(float intensity, long duration)
    {
        long[] pattern;
        int x = (int)duration;
        //long length = duration/((long)250/x + (long)25/x);
        pattern = new long[x+1];
        for(int y = 1; y < x; y+= 2)
        {
            pattern[y] = (long)2500/x;
            pattern[y+1] = (long)250/x;
        }
        return pattern;
    }

    //http://stackoverflow.com/questions/20808479/algorithm-for-generating-vibration-patterns-ranging-in-intensity-in-android/20821575#20821575
   public long[] genVibratorPattern( float intensity, long duration )
    {
        duration = duration/2;
        float dutyCycle = Math.abs( ( intensity * 2.0f ) - 1.0f );//[0,1]
        long hWidth = (long) ( dutyCycle * ( duration - 1 ) ) + 1;
        long lWidth = (dutyCycle == 1.0f) ? 0 : 1;

        int pulseCount = (int) ( 2.0f * ( (float) duration / (float) ( hWidth + lWidth ) ) );
        long[] pattern = new long[ pulseCount ];

        for( int i = 0; i < pulseCount; i++ )
        {
            pattern[i] = intensity < 0.5f ? ( i % 2 == 0 ? hWidth : lWidth ) : ( i % 2 == 0 ? lWidth : hWidth );
        }

        return pattern;
    }
}
