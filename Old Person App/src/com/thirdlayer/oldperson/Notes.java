
package com.thirdlayer.oldperson;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class Notes extends FragmentActivity implements NotesList.OnNoteSelectedListener {

    String mSelectedNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);
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
        transaction.replace(R.id.notes, editNote);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }
}
