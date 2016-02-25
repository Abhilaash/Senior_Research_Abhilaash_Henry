package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Abhilaash on 2/22/2016.
 */
public class CameraFrustum extends Renderable {

    private static final int COORDS_PER_VERTEX = 3;

    private static final String sVertexShaderCode = "uniform mat4 uMVPMatrix;"
            + "attribute vec4 vPosition;" + "attribute vec4 aColor;"
            + "varying vec4 vColor;" + "void main() {" + "  vColor=aColor;"
            + "gl_Position = uMVPMatrix * vPosition;" + "}";

    private static final String sFragmentShaderCode = "precision mediump float;"
            + "varying vec4 vColor;"
            + "void main() {"
            + "gl_FragColor = vec4(0.8,0.5,0.8,1);" + "}";

    private FloatBuffer mVertexBuffer, mColorBuffer;

    private float mVertices[] = { 0.0f, 0.0f, 0.0f, -0.4f, 0.3f, -0.5f,

            0.0f, 0.0f, 0.0f, 0.4f, 0.3f, -0.5f,

            0.0f, 0.0f, 0.0f, -0.4f, -0.3f, -0.5f,

            0.0f, 0.0f, 0.0f, 0.4f, -0.3f, -0.5f,

            -0.4f, 0.3f, -0.5f, 0.4f, 0.3f, -0.5f,

            0.4f, 0.3f, -0.5f, 0.4f, -0.3f, -0.5f,

            0.4f, -0.3f, -0.5f, -0.4f, -0.3f, -0.5f,

            -0.4f, -0.3f, -0.5f, -0.4f, 0.3f, -0.5f };

    private float mColors[] = { 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,

            1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,

            1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f };

    private final int mProgram;
    private int mPosHandle, mColorHandle;
    private int mMVPMatrixHandle;

    public CameraFrustum() {
        // Reset the model matrix to the identity
        Matrix.setIdentityM(getModelMatrix(), 0);

        // Load the vertices into a vertex buffer
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(mVertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        // Load the colors into a color buffer
        ByteBuffer cByteBuff = ByteBuffer.allocateDirect(mColors.length * 4);
        cByteBuff.order(ByteOrder.nativeOrder());
        mColorBuffer = cByteBuff.asFloatBuffer();
        mColorBuffer.put(mColors);
        mColorBuffer.position(0);

        // Load the vertex and fragment shaders, then link the program
        int vertexShader = RenderUtils.loadShader(GLES20.GL_VERTEX_SHADER,
                sVertexShaderCode);
        int fragShader = RenderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
                sFragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragShader);
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        GLES20.glUseProgram(mProgram);
        // updateViewMatrix(viewMatrix);

        // Compose the model, view, and projection matrices into a single mvp
        // matrix
        updateMvpMatrix(viewMatrix, projectionMatrix);

        // Load vertex attribute data
        mPosHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glVertexAttribPointer(mPosHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPosHandle);

        // Load color attribute data
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                0, mColorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Draw the CameraFrustum
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, getMvpMatrix(), 0);
        GLES20.glLineWidth(1);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 16);
    }
}
