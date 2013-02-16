
package com.thirdlayer.oldperson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Fragment {

    private Activity thisActivity;
    private EditText mTitleBox;
    private EditText mContentBox;
    private String mStartingTitle;
    private FileOutputStream fosContent;
    private OnNoteSavedListener mCallbackSaved;
    private NoteDoneListener mCallbackDone;
    private Button mButtonDone;
    private Button mButtonDelete;
    public interface OnNoteSavedListener {
        public void onNoteSaved(String title);
    }

    public interface NoteDoneListener {
        public void noteDone();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mStartingTitle = getArguments().getString("title");
        return inflater.inflate(R.layout.edit_note, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitleBox = (EditText) getView().findViewById(R.id.titlebox);
        mContentBox = (EditText) getView().findViewById(R.id.contentbox);
        mTitleBox.setText(mStartingTitle);
        mContentBox.setText(getContent(mStartingTitle));
        mButtonDone = (Button) getView().findViewById(R.id.done);
        mButtonDelete = (Button) getView().findViewById(R.id.delete);

        mButtonDone.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCallbackDone.noteDone();
            }
        });

        mButtonDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteNote();
            }
        });

    }

    private void deleteNote() {

        thisActivity.deleteFile(mStartingTitle);
        mTitleBox.setText("");
        mContentBox.setText("");
        mCallbackDone.noteDone();
    }

    public class DeleteNoteDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_note_dialog)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteNote();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private String getContent(String title) {
        FileInputStream fisContent;
        String mNoteContent = "";
        try {
            fisContent = thisActivity.openFileInput(title);

            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fisContent.read(buffer)) != -1) {
                fileContent.append(new String(buffer));
            }
            mNoteContent = fileContent.toString();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mNoteContent;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisActivity = activity;
        try {
            mCallbackDone = (NoteDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must imlpement NoteDoneListener");
        }
        try {
            mCallbackSaved = (OnNoteSavedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must imlpement OnNoteSelected");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        String mTitleToSave;
        if (!(mTitleBox.getText().toString().equals("") && mContentBox.getText().toString()
                .equals(""))) {
            if (mTitleBox.getText().toString().equals("")) {
                if (mContentBox.getText().toString().length() > 15) {
                    mTitleToSave = mContentBox.getText().toString().substring(0, 14);
                } else {
                    mTitleToSave = mContentBox.getText().toString();
                }
            } else {
                mTitleToSave = mTitleBox.getText().toString();
            }
            try {
                FileOutputStream fosContent = thisActivity.openFileOutput(mTitleToSave,
                        Context.MODE_PRIVATE);
                fosContent.write(mContentBox.getText().toString().getBytes());
                fosContent.close();
                mCallbackSaved.onNoteSaved(mTitleToSave);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(thisActivity, "file not found exception", Toast.LENGTH_LONG);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(thisActivity, "file io exception", Toast.LENGTH_LONG);
            }
        } else {
            mCallbackSaved.onNoteSaved("");
        }
        super.onPause();
    }

}
