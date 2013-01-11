
package com.thirdlayer.oldperson;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class NavActivity extends FragmentActivity implements NotesList.OnNoteSelectedListener,
        SurfaceHolder.Callback {

    String mSelectedNote;
    // UI
    private ImageView mButtonMagnify;
    private ImageView mButtonNotes;
    private ImageView mButtonLight;
    private SurfaceView mPreview;

    // State flags
    private Boolean mLightIsOn;
    private Boolean cameraConfigured;
    private Boolean inPreview;
    private String mCurrentTool;

    // Passed Data
    Intent mIntent;

    // Light functionality
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        if (findViewById(R.id.fragmentcontainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            AllOffFragment allOffFragment = new AllOffFragment();

            // In case this activity was started with special instructions from
            // an Intent,
            // pass the Intent's extras to the fragment as arguments
            allOffFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentcontainer, allOffFragment).commit();
            mCurrentTool = "AllOff";
        }
        // Instantiate variables
        mButtonMagnify = (ImageView) findViewById(R.id.magnify);
        mButtonNotes = (ImageView) findViewById(R.id.notes);
        mButtonLight = (ImageView) findViewById(R.id.light);
        mLightIsOn = false;
        inPreview = false;
        cameraConfigured = false;

        // Camera setup for flashlight
        // mCamera = Camera.open();
        mPreview = (SurfaceView) findViewById(R.id.preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(surfaceCallback);
        mIntent = getIntent();
        mLightIsOn = mIntent.getBooleanExtra("lightIsOn", false);

        mButtonMagnify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mCurrentTool.equals("Magnify")) {
                    AllOffFragment allOffFragment = new AllOffFragment();
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragmentcontainer, allOffFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                    mCurrentTool = "AllOff";

                } else {
                    MagnifyClear magnifyClear = new MagnifyClear();
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragmentcontainer, magnifyClear, "Magnify");
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                    mCurrentTool = "Magnify";
                    mCamera.startPreview();
                }

            }
        });
        /*
         * mButtonNotes.setOnClickListener(new OnClickListener() { public void
         * onClick(View v) { Intent mGoToAllOff = new Intent(Notes.this,
         * AllOff.class);
         * mGoToAllOff.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
         * mGoToAllOff.putExtra("lightIsOn", mLightIsOn);
         * Notes.this.startActivity(mGoToAllOff); } });
         */
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
        mCamera.startPreview();
    }

    private void turnLightOff() {
        Parameters params = mCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
        if (!mCurrentTool.equals("Magnify")) {
            mCamera.stopPreview();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes, menu);
        return true;
    }

    public void onNoteSelected(String title) {
        // Otherwise, we're in the one-pane layout and must swap frags...

        // Create fragment and give it an argument for the selected article
        EditNote editNote = new EditNote();
        Bundle args = new Bundle();
        args.putString("title", title);
        editNote.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragmentcontainer, editNote);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

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
        mCamera.release();
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

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open();
        if (mLightIsOn) {
            turnLightOn();
        }
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

    private void initPreview(int width, int height) {
        if (mCamera != null && mHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(NavActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters mParameters = mCamera.getParameters();
                // Camera.Size size = getBestPreviewSize(width, height,
                // parameters);

                // if (size != null) {
                // parameters.set("orientation", "portrait");
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

                // parameters.setFocusMode(Parameters.);
                // }
                // parameters.setPreviewSize(size.width, size.height);
                mCamera.setParameters(mParameters);
                cameraConfigured = true;
            }
        }
        // }
    }

}
