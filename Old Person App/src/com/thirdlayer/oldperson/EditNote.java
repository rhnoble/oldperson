package com.thirdlayer.oldperson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Fragment{
    
    Activity thisActivity;
    EditText mTitleBox;
    EditText mContentBox;
    String mStartingTitle;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mStartingTitle = getArguments().getString("title");
        return inflater.inflate(R.layout.edit_note, container, false);
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitleBox = (EditText) getView().findViewById(R.id.titlebox);
        mContentBox = (EditText) getView().findViewById(R.id.contentbox);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisActivity = activity;
    }
    
    @Override
    public void onPause() {
        try {
            FileOutputStream fosContent = thisActivity.openFileOutput(mTitleBox.getText().toString(), Context.MODE_PRIVATE);
            fosContent.write(mContentBox.getText().toString().getBytes());
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
