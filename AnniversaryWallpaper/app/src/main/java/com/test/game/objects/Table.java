package com.test.game.objects;

import com.test.game.data.VertexArray;
import com.test.game.programs.*;
import static android.opengl.GLES20.*;
import com.test.game.Constants;
/**
 * Created by User on 22-12-2015.
 */
public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
// Order of coordinates: X, Y, S, T
// Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -1f, -1f, 0f, 1f,
            1f, -1f, 1f, 1f,
            1f, 1f, 1f, 0f,
            -1f, 1f, 0f, 0f,
            -1f, -1f, 0f, 1f };
    private final VertexArray vertexArray;
    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }


    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }


    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
