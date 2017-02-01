package com.yasuaki.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yasuaki.stockhawk.data.Contract;
import com.yasuaki.stockhawk.data.PrefUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {
    }

    /**
     * Get quotes with sharedPreferenced symbol from yahoo finance API.
     * This is called from QuoteIntentService in background thread
     *
     * @param context
     */
    static void getQuotes(Context context) {

        Timber.d("Running sync job");

        //Start and end day for quotes to retrieve
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {
            //Get stock symbols from sharedPreferences
            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);


            if (stockArray.length == 0) {
                return;
            }

            //Query Stock data from YahooFinance api.
            // Returned map only includes the Stocks
            // that successfully be retrieved from Yahoo Finance.
            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();

            //Iterate stock data
            while (iterator.hasNext()) {
                String symbol = iterator.next();

                //Get stock with symbol as a key
                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();

                float price = quote.getPrice().floatValue();
                //Get difference between current price and previous closing price
                float change = quote.getChange().floatValue();
                float percentChange = quote.getChangeInPercent().floatValue();

                // WARNING! Don't request historical data for a stock that doesn't exist!
                // The request will hang forever X_x
                List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                StringBuilder historyBuilder = new StringBuilder();

                //Retrieve date and closing price
                for (HistoricalQuote it : history) {
                    historyBuilder.append(it.getDate().getTimeInMillis());
                    historyBuilder.append(", ");
                    historyBuilder.append(it.getClose());
                    historyBuilder.append("\n");
                }

                ContentValues quoteCV = new ContentValues();
                quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);

                Timber.d("History data of %s is %s", symbol, historyBuilder.toString());

                quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());
                contentValuesArrayList.add(quoteCV);
            }

            //Insert stock data
            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]));

            //Broadcast updated quotes
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    /**
     * Set job schedule for QuoteJobService.class
     */
    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(
                PERIODIC_ID, new ComponentName(context, QuoteJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);
        JobScheduler scheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    /**
     * If network is ok, startService. If network is not ok, try one time job.
     *
     * @param context
     */
    public static synchronized void syncImmediately(Context context) {

        //If network is ok, startService
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {
            //ONE_OFF_ID: ID for one time job
            JobInfo.Builder builder = new JobInfo.Builder(
                    ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));

            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.schedule(builder.build());
        }
    }
}
