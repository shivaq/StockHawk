package com.yasuaki.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yasuaki.stockhawk.R;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String GRAPH_FRAGMENT_TAG = "GFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        boolean isTwoPane = getResources().getBoolean(R.bool.use_two_pane_activity);

        if (isTwoPane) {
            Timber.d("MainActivity:onCreate: twoPane");
            if (savedInstanceState == null) {

                //２ペインならここで FragmentManager のトランザクション開始
                //main_two_pane_activity 内のID graph_fragment_container と
                //GraphFragment インスタンスを紐付けて使用する
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.graph_fragment_container,
                                GraphFragment.newInstance(),
                                GRAPH_FRAGMENT_TAG)
                        .commit();
                Timber.d("MainActivity:onCreate: GraphFragment instance is created");
            }
        } else {
            Timber.d("MainActivity:onCreate: onePane");
        }

        //FragmentManager を使って ID をもとに MainFragment インスタンスへの参照を取得
        //変数に格納
        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_fragment_container);

        //MainFragment インスタンスへの参照が取得できたか確認
        if (mainFragment == null) {
            //1 ペインならここで FragmentManager のトランザクション開始
            //main_one_pane_activity 内のID main_fragment_container と
            //MainFragment インスタンスを紐付けて使用する
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container,
                            MainFragment.newInstance())
                    .commit();
            Timber.d("MainActivity:onCreate: MainFragment instance is created");
        }
    }
}
