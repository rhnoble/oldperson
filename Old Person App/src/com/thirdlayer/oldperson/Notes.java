
package com.thirdlayer.oldperson;

import java.io.IOException;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Notes extends FragmentActivity implements NotesList.OnNoteSelectedListener, SurfaceHolder.Callback {

    String mSelectedNote;
    // UI
    private ImageView mButtonMagnify;
    private ImageView mButtonNotes;
    private ImageView mButtonLight;
    private SurfaceView mPreview;

    // State flags
    private Boolean mLightIsOn;
    
 // Passed Data
    Intent mIntent;

    // Light functionality
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);
        if (findViewById(R.id.fragmentcontainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            NotesList mNotesList = new NotesList();

            // In case this activity was started with special instructions from
            // an Intent,
            // pass the Intent's extras to the fragment as arguments
            mNotesList.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentcontainer, mNotesList).commit();
        }
        // Instantiate variables
        mButtonMagnify = (ImageView) findViewById(R.id.magnify);
        mButtonNotes = (ImageView) findViewById(R.id.notes);
        mButtonLight = (ImageView) findViewById(R.id.light);
        mLightIsOn = false;

        // Camera setup for flashlight
        // mCamera = Camera.open();
        mPreview = (SurfaceView) findViewById(R.id.preview);
        SurfaceHolder mHolder = mPreview.getHolder();
        mHolder.addCallback(this);
        mIntent = getIntent();
        mLightIsOn = mIntent.getBooleanExtra("lightIsOn", false);

        mButtonMagnify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent mGoToMagnify = new Intent(Notes.this, Magnify.class);
                mGoToMagnify.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mGoToMagnify.putExtra("lightIsOn", mLightIsOn);
                Notes.this.startActivity(mGoToMagnify);
            }
        });

        mButtonNotes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent mGoToAllOff = new Intent(Notes.this, AllOff.class);
                mGoToAllOff.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mGoToAllOff.putExtra("lightIsOn", mLightIsOn);
                Notes.this.startActivity(mGoToAllOff);
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
        mCamera.startPreview();
    }
    
    private void turnLightOff() {
        Parameters params = mCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
        mCamera.stopPreview();
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
