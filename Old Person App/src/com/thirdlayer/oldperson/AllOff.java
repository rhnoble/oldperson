
package com.thirdlayer.oldperson;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AllOff extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_off);

        Button mButtonMagnify = (Button) findViewById(R.id.magnify);
        Button mButtonNotes = (Button) findViewById(R.id.notes);
        Button mButtonLight = (Button) findViewById(R.id.light);

        mButtonMagnify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent mGoToMagnify = new Intent(AllOff.this, Maginfy.class);
                AllOff.this.startActivity(mGoToMagnify);
            }
        });
        
        mButtonNotes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent mGoToMagnify = new Intent(AllOff.this, Notes.class);
                AllOff.this.startActivity(mGoToMagnify);
            }
        });
        
        mButtonNotes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_off, menu);
        return true;
    }

}
