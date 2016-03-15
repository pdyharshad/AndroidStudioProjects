package com.test.game.objects;

import com.test.game.data.VertexArray;
import com.test.game.programs.ColorShaderProgram;
import com.test.game.util.Geometry;

import java.util.List;

/**
 * Created by User on 15-01-2016.
 */
public class Heart {
    public float width;
//    public float height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    public Heart( float width){
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createHeart(width, new Geometry.Point(0f,
                0f, 0f), 30);
//        Assuming the ratio of width to height of 1.25
        this.width = width;
//        this.height = (float) 1.25 * width;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
    }
    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
