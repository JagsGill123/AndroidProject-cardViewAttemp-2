package com.example.jgill.myapplication;

import android.app.Fragment;


public class CountryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CountryFragment();
    }
}
