package com.yasuaki.stockhawk.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yasuaki.stockhawk.R;
import com.yasuaki.stockhawk.Utilily;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphFragment extends Fragment {

    private final String ONE_DAY = "1d";
    private final String FIVE_DAY = "5d";
    private final String ONE_MONTH = "1m";
    private final String SIX_MONTH = "6m";

    @BindView(R.id.stock_history_graph)
    ImageView quoteGraph;
    @BindView(R.id.stock_five_day_graph)
    ImageView fiveDayGraph;

    //TODO:Uri を生成して、Picasso で利用するためのロジック

    public GraphFragment() {
    }

    public static GraphFragment newInstance() {
        return new GraphFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        ButterKnife.bind(this, rootView);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String symbol = intent.getStringExtra(Intent.EXTRA_TEXT);

            Uri oneDayGraphUri = Utilily.buildUri(symbol, ONE_DAY);
            Uri fiveDayGraphUri = Utilily.buildUri(symbol, FIVE_DAY);

            Picasso.with(getContext()).load(oneDayGraphUri).into(quoteGraph);
            Picasso.with(getContext()).load(fiveDayGraphUri).into(fiveDayGraph);
        }

        //TODO:Intent で飛んできて、シンボルを取得し、それをもってグラフを取得


        setHasOptionsMenu(true);
        return rootView;
    }
}
