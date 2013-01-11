
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Fragment {

    Activity thisActivity;
    EditText mTitleBox;
    EditText mContentBox;
    String mStartingTitle;
    FileOutputStream fosContent;
    OnNoteSavedListener mCallback;
    
    public interface OnNoteSavedListener {
        public void onNoteSaved(String title);
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
            mCallback = (OnNoteSavedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must imlpement OnNoteSelected");
        }
    }

    @Override
    public void onPause() {
        String mTitleToSave;
        if (mTitleBox.getText().toString() == "") {
            if (mContentBox.getText().toString().length() > 15) {
                mTitleToSave = mContentBox.getText().toString().substring(0, 14);
            } else {
                mTitleToSave = mContentBox.getText().toString();
            }
        } else {
            mTitleToSave = mTitleBox.getText().toString();
        }
        try {
            FileOutputStream fosContent = thisActivity.openFileOutput(mTitleToSave, Context.MODE_PRIVATE);
            fosContent.write(mContentBox.getText().toString().getBytes());
            fosContent.close();
            mCallback.onNoteSaved(mTitleToSave);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(thisActivity, "file not found exception", Toast.LENGTH_LONG);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(thisActivity, "file io exception", Toast.LENGTH_LONG);
        }
        super.onPause();
    }

}
