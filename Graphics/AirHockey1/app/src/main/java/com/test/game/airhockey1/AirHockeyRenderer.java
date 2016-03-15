package com.test.game.airhockey1;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
//import static java.nio.ByteBuffer.*;

/**
 * Created by User on 24-10-2015.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    static float[] tableVertices;
    private FloatBuffer vertexData;
    ByteBuffer bb = ByteBuffer.allocateDirect(
            // (number of coordinate values * 4 bytes per float)
            tableVertices.length * BYTES_PER_FLOAT);
    bb.order
//    bb.order(ByteOrder.nativeOrder());
//    bb
//    vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
//            .order(ByteOrder.nativeOrder())
//            .asFloatBuffer();
    public AirHockeyRenderer() {
        float[] tableVertices = {
                // Triangle 1
                0f, 0f,
                9f, 14f,
                0f, 14f,
                // Triangle 2
                0f, 0f,
                9f, 0f,
                9f, 14f,
                // Line 1
                0f, 7f,
                9f, 7f,
                // Mallets
                4.5f, 2f,
                4.5f, 12f
        };
    }
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
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
    }
}
