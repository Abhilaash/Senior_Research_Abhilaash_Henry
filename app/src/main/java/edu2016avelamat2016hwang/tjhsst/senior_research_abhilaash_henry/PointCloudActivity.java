package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tango.ux.TangoUxLayout;
import com.google.atap.tango.ux.UxExceptionEvent;
import com.google.atap.tango.ux.UxExceptionEventListener;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Abhilaash on 2/22/2016.
 */
public class PointCloudActivity extends Activity implements View.OnClickListener {

    private static final String TAG = PointCloudActivity.class.getSimpleName();
    private static final int SECS_TO_MILLISECS = 1000;
    private Tango mTango;
    private TangoConfig mConfig;

    private PCRenderer mRenderer;
    private GLSurfaceView mGLView;

    private TextView mDeltaTextView;
    private TextView mPoseCountTextView;
    private TextView mPoseTextView;
    private TextView mQuatTextView;
    private TextView mPoseStatusTextView;
    private TextView mTangoEventTextView;
    private TextView mPointCountTextView;
    private TextView mTangoServiceVersionTextView;
    private TextView mApplicationVersionTextView;
    private TextView mAverageZTextView;
    private TextView mFrequencyTextView;

    private Button mFirstPersonButton;
    private Button mThirdPersonButton;
    private Button mTopDownButton;

    private int count;
    private int mPreviousPoseStatus;
    private int mPointCount;
    private float mDeltaTime;
    private float mPosePreviousTimeStamp;
    private float mXyIjPreviousTimeStamp;
    private float mCurrentTimeStamp;
    private float mPointCloudFrameDelta;
    private boolean mIsTangoServiceConnected;
    private TangoPoseData mPose;
    private Vibrator vibrator;
    private TangoUx mTangoUx;
    private TangoUxLayout mTangoUxLayout;
    private int vibratetime = 0;
    private float countTime = 0;
    private static final int UPDATE_INTERVAL_MS = 100;
    public static Object poseLock = new Object();
    public static Object depthLock = new Object();


    /*
     * This is an advanced way of using UX exceptions. In most cases developers can just use the in
     * built exception notifications using the Ux Exception layout. In case a developer doesn't want
     * to use the default Ux Exception notifications, he can set the UxException listener as shown
     * below.
     * In this example we are just logging all the ux exceptions to logcat, but in a real app,
     * developers should use these exceptions to contextually notify the user and help direct the
     * user in using the device in a way Tango service expects it.
     */
    private UxExceptionEventListener mUxExceptionListener = new UxExceptionEventListener() {

        @Override
        public void onUxExceptionEvent(UxExceptionEvent uxExceptionEvent) {
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_LYING_ON_SURFACE){
                Log.i(TAG, "Device lying on surface ");
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_FEW_DEPTH_POINTS){
                Log.i(TAG, "Very few depth points in point cloud " );
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_FEW_FEATURES){
                Log.i(TAG, "Invalid poses in MotionTracking ");
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_INCOMPATIBLE_VM){
                Log.i(TAG, "Device not running on ART");
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_MOTION_TRACK_INVALID){
                Log.i(TAG, "Invalid poses in MotionTracking ");
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_MOVING_TOO_FAST){
                Log.i(TAG, "Invalid poses in MotionTracking ");
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_OVER_EXPOSED){
                Log.i(TAG, "Camera Over Exposed");
            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_TANGO_SERVICE_NOT_RESPONDING){
                Log.i(TAG, "TangoService is not responding ");
            }
//            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_TANGO_UPDATE_NEEDED){
//                Log.i(TAG, "Device not running on ART");
//            }
            if(uxExceptionEvent.getType() == UxExceptionEvent.TYPE_UNDER_EXPOSED){
                Log.i(TAG, "Camera Under Exposed " );
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_cloud);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mPoseTextView = (TextView) findViewById(R.id.pose);
        mQuatTextView = (TextView) findViewById(R.id.quat);
        mPoseCountTextView = (TextView) findViewById(R.id.posecount);
        mDeltaTextView = (TextView) findViewById(R.id.deltatime);
        mTangoEventTextView = (TextView) findViewById(R.id.tangoevent);
        mPoseStatusTextView = (TextView) findViewById(R.id.status);
        mPointCountTextView = (TextView) findViewById(R.id.pointCount);
        mAverageZTextView = (TextView) findViewById(R.id.averageZ);
        mFrequencyTextView = (TextView) findViewById(R.id.frameDelta);

        mFirstPersonButton = (Button) findViewById(R.id.first_person_button);
        mFirstPersonButton.setOnClickListener(this);
        mThirdPersonButton = (Button) findViewById(R.id.third_person_button);
        mThirdPersonButton.setOnClickListener(this);
        mTopDownButton = (Button) findViewById(R.id.top_down_button);
        mTopDownButton.setOnClickListener(this);
        mTango = new Tango(this);
        mConfig = new TangoConfig();
        mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);

        mTangoUxLayout = (TangoUxLayout) findViewById(R.id.layout_tango);
        mTangoUx = new TangoUx(this);
        mTangoUx.setLayout(mTangoUxLayout);

        mTangoUx.setUxExceptionEventListener(mUxExceptionListener);


        int maxDepthPoints = mConfig.getInt("max_point_cloud_elements");
        mRenderer = new PCRenderer(maxDepthPoints);
        mGLView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(mRenderer);

        mIsTangoServiceConnected = false;
        startUIThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vibrator.cancel();
        mTangoUx.stop();
        mTango.disconnect();
        mIsTangoServiceConnected = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TangoUx.StartParams params = new TangoUx.StartParams();
        mTangoUx.start(params);
        if (!mIsTangoServiceConnected) {
            startActivityForResult(
                    Tango.getRequestPermissionIntent(Tango.PERMISSIONTYPE_MOTION_TRACKING),
                    Tango.TANGO_INTENT_ACTIVITYCODE);
        }
        Log.i(TAG, "onResumed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Tango.TANGO_INTENT_ACTIVITYCODE) {
            Log.i(TAG, "Triggered");
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.motiontrackingpermission, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            try {
                setTangoListeners();
            } catch (TangoErrorException e) {
                Toast.makeText(this, R.string.TangoError, Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), R.string.motiontrackingpermission,
                        Toast.LENGTH_SHORT).show();
            }
            try {
                mTango.connect(mConfig);
                mIsTangoServiceConnected = true;
            } catch (TangoOutOfDateException outDateEx) {
                if (mTangoUx != null) {
                    mTangoUx.showTangoOutOfDate();
                }
            } catch (TangoErrorException e) {
                Toast.makeText(getApplicationContext(), R.string.TangoError, Toast.LENGTH_SHORT)
                        .show();
            }
            setUpExtrinsics();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
        mTangoUx.stop();
        mTango.disconnect();
        mIsTangoServiceConnected = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_person_button:
                mRenderer.setFirstPersonView();
                break;
            case R.id.third_person_button:
                mRenderer.setThirdPersonView();
                break;
            case R.id.top_down_button:
                mRenderer.setTopDownView();
                break;
            default:
                Log.w(TAG, "Unrecognized button click.");
                return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mRenderer.onTouchEvent(event);
    }

    private void setUpExtrinsics() {
        // Set device to imu matrix in Model Matrix Calculator.
        TangoPoseData device2IMUPose = new TangoPoseData();
        TangoCoordinateFramePair framePair = new TangoCoordinateFramePair();
        framePair.baseFrame = TangoPoseData.COORDINATE_FRAME_IMU;
        framePair.targetFrame = TangoPoseData.COORDINATE_FRAME_DEVICE;
        try {
            device2IMUPose = mTango.getPoseAtTime(0.0, framePair);
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), R.string.TangoError, Toast.LENGTH_SHORT).show();
        }
        mRenderer.getModelMatCalculator().SetDevice2IMUMatrix(
                device2IMUPose.getTranslationAsFloats(), device2IMUPose.getRotationAsFloats());

        // Set color camera to imu matrix in Model Matrix Calculator.
        TangoPoseData color2IMUPose = new TangoPoseData();

        framePair.baseFrame = TangoPoseData.COORDINATE_FRAME_IMU;
        framePair.targetFrame = TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR;
        try {
            color2IMUPose = mTango.getPoseAtTime(0.0, framePair);
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), R.string.TangoError, Toast.LENGTH_SHORT).show();
        }
        mRenderer.getModelMatCalculator().SetColorCamera2IMUMatrix(
                color2IMUPose.getTranslationAsFloats(), color2IMUPose.getRotationAsFloats());
    }
    private void setTangoListeners() {
        // Configure the Tango coordinate frame pair
        final ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));
        // Listen for new Tango data
        mTango.connectListener(framePairs, new Tango.OnTangoUpdateListener() {

            @Override
            public void onPoseAvailable(final TangoPoseData pose) {
                // Passing in the pose data to UX library produce exceptions.
                if (mTangoUx != null) {
                    mTangoUx.updatePoseStatus(pose.statusCode);
                }
                // Make sure to have atomic access to Tango Pose Data so that
                // render loop doesn't interfere while Pose call back is updating
                // the data.
                synchronized (poseLock) {
                    mPose = pose;
                    // Calculate the delta time from previous pose.
                    mDeltaTime = (float) (pose.timestamp - mPosePreviousTimeStamp)
                            * SECS_TO_MILLISECS;
                    mPosePreviousTimeStamp = (float) pose.timestamp;
                    if (mPreviousPoseStatus != pose.statusCode) {
                        count = 0;
                    }
                    count++;
                    mPreviousPoseStatus = pose.statusCode;
                    if (!mRenderer.isValid()) {
                        return;
                    }
                    mRenderer.getModelMatCalculator().updateModelMatrix(
                            pose.getTranslationAsFloats(), pose.getRotationAsFloats());
                }
            }

            @Override
            public void onXyzIjAvailable(final TangoXyzIjData xyzIj) {
                if (mTangoUx != null) {
                    mTangoUx.updateXyzCount(xyzIj.xyzCount);
                }
                // Make sure to have atomic access to TangoXyzIjData so that
                // render loop doesn't interfere while onXYZijAvailable callback is updating
                // the point cloud data.
                synchronized (depthLock) {
                    mCurrentTimeStamp = (float) xyzIj.timestamp;
                    mPointCloudFrameDelta = (mCurrentTimeStamp - mXyIjPreviousTimeStamp)
                            * SECS_TO_MILLISECS;
                    mXyIjPreviousTimeStamp = mCurrentTimeStamp;
                    try {
                        TangoPoseData pointCloudPose = mTango.getPoseAtTime(mCurrentTimeStamp,
                                framePairs.get(0));

                        if (!mRenderer.isValid()) {
                            return;
                        }
                        mRenderer.getPointCloud().UpdatePoints(xyzIj.xyz);
                        mRenderer.getModelMatCalculator().updatePointCloudModelMatrix(
                                pointCloudPose.getTranslationAsFloats(),
                                pointCloudPose.getRotationAsFloats());
                        mRenderer.getPointCloud().setModelMatrix(
                                mRenderer.getModelMatCalculator().getPointCloudModelMatrixCopy());
                        mPointCount = xyzIj.xyzCount;

                    } catch (TangoErrorException e) {
                        Toast.makeText(getApplicationContext(), R.string.TangoError,
                                Toast.LENGTH_SHORT).show();
                    } catch (TangoInvalidException e) {
                        Toast.makeText(getApplicationContext(), R.string.TangoError,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onTangoEvent(final TangoEvent event) {
                if (mTangoUx != null) {
//                    mTangoUx.onTangoEvent(event);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTangoEventTextView.setText(event.eventKey + ": " + event.eventValue);
                    }
                });
            }

            @Override
            public void onFrameAvailable(int cameraId) {
                // We are not using onFrameAvailable for this application.
            }
        });
    }
    /**http://stackoverflow.com/questions/20808479/algorithm-for-generating-vibration-patterns-ranging-in-intensity-in-android/20821575#20821575
    *Vary vibration intensity; adapted from StackOverflow question. Intensity is from [0, 1.0]
    */
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
    /**
     * Create a separate thread to update Log information on UI at the specified interval of
     * UPDATE_INTERVAL_MS. This function also makes sure to have access to the mPose atomically.
     */
    private void startUIThread() {
        new Thread(new Runnable() {
            final DecimalFormat threeDec = new DecimalFormat("0.000");

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(UPDATE_INTERVAL_MS);
                        // Update the UI with TangoPose information
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (poseLock) {
                                    if (mPose == null) {
                                        return;
                                    }
                                   vibrator.cancel();
                                    float depth = mRenderer.getPointCloud().getAverageZ();
                                    vibratetime = mPointCount;
                                    float vibrateIntensity = 0;
                                    vibrator.cancel();
                                    if(depth < 4){
                                        vibrator.cancel();
                                        //vibratetime = 1000;
                                        vibrateIntensity = -1*(depth-2)/2;
                                        long[] pattern = genVibratorPattern((float) vibrateIntensity, 3000);
                                        //vibrator.vibrate(vibratetime);//1000 milliseconds or 1 second vibration
                                        vibrator.vibrate(pattern, -1);
                                    }

                                    String translationString = "["
                                            + threeDec.format(mPose.translation[0]) + ", "
                                            + threeDec.format(mPose.translation[1]) + ", "
                                            + threeDec.format(mPose.translation[2]) + "] ";
                                    String quaternionString = "["
                                            + threeDec.format(mPose.rotation[0]) + ", "
                                            + threeDec.format(mPose.rotation[1]) + ", "
                                            + threeDec.format(mPose.rotation[2]) + ", "
                                            + threeDec.format(mPose.rotation[3]) + "] ";

                                    // Display pose data on screen in TextViews
                                    mPoseTextView.setText(translationString);
                                    mQuatTextView.setText(quaternionString);
                                    mPoseCountTextView.setText(Integer.toString(count));
                                    mDeltaTextView.setText(threeDec.format(mDeltaTime));

                                    if (mPose.statusCode == TangoPoseData.POSE_VALID) {
                                        mPoseStatusTextView.setText(R.string.pose_valid);
                                    } else if (mPose.statusCode == TangoPoseData.POSE_INVALID) {
                                        mPoseStatusTextView.setText(R.string.pose_invalid);
                                    } else if (mPose.statusCode == TangoPoseData.POSE_INITIALIZING) {
                                        mPoseStatusTextView.setText(R.string.pose_initializing);
                                    } else if (mPose.statusCode == TangoPoseData.POSE_UNKNOWN) {
                                        mPoseStatusTextView.setText(R.string.pose_unknown);
                                    }
                                }
                                synchronized (depthLock) {
                                    // Display number of points in the point cloud
                                    mPointCountTextView.setText(Integer.toString(mPointCount));
                                    mFrequencyTextView.setText(""
                                            + threeDec.format(mPointCloudFrameDelta));
                                    mAverageZTextView.setText(""
                                            + threeDec.format(mRenderer.getPointCloud()
                                            .getAverageZ()));
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}