package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

/**
 * Created by Abhilaash on 2/22/2016.
 */
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL rendering class for the Motion Tracking API sample. This class managers the objects
 * visible in the OpenGL view which are the {@link CameraFrustum}, {@link PointCloud} and the
 * {@link Grid}. These objects are implemented in the TangoUtils library in the package
 * {@link com.projecttango.tangoutils.renderables}.
 *
 * This class receives {@link TangoPose} data from the {@link MotionTracking} class and updates the
 * model and view matrices of the {@link Renderable} objects appropriately. It also handles the
 * user-selected camera view, which can be 1st person, 3rd person, or top-down.
 *
 */
public class PCRenderer extends Renderer implements GLSurfaceView.Renderer {

    private PointCloud mPointCloud;
    private Grid mGrid;
    private CameraFrustumAndAxis mCameraFrustumAndAxis;
    private int mMaxDepthPoints;
    private boolean mIsValid = false;
    public PCRenderer(int maxDepthPoints) {
        mMaxDepthPoints = maxDepthPoints;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mPointCloud = new PointCloud(mMaxDepthPoints);
        mGrid = new Grid();
        mCameraFrustumAndAxis = new CameraFrustumAndAxis();
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setLookAtM(mViewMatrix, 0, 5f, 5f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);
        mCameraFrustumAndAxis.setModelMatrix(getModelMatCalculator().getModelMatrix());
        mIsValid = true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mCameraAspect = (float) width / height;
        Matrix.perspectiveM(mProjectionMatrix, 0, CAMERA_FOV, mCameraAspect, CAMERA_NEAR,
                CAMERA_FAR);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mGrid.draw(mViewMatrix, mProjectionMatrix);
        synchronized (PointCloudActivity.depthLock) {
            mPointCloud.draw(mViewMatrix, mProjectionMatrix);
        }
        synchronized (PointCloudActivity.poseLock) {
            mCameraFrustumAndAxis.draw(mViewMatrix, mProjectionMatrix);
        }
        updateViewMatrix();
    }

    public PointCloud getPointCloud() {
        return mPointCloud;
    }

    public boolean isValid(){
        return mIsValid;
    }
}

