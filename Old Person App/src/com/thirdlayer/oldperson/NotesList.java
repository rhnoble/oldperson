
package com.thirdlayer.oldperson;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class NotesList extends ListFragment {
    String[] mNoteTitles;
    Activity thisActivity;
    OnNoteSelectedListener mCallback;
    Button mNewNote;

    public interface OnNoteSelectedListener {
        public void onNoteSelected(String title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisActivity = activity;
        populateNoteList();
        try {
            mCallback = (OnNoteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must imlpement OnNoteSelected");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNewNote = (Button) thisActivity.findViewById(R.id.newnote);
        mNewNote.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCallback.onNoteSelected("");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create an array adapter to store the list of notes
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(inflater.getContext(),
                android.R.layout.simple_list_item_1, mNoteTitles);

        // Set the list adapter for the ListFragment
        setListAdapter(mAdapter);

        View view = inflater.inflate(R.layout.notes_list, container, false);

        return view;
    }

    private void populateNoteList() {
        // mNoteTitles = thisActivity.fileList();
        mNoteTitles = new String[11];
        mNoteTitles[0] = "first";
        mNoteTitles[1] = "second";
        mNoteTitles[2] = "third";
        mNoteTitles[3] = "third";
        mNoteTitles[4] = "third";
        mNoteTitles[5] = "third";
        mNoteTitles[6] = "third";
        mNoteTitles[7] = "third";
        mNoteTitles[8] = "third";
        mNoteTitles[9] = "third";
        mNoteTitles[10] = "third";

    }

    // @Override
    // public void onListItemClick(ListView l, View v, int position, long id) {
    // showDetails(position);
    // mCallback.onArticleSelected(position);
    // }

}
