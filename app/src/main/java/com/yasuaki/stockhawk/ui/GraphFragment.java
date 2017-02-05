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

    @BindView(R.id.image_one_day_graph)
    ImageView quoteGraph;
    @BindView(R.id.image_five_day_graph)
    ImageView fiveDayGraph;
    @BindView(R.id.image_one_month_graph)
    ImageView oneMonthGraph;
    @BindView(R.id.image_five_six_month)
    ImageView sixMonthGraph;

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

            String eps =



            Uri oneDayGraphUri = Utilily.buildUri(symbol, ONE_DAY);
            Uri fiveDayGraphUri = Utilily.buildUri(symbol, FIVE_DAY);
            Uri oneMonthGraphUri = Utilily.buildUri(symbol, ONE_MONTH);
            Uri sixMonthGraphUri = Utilily.buildUri(symbol, SIX_MONTH);

            Picasso.with(getContext()).load(oneDayGraphUri).into(quoteGraph);
            Picasso.with(getContext()).load(fiveDayGraphUri).into(fiveDayGraph);
            Picasso.with(getContext()).load(oneMonthGraphUri).into(oneMonthGraph);
            Picasso.with(getContext()).load(sixMonthGraphUri).into(sixMonthGraph);
        }

        setHasOptionsMenu(true);
        return rootView;
    }
}
