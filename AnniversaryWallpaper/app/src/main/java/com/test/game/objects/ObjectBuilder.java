package com.test.game.objects;



import com.test.game.util.Geometry;
import com.test.game.util.Geometry.*;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

/**
 * Created by User on 27-12-2015.
 */
public class ObjectBuilder {
    static interface DrawCommand {
        void draw();
    }
    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;
        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }

    }
    private static final int FLOATS_PER_VERTEX = 6;
    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;
    public ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];

    }


    private static int sizeOfCircleInVertices(int numPoints) {
        /**   A cylinder top is a circle built out of a triangle fan; it has one vertex in the
        center, one vertex for each point around the circle, and the first vertex around
        the circle is repeated twice so that we can close the circle off. **/

        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        /**
         A cylinder side is a rolled-up rectangle built out of a triangle strip, with two
         vertices for each point around the circle, and with the first two vertices
         repeated twice so that we can close off the tube.
         **/
        return (numPoints + 1) * 2;
    }

    private void appendCircle(Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
// Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
// Fan around center point. <= is used because we want to generate
// the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            vertexData[offset++] =
                    circle.center.x
                            + circle.radius * (float) Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    circle.center.z
                            + circle.radius * (float) Math.sin(angleInRadians);
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            float xPosition =
                    cylinder.center.x
                            + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition =
                    cylinder.center.z
                            + cylinder.radius * (float)  Math.sin(angleInRadians);
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }


    static GeneratedData createPuck(Cylinder puck, int numPoints) {
        int size = ObjectBuilder.sizeOfCircleInVertices(numPoints)
                + ObjectBuilder.sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        Circle puckTop = new Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);
        return builder.build();
    }

    static GeneratedData createMallet(
            Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
// First, generate the mallet base.
        float baseHeight = height * 0.25f;
        Circle baseCircle = new Circle(
                center.translateY(-baseHeight),
                radius);
        Cylinder baseCylinder = new Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        Circle handleCircle = new Circle(
                center.translateY(height * 0.5f),
                handleRadius);
        Cylinder handleCylinder = new Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

//    private static int sizeOfArcInVertices(int numPoints) {
//        /**   A cylinder top is a circle built out of a triangle fan; it has one vertex in the
//         center, one vertex for each point around the circle, and the first vertex around
//         the circle is repeated twice so that we can close the circle off. **/
//
//        return numPoints + 1;
//    }



    private void appendarc(Arc arc, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
//        final int numVertices = sizeOfArcInVertices(numPoints);
//        final float[] tempVertices = new float[numPoints * FLOATS_PER_VERTEX] ;
//        int tempOffset = 0;
// Center point of fan
//        vertexData[offset++] = arc.center.x;
//        vertexData[offset++] = arc.center.y;
//        vertexData[offset++] = arc.center.z;
// Fan around center point. <= is used because we want to generate
// the point at the starting angle twice to complete the fan.
        for (int i = 1; i <= numPoints; i++) {
            float angleInRadians =
                    (arc.fromAngle * ((float) Math.PI / 180f)) +
                            (((float) i / (float) numPoints)
                                * ((arc.angle * (float) Math.PI) / 180f));
            if (angleInRadians >= (2 * (float) Math.PI)) {
                angleInRadians = angleInRadians - (2 * (float) Math.PI) ;
                }
            vertexData[offset++] = arc.center.x + arc.radius * (float) Math.cos(angleInRadians);
            vertexData[offset++] = arc.center.y + arc.radius * (float) Math.sin(angleInRadians);
            vertexData[offset++] = arc.center.z;
            vertexData[offset++] = 1f;
            vertexData[offset++] = 0f;
            vertexData[offset++] = 0f;
        }
//        drawList.add(new DrawCommand() {
//            @Override
//            public void draw() {
//                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
//            }
//        });
    }
    private void appendHeart(ObjectBuilder builder, float heartWidth, Point center, int numPointsInArc, final int numVertices) {
        float heartHeight = heartWidth;
        vertexData[offset++] = center.x;
        vertexData[offset++] = center.y;
        vertexData[offset++] = center.z;
        vertexData[offset++] = 1f;
        vertexData[offset++] = 1f;
        vertexData[offset++] = 1f;

        vertexData[offset++] = center.x;
        vertexData[offset++] = center.y - heartHeight;
        vertexData[offset++] = center.z;
        vertexData[offset++] = 1f;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 0f;


        Arc rightArc = new Arc(center.translateX(heartWidth / (float)2), heartWidth / (float)2,(float) 220, (float) 320 );
//        Draw Right Arc
        builder.appendarc(rightArc,numPointsInArc);

        Arc leftArc = new Arc(center.translateX(- heartWidth / (float)2), heartWidth / (float)2,(float) 220, (float) 0 );
//        Draw Left Arc
        builder.appendarc(leftArc,numPointsInArc);

        vertexData[offset++] = center.x;
        vertexData[offset++] = center.y - heartHeight;
        vertexData[offset++] = center.z;
        vertexData[offset++] = 1f;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 0f;
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, 0, numVertices);
            }
        });


    }

    static GeneratedData createHeart(float heartWidth, Point center, int numPointsInArc) {
        final int numVertices = (numPointsInArc * 2) + 3;
        ObjectBuilder builder = new ObjectBuilder(numVertices);
        builder.appendHeart(builder, heartWidth ,center,numPointsInArc,numVertices);
        return builder.build();
        }

}
