
package com.thirdlayer.oldperson;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Magnify extends Activity {
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;
    
    // UI variables
    private ImageView mButtonMagnify;
    private ImageView mButtonNotes;
    private ImageView mButtonLight;
    
    // State flags
    private Boolean mLightIsOn;
    private int mApiVersion;
    
    // Passed Data
    Intent mIntent;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnify);
        
        // Instantiate variables
        mButtonMagnify = (ImageView) findViewById(R.id.magnify);
        mButtonNotes = (ImageView) findViewById(R.id.notes);
        mButtonLight = (ImageView) findViewById(R.id.light);
        mIntent = getIntent();
        mLightIsOn = mIntent.getBooleanExtra("lightIsOn", false);
        mApiVersion = android.os.Build.VERSION.SDK_INT;

        // Magnify variables
        mPreview = (SurfaceView) findViewById(R.id.preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(surfaceCallback);
        //previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mButtonMagnify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                 Intent mGoToMagnify = new Intent(Magnify.this, AllOff.class);
                 mGoToMagnify.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 mGoToMagnify.putExtra("lightIsOn", mLightIsOn);
                 Magnify.this.startActivity(mGoToMagnify);
            }
        });

        mButtonNotes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                 Intent mGoToNotes = new Intent(Magnify.this, Notes.class);
                 mGoToNotes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 mGoToNotes.putExtra("lightIsOn", mLightIsOn);
                 Magnify.this.startActivity(mGoToNotes);
            }
        });

        mButtonLight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH))
                    ;
                {
                    if (mLightIsOn) {
                        turnLightOff();
                        mLightIsOn = false;
                    } else {
                        turnLightOn();
                        mLightIsOn = true;
                    }
                }
            }
        });
    }
    
    
    
    private void turnLightOn() {
        Parameters p = mCamera.getParameters();
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
    }
    
    private void turnLightOff() {
        Parameters params = mCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCamera = Camera.open();
        // Set light
        if (mLightIsOn) {
            turnLightOn();
        }
        startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            mCamera.stopPreview();
        }

        mCamera.release();
        mCamera = null;
        inPreview = false;

        super.onPause();
    }

//    private Camera.Size getBestPreviewSize(int width, int height,
//            Camera.Parameters parameters) {
//        Camera.Size result = null;

//        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
//            if (size.width <= width && size.height <= height) {
//                if (result == null) {
//                    result = size;
//                }
//                else {
//                    int resultArea = result.width * result.height;
//                    int newArea = size.width * size.height;

//                    if (newArea > resultArea) {
//                        result = size;
//                    }
//                }
//            }
//        }

//        return (result);
//    }

    private void initPreview(int width, int height) {
        if (mCamera != null && mHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(Magnify.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters mParameters = mCamera.getParameters();
//                Camera.Size size = getBestPreviewSize(width, height,
//                        parameters);

//                if (size != null) {
                    //parameters.set("orientation", "portrait");
                    mCamera.setDisplayOrientation(90);
                    if (mParameters.isZoomSupported()) {
                        int mMaxZoom = mParameters.getMaxZoom();
                        if (mMaxZoom > 30) {
                            mParameters.setZoom(30);
                        } else {
                            mParameters.setZoom(mMaxZoom);
                        }
                    }
                    List<String> focusModes = mParameters.getSupportedFocusModes();
                    if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        mParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        mParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                   // if (mApiVersion > 9) {

                     //   parameters.setFocusMode(Parameters.);
                    //}
//                    parameters.setPreviewSize(size.width, size.height);
                    mCamera.setParameters(mParameters);
                    cameraConfigured = true;
                }
            }
  //      }
    }

    private void startPreview() {
        if (cameraConfigured && mCamera != null) {
            mCamera.startPreview();
            inPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                int format, int width,
                int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };
}
