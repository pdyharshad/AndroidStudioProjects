package com.test.game.programs;

import android.content.Context;

import com.test.game.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by User on 22-12-2015.
 */
public class ColorShaderProgram extends ShaderProgram{
    // Uniform locations
    private final int uMatrixLocation;
    private final int aColorLocation;
    // Attribute locations
    private final int aPositionLocation;
//    private final int aColorLocation;
    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader);
// Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
// Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
//        aColorLocation = glGetAttribLocation(program, A_COLOR);
    }
    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
//        glUniform4f(uColorLocation, r, g, b, 1f);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}

