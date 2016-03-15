package com.test.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.test.game.R;
import com.test.game.objects.Heart;
import com.test.game.objects.Mallet;
import com.test.game.objects.Puck;
import com.test.game.objects.Table;
import com.test.game.util.Geometry;
import com.test.game.util.MatrixHelper;
import com.test.game.util.TextureHelper;
import com.test.game.programs.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
//import static android.opengl.GLES20.glClear;
//import static android.opengl.GLES20.glClearColor;
//import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.*;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];

    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;
    private Heart heart;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;


    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    private Geometry.Point previousBlueMalletPosition;

    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;

    private Position[] positions = new Position[5];
    private int positonPointer;
    private long frameStartTimeMs;
//    private int postitonPointer;

    private static final String TAG = "AirHockeyRenderer";

    public int width;
    public int height;
    private WindowManager mWinMgr;

    public AirHockeyRenderer(Context context) {
        mWinMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        DisplayMetrics size = new DisplayMetrics();
        mWinMgr.getDefaultDisplay().getMetrics(size);
        this.width = size.widthPixels;
        this.height = size.heightPixels;

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        heart = new Heart(0.2f);
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        puck = new Puck(0.06f, 0.02f, 32);
        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.heart_beat);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
        setLookAtM(viewMatrix, 0, 0f, 0f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    private void limitFrameRate(int framesPerSecond) {
        Log.w(TAG, "frameStartTimeMs"+ frameStartTimeMs+ "\n");
        long elapsedFrameTimeMs = SystemClock.elapsedRealtime() - frameStartTimeMs;
        long expectedFrameTimeMs = 1000 / framesPerSecond;
        long timeToSleepMs = expectedFrameTimeMs - elapsedFrameTimeMs;
        if (timeToSleepMs > 0) {
            SystemClock.sleep(timeToSleepMs);
        }
        frameStartTimeMs = SystemClock.elapsedRealtime();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        limitFrameRate(2);
// Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
// Draw the table.
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        heart.bindData(colorProgram);
//        Draw Heart
        for(Position pos: positions) {

            if (pos == null){
                continue;
            }
            if (SystemClock.elapsedRealtime() - pos.posStartTime >= 15000){
                pos = null;
                continue;
            }
            positionObjectInScene(pos.normalizedX, pos.normalizedY, pos.normalizedZ);
            pos.setZ();

            colorProgram.setUniforms(modelViewProjectionMatrix);

            heart.draw();
        }
//// Draw the mallets.
//        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
//        colorProgram.useProgram();
//        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
//        mallet.bindData(colorProgram);
//        mallet.draw();
//
//        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y,
//                blueMalletPosition.z);
//        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
//// Note that we don't have to define the object data twice -- we just
//// draw the same mallet again but in a different position and with a
//// different color.
//        mallet.draw();
//// Draw the puck.
//        if (puckPosition.x < leftBound + puck.radius
//                || puckPosition.x > rightBound - puck.radius) {
//            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
//            puckVector = puckVector.scale(0.9f);
//        }
//        if (puckPosition.z < farBound + puck.radius
//                || puckPosition.z > nearBound - puck.radius) {
//            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
//            puckVector = puckVector.scale(0.9f);
//        }
//// Clamp the puck position.
//        puckPosition = new Geometry.Point(
//                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
//                puckPosition.y,
//                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
//        );
//        puckVector = puckVector.scale(0.99f);
//        puckPosition = puckPosition.translate(puckVector);
//        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
//        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
//        puck.bindData(colorProgram);
//        puck.draw();
    }


    private void positionTableInScene() {
// The table is defined in terms of X & Y coordinates, so we rotate it
// 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, 0, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
        Log.w(TAG, "In Position object.\n" +x + "\t"+y+"\t"+z + "\n");
    }



    public void handleTouchPress(float normalizedX, float normalizedY) {
        if (positonPointer < 3) {
            positions[positonPointer] = new Position(normalizedX, normalizedY, 0.5f);
            Log.w(TAG, "B4 Converting.\n" + positions[positonPointer].normalizedX + "\t" + positions[positonPointer].normalizedY + "\n");
            positions[positonPointer].convertto3d(invertedViewProjectionMatrix);
            positonPointer++;
            if (positonPointer >2){
                positonPointer = 0;
            }
        }
        for (Position pos : positions) {
            if (pos == null){break;}
            Log.w(TAG, "Result of Pressing in position.\n" + pos.normalizedX + "\t" + pos.normalizedY + "\n");
        }
//        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
//// Now test if this ray intersects with the mallet by creating a
//// bounding sphere that wraps the mallet.
//        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
//                blueMalletPosition.x,
//                blueMalletPosition.y,
//                blueMalletPosition.z),
//                mallet.height / 2f);
//// If the ray intersects (if the user touched a part of the screen that
//// intersects the mallet's bounding sphere), then set malletPressed =
//// true.
//        malletPressed = Geometry.intersects(malletBoundingSphere, ray);

    }
    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
// Define a plane representing our air hockey table.
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
// Find out where the touched point intersects the plane
// representing our table. We'll move the mallet along this plane.
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
//            blueMalletPosition =
//                    new Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z);
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(
                    clamp(touchedPoint.x,
                            leftBound + mallet.radius,
                            rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,
                            0f + mallet.radius,
                            nearBound - mallet.radius));
            float distance =
                    Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
// The mallet has struck the puck. Now send the puck flying
// based on the mallet velocity.
                puckVector = Geometry.vectorBetween(
                        previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
        }

    private Geometry.Ray convertNormalized2DPointToRay(
            float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
//        Inverting Perspective divide
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay =
        new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay =
        new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Geometry.Ray(nearPointRay,
        Geometry.vectorBetween(nearPointRay, farPointRay));
        }
}

class Position{
    float normalizedX;
    float normalizedY;
    float normalizedZ;
    long posStartTime;
    public Position(float normalizedX, float normalizedY , float normalizedZ){
        this.normalizedX = normalizedX;
        this.normalizedY = normalizedY;
        this.normalizedZ = normalizedZ;
        this.posStartTime = SystemClock.elapsedRealtime();
    }

    public  void convertto3d(float[] invertedViewProjectionMatrix){
        final float[] touchPoint = {this.normalizedX, this.normalizedY, this.normalizedZ, 1};
        final float[] point3d = new float[4];
        multiplyMV(
                point3d, 0, invertedViewProjectionMatrix, 0, touchPoint, 0);

        this.normalizedX = point3d[0] / point3d[3];
        this.normalizedY = point3d[1] / point3d[3];
        this.normalizedZ = point3d[2] / point3d[3];

    }

    public void setZ(){
        if (normalizedZ == 0f){
            normalizedZ = 0.5f;
        }
        else{
            normalizedZ = 0f;
        }
    }
}
