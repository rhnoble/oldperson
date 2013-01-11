
package com.thirdlayer.oldperson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.thirdlayer.oldperson.NotesList.OnNoteSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Fragment {

    Activity thisActivity;
    EditText mTitleBox;
    EditText mContentBox;
    String mStartingTitle;
    FileOutputStream fosContent;
    OnNoteSavedListener mCallbackSaved;
    NoteDoneListener mCallbackDone;
    Button mButtonDone;
    Button mButtonDelete;

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
                thisActivity.deleteFile(mStartingTitle);
                mTitleBox.setText("");
                mContentBox.setText("");
                mCallbackDone.noteDone();
            }
        });

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
        int x = 1;
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
