package com.yasuaki.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yasuaki.stockhawk.R;
import com.yasuaki.stockhawk.Utility;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Get reference of Fragment
        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_fragment_container);

        //If fragment reference is null, Create and inflate new fragment instance
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            Utility.addFragmentToActivity(
                    getSupportFragmentManager(),
                    mainFragment,
                    R.id.main_fragment_container);
        }

    }

}
