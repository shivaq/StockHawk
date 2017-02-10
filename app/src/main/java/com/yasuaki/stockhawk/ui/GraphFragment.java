package com.yasuaki.stockhawk.ui;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yasuaki.stockhawk.R;
import com.yasuaki.stockhawk.Utility;
import com.yasuaki.stockhawk.data.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 0;
    private static final String SCROLL_POSITION_KEY = "com.yasuaki.stockhawk.ui.SCROLL_POSITION_KEY";

    @BindView(R.id.text_label_one_day)
    TextView tvOneDay;
    @BindView(R.id.text_label_five_day)
    TextView tvFiveDay;
    @BindView(R.id.text_label_one_month)
    TextView tvOneMonth;
    @BindView(R.id.text_label_six_month)
    TextView tvSixMonth;
    @BindView(R.id.scroll_graph)
    ScrollView scrollView;
    @BindView(R.id.image_one_day_graph)
    ImageView quoteGraph;
    @BindView(R.id.image_five_day_graph)
    ImageView fiveDayGraph;
    @BindView(R.id.image_one_month_graph)
    ImageView oneMonthGraph;
    @BindView(R.id.image_five_six_month)
    ImageView sixMonthGraph;


    @BindView(R.id.text_day_low)
    TextView tvDayLow;
    @BindView(R.id.text_day_high)
    TextView tvDayHigh;
    @BindView(R.id.text_year_low)
    TextView tvYearLow;
    @BindView(R.id.text_year_high)
    TextView tvYearHigh;
    @BindView(R.id.text_eps)
    TextView tvEps;
    @BindView(R.id.text_roe)
    TextView tvRoe;
    @BindView(R.id.text_error_loading)
    TextView tvError;
    @BindView(R.id.loading_spinner)
    ProgressBar progressBar;
    @BindView(R.id.text_history_date)
    TextView tvHistoryDate;
    @BindView(R.id.text_history_price)
    TextView tvHistoryPrice;

    private String mSymbol;

    public GraphFragment() {
    }

    public static GraphFragment newInstance() {
        return new GraphFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.graph_fragment, container, false);
        ButterKnife.bind(this, rootView);

        Intent intent = getActivity().getIntent();
        //Get symbol and fetch graphs
        //Check if it's online
        if (Utility.networkUp(getContext())) {
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mSymbol = intent.getStringExtra(Intent.EXTRA_TEXT);

                final String ONE_DAY = "1d";
                final String FIVE_DAY = "5d";
                final String ONE_MONTH = "1m";
                final String SIX_MONTH = "6m";

                Uri oneDayGraphUri = Utility.buildUri(mSymbol, ONE_DAY);
                Uri fiveDayGraphUri = Utility.buildUri(mSymbol, FIVE_DAY);
                Uri oneMonthGraphUri = Utility.buildUri(mSymbol, ONE_MONTH);
                Uri sixMonthGraphUri = Utility.buildUri(mSymbol, SIX_MONTH);

                showProgressBar();

                //Customize picasso to get error message
                Picasso.Builder picassoBuilder = new Picasso.Builder(getActivity());
                picassoBuilder.listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        //Check if the request url is valid
                        String errorMessage = exception.getMessage();
                        if (errorMessage.equals("Failed to decode stream.")) {
                            showErrorView(getString(R.string.error_invalid_request));
                        }
                        Timber.e("GraphFragment:onImageLoadFailed: %s", errorMessage);
                    }
                });

                //Use Picasso.Builder to check error message
                picassoBuilder.build().load(oneDayGraphUri).into(quoteGraph);
                Picasso.with(getContext()).load(fiveDayGraphUri).into(fiveDayGraph);
                Picasso.with(getContext()).load(oneMonthGraphUri).into(oneMonthGraph);
                Picasso.with(getContext()).load(sixMonthGraphUri).into(sixMonthGraph);
                hideProgressBar();
            }
            getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
        } else {
            showErrorView(getString(R.string.error_no_network));
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)){
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(scrollPosition, scrollPosition);
                }
            });
        }

        setHasOptionsMenu(true);
        return rootView;
    }



    private int scrollPosition;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        scrollPosition = scrollView.getScrollY();
        outState.putInt(SCROLL_POSITION_KEY, scrollPosition);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case STOCK_LOADER:

                String selection = Contract.Quote.COLUMN_SYMBOL + "=?";
                String[] selectionArgs = {mSymbol};

                return new CursorLoader(
                        getActivity(),
                        Contract.Quote.URI,
                        null,
                        selection,
                        selectionArgs,
                        null);

            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //display detailed information of the stock
        if (data.moveToFirst()) {
            float dayLow = data.getFloat((Contract.Quote.POSITION_DAY_LOW));
            float dayHigh = data.getFloat((Contract.Quote.POSITION_DAY_HIGH));
            float yearLow = data.getFloat((Contract.Quote.POSITION_YEAR_LOW));
            float yearHigh = data.getFloat((Contract.Quote.POSITION_YEAR_HIGH));
            float eps = data.getFloat((Contract.Quote.POSITION_EPS));
            float roe = data.getFloat((Contract.Quote.POSITION_ROE));
            String historyDate = data.getString((Contract.Quote.POSITION_HISTORY_DATE));
            String historyPrice = data.getString((Contract.Quote.POSITION_HISTORY_CLOSING_PRICE));

            String strDayLow = "Day-Low : " + dayLow;
            String strDayHigh = "Day-High : " + dayHigh;
            String strYearLow = "Year-Low : " + yearLow;
            String strYearHigh = "Year-High : " + yearHigh;
            String strEps = "EPS : " + eps;
            String strRoe = "ROE : " + roe;

            tvDayLow.setText(strDayLow);
            tvDayHigh.setText(strDayHigh);
            tvYearLow.setText(strYearLow);
            tvYearHigh.setText(strYearHigh);
            tvEps.setText(strEps);
            tvRoe.setText(strRoe);
            tvHistoryDate.setText(historyDate);
            tvHistoryPrice.setText(historyPrice);
        }
    }


    private void showProgressBar() {
        ImageView[] iViews = {quoteGraph, fiveDayGraph, oneMonthGraph, sixMonthGraph};
        TextView[] tViews = {tvOneDay, tvFiveDay, tvOneMonth, tvSixMonth};
        for (TextView tView : tViews) {
            tView.setVisibility(View.GONE);
        }
        for (ImageView iView : iViews) {
            iView.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        ImageView[] iViews = {quoteGraph, fiveDayGraph, oneMonthGraph, sixMonthGraph};
        TextView[] tViews = {tvOneDay, tvFiveDay, tvOneMonth, tvSixMonth};
        for (ImageView iView : iViews) {
            iView.setVisibility(View.VISIBLE);
        }
        for (TextView tView : tViews) {
            tView.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    //Display if the error caused by network problem or invalid request.
    private void showErrorView(String errorMessage) {
        ImageView[] iViews = {quoteGraph, fiveDayGraph, oneMonthGraph, sixMonthGraph};
        TextView[] tViews = {tvDayLow, tvDayHigh, tvYearLow, tvYearHigh, tvEps, tvRoe,
                tvOneDay, tvFiveDay, tvOneMonth, tvSixMonth};
        for (ImageView iView : iViews) {
            iView.setVisibility(View.GONE);
        }
        for (TextView tView : tViews) {
            tView.setVisibility(View.GONE);
        }
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(errorMessage);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //loader has closed cursor. Release reference to loader.
        loader = null;
    }
}
