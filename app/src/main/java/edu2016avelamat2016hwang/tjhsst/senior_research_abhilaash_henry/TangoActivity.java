package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tango.ux.TangoUxLayout;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoCameraPreview;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import android.opengl.GLES10;
import android.opengl.GLES20;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Abhilaash Velamati on 11/4/15.
 */
    public class TangoActivity extends ActionBarActivity {
        private Tango mTango;
        private TangoConfig mConfig;
        private TangoCameraPreview tangoCameraPreview;
        private TangoCameraIntrinsics tangoCameraIntrinsics;
        private TangoUx mTangoUx;
        private TangoUxLayout mTangoUxLayout;
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tango);

            mTango = new Tango(this);
            mConfig = new TangoConfig();
            mTangoUx = new TangoUx(this);
            mTangoUxLayout = (TangoUxLayout) findViewById(R.id.layout_tango);
            mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_COLORMODEAUTO, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_LEARNINGMODE, true);
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);
            mTangoUx.setLayout(mTangoUxLayout);
            tangoCameraPreview = new TangoCameraPreview(this);
            startCamera();
            setContentView(tangoCameraPreview);
//            tangoCameraPreview = new TangoCameraPreview(this);
//            tangoCameraIntrinsics = new TangoCameraIntrinsics();
//            conCamera(tangoCameraIntrinsics.TANGO_CAMERA_DEPTH);
        }

        private void startCamera(){
            tangoCameraPreview.connectToTangoCamera(mTango,
                    TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
            // Use default configuration for Tango Service.
            TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
            ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
            mTango.connectListener(framePairs, new Tango.OnTangoUpdateListener() {
                @Override
                public void onPoseAvailable(final TangoPoseData pose) {
                    if (mTangoUx != null) {
                        mTangoUx.updatePoseStatus(pose.statusCode);
                    }
                }

                @Override
                public void onFrameAvailable(int cameraId) {

                    // Check if the frame available is for the camera we want and
                    // update its frame on the camera preview.
                    if (cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
                        tangoCameraPreview.onFrameAvailable();
                    }
                }

                @Override
                public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
//                    byte[] buffer = new byte[xyzIj.xyzCount * 3 * 4];
//                    FileInputStream fileStream = new FileInputStream(
//                            xyzIj.xyzParcelFileDescriptor.getFileDescriptor());
//                    try {
//                        fileStream.read(buffer,
//                                xyzIj.xyzParcelFileDescriptorOffset, buffer.length);
//                        fileStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    // Do not process the buffer inside the callback because
                    // you will not receive any new data while it processes
                    if (mTangoUx != null) {
                        mTangoUx.updateXyzCount(xyzIj.xyzCount);
                    }
                }

                @Override
                public void onTangoEvent(TangoEvent event) {
                    // We are not using OnPoseAvailable for this app
                    if (mTangoUx != null) {
                        mTangoUx.updateTangoEvent(event);
                    }
                }
            });
            TangoUx.StartParams params = new TangoUx.StartParams();
            mTangoUx.start(params);
            try {
                mTango.connect(config);
            } catch (TangoOutOfDateException outDateEx) {
                if (mTangoUx != null) {
                    mTangoUx.showTangoOutOfDate();
                }
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            mTango.disconnect();
            mTangoUx.stop();
        }
    }
