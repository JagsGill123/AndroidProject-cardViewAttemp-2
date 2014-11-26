package com.example.jgill.myapplication;

import android.app.Fragment;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class SimpleXYPLOTActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SimpleXYPLOTFragment();
    }

}