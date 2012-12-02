
package com.thirdlayer.oldperson;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AllOff extends Activity implements SurfaceHolder.Callback {

    // UI
    private Button mButtonMagnify;
    private Button mButtonNotes;
    private Button mButtonLight;
    private SurfaceView mPreview;

    // State flags
    private Boolean mLightIsOn;
    
    // Light functionality
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_off);

        // Instantiate variables
        mButtonMagnify = (Button) findViewById(R.id.magnify);
        mButtonNotes = (Button) findViewById(R.id.notes);
        mButtonLight = (Button) findViewById(R.id.light);
        mLightIsOn = false;

        // Camera setup for flashlight
        //mCamera = Camera.open();
        mPreview = (SurfaceView) findViewById(R.id.preview);
        SurfaceHolder mHolder = mPreview.getHolder();
        mHolder.addCallback(this);

        mButtonMagnify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                 Intent mGoToMagnify = new Intent(AllOff.this, Magnify.class);
                 mGoToMagnify.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 mGoToMagnify.putExtra("lightIsOn", mLightIsOn);
                 AllOff.this.startActivity(mGoToMagnify);
            }
        });

        mButtonNotes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                 Intent mGoToNotes = new Intent(AllOff.this, Notes.class);
                 mGoToNotes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 mGoToNotes.putExtra("lightIsOn", mLightIsOn);
                 AllOff.this.startActivity(mGoToNotes);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_off, menu);
        return true;
    }
    
    private void turnLightOn() {
        Parameters p = mCamera.getParameters();
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
        mCamera.startPreview();
    }
    
    private void turnLightOff() {
        Parameters params = mCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
        mCamera.stopPreview();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }
    
    @Override
    public void onDestroy() {
        mCamera.stopPreview();
        mCamera.release();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
        mCamera.release();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open();
        if (mLightIsOn) {
            turnLightOn();
        }
    }

}
