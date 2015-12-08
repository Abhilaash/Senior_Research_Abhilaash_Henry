package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoErrorException;

/**
 * Created by henrywang on 11/4/15.
 */
    public class TangoActivity extends ActionBarActivity {
        private Tango mTango;
        private TangoConfig mConfig;

        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tango);
            mTango = new Tango(this);
            mConfig = new TangoConfig();
            mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_COLORMODEAUTO, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_LEARNINGMODE, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);
        }
    }
