
package com.thirdlayer.oldperson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
        populateNoteList();
        // Create an array adapter to store the list of notes
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(inflater.getContext(),
                android.R.layout.simple_list_item_1, mNoteTitles);

        // Set the list adapter for the ListFragment
        setListAdapter(mAdapter);

        View view = inflater.inflate(R.layout.notes_list, container, false);

        return view;
    }

    private void populateNoteList() {
        ArrayList<String> mNoteTitlesSorting = new ArrayList<String>(Arrays.asList(thisActivity
                .fileList()));
        Collections.sort(mNoteTitlesSorting, new CompareLastModified());
        mNoteTitles = mNoteTitlesSorting.toArray(new String[mNoteTitlesSorting.size()]);
    }

    class CompareLastModified implements Comparator<String> {

        public CompareLastModified() {
        }

        public int compare(String o1, String o2) {
            long o1Mod = getActivity().getFileStreamPath(o1).lastModified();
            long o2Mod = getActivity().getFileStreamPath(o2).lastModified();
            return (int) (o2Mod - o1Mod);
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onNoteSelected(mNoteTitles[position]);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateNoteList();
    }

}
