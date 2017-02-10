package com.yasuaki.stockhawk.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yasuaki.stockhawk.R;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        Timber.d("MainActivity:onCreate: contentUri is %s", contentUri);



            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_fragment_container,
                                MainFragment.newInstance())
                        .commit();
            }

        if(contentUri != null){

        }
    }
}
