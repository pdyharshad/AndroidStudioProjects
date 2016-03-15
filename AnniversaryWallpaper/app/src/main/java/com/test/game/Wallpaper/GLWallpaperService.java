package com.test.game.Wallpaper;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.test.game.AirHockeyRenderer;

/**
 * Created by User on 24-01-2016.
 */
public class GLWallpaperService extends WallpaperService {
    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new GLEngine();
    }
    public class GLEngine extends Engine {
        private WallpaperGLSurfaceView glSurfaceView;
        private boolean rendererSet;
        private static final String TAG = "GLEngine";
        private AirHockeyRenderer particlesRenderer;



        public  GLEngine(){
            setTouchEventsEnabled(true);
            particlesRenderer =
                    new AirHockeyRenderer(GLWallpaperService.this);
        }

        class WallpaperGLSurfaceView extends GLSurfaceView {
            WallpaperGLSurfaceView(Context context) {
                super(context);
            }
            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }
            public void onWallpaperDestroy() {
                super.onDetachedFromWindow();
            }
        }
        @Override
        public void onTouchEvent(MotionEvent event) {
            Log.w(TAG, "Touching wallpaper" + "\n");
//            if (action.equals(WallpaperManager.COMMAND_TAP)) {
                // do whatever you would have done on ACTION_UP
//                glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
                        if (event != null) {
// Convert touch coordinates into normalized device
// coordinates, keeping in mind that Android's Y
// coordinates are inverted.
                            Log.w(TAG, "Touch results\t"+ event.getX() +'\t'+ event.getY()+'\t'+glSurfaceView.getWidth()+
                                    '\t'+glSurfaceView.getHeight()+ "\n");
                            final float normalizedX =
                                    (event.getX() / (float) particlesRenderer.width) * 2 - 1;
                            final float normalizedY =
                                    -((event.getY() / (float) particlesRenderer.height) * 2 - 1);
                            Log.w(TAG, "Normalized Touch results\t"+ normalizedX +'\t'+ normalizedY+ "\n");
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                glSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        particlesRenderer.handleTouchPress(
                                                normalizedX, normalizedY);
                                    }
                                });
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                glSurfaceView.queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        particlesRenderer.handleTouchDrag(
                                                normalizedX, normalizedY);
                                    }
                                });
                            }
                        } else {
                            super.onTouchEvent(event);
                        }
//                    }

//            if (supportsEs2) {
//                glSurfaceView.setEGLContextClientVersion(2);
//                glSurfaceView.setRenderer(particlesRenderer);
//                rendererSet = true;
//            } else {
//                Toast.makeText(GLWallpaperService.this,
//                        "This device does not support OpenGL ES 2.0.",
//                        Toast.LENGTH_LONG).show();
//                return;
//            }
//                });

            super.onTouchEvent(event);

        }
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
// Check if the system supports OpenGL ES 2.0.
            ActivityManager activityManager =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = activityManager
                    .getDeviceConfigurationInfo();
            final boolean supportsEs2 =
                    configurationInfo.reqGlEsVersion >= 0x20000
// Check for emulator.
                            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                            && (Build.FINGERPRINT.startsWith("generic")
                            || Build.FINGERPRINT.startsWith("unknown")
                            || Build.MODEL.contains("google_sdk")
                            || Build.MODEL.contains("Emulator")
                            || Build.MODEL.contains("Android SDK built for x86")));

            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            glSurfaceView.setRenderer(particlesRenderer);
            rendererSet = true;

            // Assign our renderer.

        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onWallpaperDestroy();
        }
    }

}


