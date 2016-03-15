package com.test.game.airhockey1;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.test.game.util.ShaderHelper;
import com.test.game.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
//import static android.opengl.GLES20.glClear;
//import static android.opengl.GLES20.glClearColor;
//import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.*;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private int program;
    private final Context context;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    float[] tableVertices;
    private FloatBuffer vertexData;

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String TAG = "Testing: ";



    public AirHockeyRenderer(Context context) {

        this.context = context;
        float[] tableVertices = {
                // Triangle 1
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                // Triangle 1
                -0.5f, -0.01f,
                0.5f, 0.01f,
                -0.5f, 0.01f,
                // Triangle 2
                -0.5f, -0.01f,
                0.5f, -0.01f,
                0.5f, 0.01f,
//                // Line 1
//                -0.5f, 0f,
//                0.5f, 0f,
                // Mallets
//                0f, -0.25f,
//                0f, 0.25f
        };

        //    private FloatBuffer vertexData;
        vertexData = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        ShaderHelper.validateProgram(program);
        glUseProgram(program);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 6, 6);
        // Draw the first mallet blue.
//        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
//        glDrawArrays(GL_POINTS, 8, 1);
// Draw the second mallet red.
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//        glDrawArrays(GL_POINTS, 9, 1);

    }
}
