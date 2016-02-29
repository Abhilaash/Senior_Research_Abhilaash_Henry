package edu2016avelamat2016hwang.tjhsst.senior_research_abhilaash_henry;

/**
 * From Google Sample Code 2/22/2016.
 */
import android.opengl.GLES20;

/**
 * Static functions used by Renderer classes in Tango Java samples.
 */
public class RenderUtils {

    /**
     * Creates a vertex or fragment shader.
     *
     * @param type
     *            one of GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode
     *            GLSL code for the shader as a String
     * @return a compiled shader.
     */
    public static int loadShader(int type, String shaderCode) {
        // Create a shader of the correct type
        int shader = GLES20.glCreateShader(type);

        // Compile the shader from source code
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
