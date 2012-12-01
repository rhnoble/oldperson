package com.thirdlayer.oldperson;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AllOff extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_off);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_off, menu);
        return true;
    }
}
