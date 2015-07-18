
package com.thirdlayer.oldperson;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NavActivity extends FragmentActivity implements NotesList.OnNoteSelectedListener,
        EditNote.OnNoteSavedListener, EditNote.NoteDoneListener, ResizeListener{

    String mSelectedNote;
/*    ResizeListener mResizeListener;*/
    // UI
    private ImageView mButtonMagnify;
    private ImageView mButtonNotes;
    private ImageView mButtonLight;
    private SurfaceView mPreview;
    private LinearLayout mNavButtons;
    // private FrameLayout mToolBox;

    // State flags
    private Boolean mLightIsOn;
    private Boolean cameraConfigured;
    private Boolean inPreview;
    private String mCurrentTool;
    private Boolean mNoteIsOpen;
    private String mLastNoteOpen;
    private FragmentBackStack mFragmentBackStack;

    // Passed Data
    Intent mIntent;

    // Light functionality
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        mNoteIsOpen = false;
        mLastNoteOpen = "";
        mNavButtons = (LinearLayout) findViewById(R.id.nav);
        // mToolBox = (FrameLayout) findViewById(R.id.toolbox);



        ((KeyboardHandlingLinearLayout) findViewById(R.id.oldperson))
                .SetOnResizeListener(this);

        // Camera setup for flashlight
        // mCamera = Camera.open();

        mIntent = getIntent();
        mLightIsOn = mIntent.getBooleanExtra("lightIsOn", false);

        mButtonMagnify.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mButtonMagnify.setImageResource(R.drawable.glass_onpress);

                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    onMagnifyClick();
                }
                return true;
            }
        });

        mButtonNotes.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mButtonNotes.setImageResource(R.drawable.notes_onpress);

                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    onNotesClick();
                }
                return true;
            }
        });

        mButtonLight.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mButtonLight.setImageResource(R.drawable.light_onpress);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    onLightClick();
                }
                return true;
            }
        });

        /*
         * Intent mGoToAllOff = new Intent(Notes.this, } AllOff.class);
         * mGoToAllOff.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
         * mGoToAllOff.putExtra("lightIsOn", mLightIsOn);
         * Notes.this.startActivity(mGoToAllOff); } });
         */
    }

    public void onLightClick() {
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

    public void onNotesClick() {
        if (mCurrentTool.equals("NoteEdit") || mCurrentTool.equals("NoteList")) {
            AllOffFragment allOffFragment = new AllOffFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, allOffFragment, "AllOff");
            mFragmentBackStack.addToStack("AllOff");

            // Commit the transaction
            transaction.commit();
            mCurrentTool = "AllOff";
            mButtonNotes.setImageResource(R.drawable.notes);

        } else if (mNoteIsOpen) {
            onNoteSelected(mLastNoteOpen);
            mButtonNotes.setImageResource(R.drawable.notes_depressed);
            mButtonMagnify.setImageResource(R.drawable.glass);
        } else {
            NotesList notesList = new NotesList();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, notesList, "NoteList");
            mFragmentBackStack.addToStack("NoteList");

            // Commit the transaction
            transaction.commit();
            mCurrentTool = "NoteList";
            mButtonNotes.setImageResource(R.drawable.notes_depressed);
            mButtonMagnify.setImageResource(R.drawable.glass);

        }

    }

    public void onMagnifyClick() {
        if (mCurrentTool.equals("Magnify")) {
            AllOffFragment allOffFragment = new AllOffFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, allOffFragment, "AllOff");
            mFragmentBackStack.addToStack("AllOff");

            // Commit the transaction
            transaction.commit();
            mCurrentTool = "AllOff";
            mButtonMagnify.setImageResource(R.drawable.glass);

        } else {
            MagnifyClear magnifyClear = new MagnifyClear();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, magnifyClear, "Magnify");
            mFragmentBackStack.addToStack("Magnify");

            // Commit the transaction
            transaction.commit();
            mCurrentTool = "Magnify";
            mCamera.startPreview();
            mButtonMagnify.setImageResource(R.drawable.glass_depressed);
            mButtonNotes.setImageResource(R.drawable.notes);
        }

    }

    private void turnLightOn() {
        Parameters p = mCamera.getParameters();
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
        mButtonLight.setImageResource(R.drawable.light_depressed);
        // mCamera.startPreview();
    }

    private void turnLightOff() {
        Parameters params = mCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
        mButtonLight.setImageResource(R.drawable.light);
        // if (!mCurrentTool.equals("Magnify")) {
        // mCamera.stopPreview();
        // }
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
        transaction.replace(R.id.fragmentcontainer, editNote, "NoteEdit");
        mFragmentBackStack.addToStack("NoteEdit");

        // Commit the transaction
        transaction.commit();
        mNoteIsOpen = true;
        mCurrentTool = "NoteEdit";

    }

    public void onNoteSaved(String title) {
        mLastNoteOpen = title;
    }

    @Override
    public void onDestroy() {
        if (inPreview) {
            mCamera.stopPreview();
            inPreview = false;
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            cameraConfigured = false;
        }
        super.onDestroy();

    }

    @Override
    public void onPause() {
        if (inPreview) {
            mCamera.stopPreview();
            inPreview = false;
        }

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            cameraConfigured = false;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open();
        mHolder = mPreview.getHolder();
        mHolder.addCallback(surfaceCallback);
        if (cameraConfigured == null) {
            cameraConfigured = false;
        }
        if (mLightIsOn == null) {
            //if (findViewById(R.id.fragmentcontainer) != null) {

                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.

                // Create an instance of ExampleFragment
//                AllOffFragment allOffFragment = new AllOffFragment();

                // In case this activity was started with special instructions
                // from
                // an Intent,
                // pass the Intent's extras to the fragment as arguments
//                allOffFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragmentcontainer, allOffFragment).commit();
                mCurrentTool = "AllOff";
//            }
            // Instantiate variables
            mButtonMagnify = (ImageView) findViewById(R.id.magnify);
            mButtonNotes = (ImageView) findViewById(R.id.notes);
            mButtonLight = (ImageView) findViewById(R.id.light);
            mLightIsOn = false;
            inPreview = false;
            cameraConfigured = false;
            mNoteIsOpen = false;
            mLastNoteOpen = "";
            mNavButtons = (LinearLayout) findViewById(R.id.nav);
            // mToolBox = (FrameLayout) findViewById(R.id.toolbox);

           // Camera setup for flashlight
            // mCamera = Camera.open();

            mIntent = getIntent();
            mLightIsOn = mIntent.getBooleanExtra("lightIsOn", false);

            mButtonMagnify.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonMagnify.setImageResource(R.drawable.glass_onpress);

                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        onMagnifyClick();
                    }
                    return true;
                }
            });

            mButtonNotes.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonNotes.setImageResource(R.drawable.notes_onpress);

                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        onNotesClick();
                    }
                    return true;
                }
            });

            mButtonLight.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mButtonLight.setImageResource(R.drawable.light_onpress);
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        onLightClick();
                    }
                    return true;
                }
            });

            // mLightIsOn = false;
            // onCreate(null);
            /*
             * } if (mButtonLight == null) { mButtonLight = (ImageView)
             * findViewById(R.id.light); ; } if (mButtonNotes == null) {
             * mButtonNotes = (ImageView) findViewById(R.id.notes); ; } if
             * (mButtonMagnify == null) { mButtonMagnify = (ImageView)
             * findViewById(R.id.magnify); ; }
             * mButtonMagnify.setOnTouchListener(new View.OnTouchListener() {
             * public boolean onTouch(View view, MotionEvent motionEvent) { if
             * (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
             * mButtonMagnify.setImageResource(R.drawable.glass_onpress); } else
             * if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
             * onMagnifyClick(); } return true; } });
             * mButtonNotes.setOnTouchListener(new View.OnTouchListener() {
             * public boolean onTouch(View view, MotionEvent motionEvent) { if
             * (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
             * mButtonNotes.setImageResource(R.drawable.notes_onpress); } else
             * if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
             * onNotesClick(); } return true; } });
             * mButtonLight.setOnTouchListener(new View.OnTouchListener() {
             * public boolean onTouch(View view, MotionEvent motionEvent) { if
             * (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
             * mButtonLight.setImageResource(R.drawable.light_onpress); } else
             * if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
             * onLightClick(); } return true; } });
             */
        }
        initPreview();
        startPreview();
        if (mCurrentTool.equals("Magnify")) {
            mCamera.startPreview();
        }
        if (mLightIsOn) {
            turnLightOn();
        }

    }

    private void startPreview() {
        Log.w("startPreview() cameraConfigured =", cameraConfigured.toString());
        Log.w("startPreview() mCamera =", mCamera.toString());
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
            initPreview(/* width, height */);
            Log.w("surfaceChanged()", holder.toString());
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

    private void initPreview(/* int width, int height */) {
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

    public void noteDone() {
        NotesList notesList = new NotesList();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentcontainer, notesList, "NoteList");
        mFragmentBackStack.addToStack("NoteList");

        // Commit the transaction
        transaction.commit();
        mNoteIsOpen = false;
        mCurrentTool = "NoteList";
    }

    @Override
    public void onBackPressed() {
        final String mLastFragment = mFragmentBackStack.onBackPressed();
        if (mLastFragment.equals("empty stack")) {
            super.onBackPressed();
        } else if (mLastFragment.equals("AllOff")) {
            AllOffFragment allOffFragment = new AllOffFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, allOffFragment, "AllOff");
            transaction.commit();
            mCurrentTool = "AllOff";
            mButtonNotes.setImageResource(R.drawable.notes);
            mButtonMagnify.setImageResource(R.drawable.glass);
        } else if (mLastFragment.equals("Magnify")) {
            MagnifyClear magnifyClear = new MagnifyClear();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, magnifyClear, "Magnify");
            transaction.commit();
            mCurrentTool = "Magnify";
            mCamera.startPreview();
            mButtonMagnify.setImageResource(R.drawable.glass_depressed);
            mButtonNotes.setImageResource(R.drawable.notes);
        } else if (mLastFragment.equals("NoteList")) {
            NotesList notesList = new NotesList();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragmentcontainer, notesList, "NoteList");
            mFragmentBackStack.addToStack("NoteList");

            // Commit the transaction
            transaction.commit();
            mCurrentTool = "NoteList";
            mButtonNotes.setImageResource(R.drawable.notes_depressed);
            mButtonMagnify.setImageResource(R.drawable.glass);
        } else if (mLastFragment.equals("NoteEdit")) {
            EditNote editNote = new EditNote();
            Bundle args = new Bundle();
            args.putString("title", mLastNoteOpen);
            editNote.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this
            // fragment,
            // and add the transaction to the back stack so the user can
            // navigate
            // back
            transaction.replace(R.id.fragmentcontainer, editNote, "NoteEdit");

            // Commit the transaction
            transaction.commit();
            mNoteIsOpen = true;
            mCurrentTool = "NoteEdit";
            mButtonNotes.setImageResource(R.drawable.notes_depressed);
            mButtonMagnify.setImageResource(R.drawable.glass);
        }
    }

    public void onKeyboardUp() {
        mNavButtons.setVisibility(View.GONE);
    }

    public void onKeyboardDown() {
        mNavButtons.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        mFragmentBackStack = null;
        mPreview = null;
        mHolder = null;
        super.onStop();
    }

    @Override
    public void onRestart() {

        super.onRestart();
    }

    @Override
    public void onStart() {
        mPreview = (SurfaceView) findViewById(R.id.preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(surfaceCallback);
        mFragmentBackStack = new FragmentBackStack();
        mFragmentBackStack.addToStack(mCurrentTool);
        super.onStart();
    }

    public void onResize(int id, int xNew, int yNew, int xOld, int yOld) {
        {
            if (xNew * yNew < xOld * yOld) {
                onKeyboardUp();
            } else {
                onKeyboardDown();
            }
        }
    }
}
