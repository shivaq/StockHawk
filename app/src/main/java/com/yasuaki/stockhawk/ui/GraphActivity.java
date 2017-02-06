package com.yasuaki.stockhawk.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yasuaki.stockhawk.R;
import com.yasuaki.stockhawk.Utility;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        GraphFragment graphFragment =
                (GraphFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.graph_fragment_container);

        if (graphFragment == null) {
            graphFragment = GraphFragment.newInstance();
            Utility.addFragmentToActivity(
                    getSupportFragmentManager(),
                    graphFragment,
                    R.id.graph_fragment_container);
        }
    }
}
