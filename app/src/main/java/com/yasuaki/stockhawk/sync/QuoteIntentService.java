package com.yasuaki.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    Handler mHandler;
    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Create Handler instance in UI thread
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Timber.d("Intent handled");

        final String invalidSymbol = QuoteSyncJob.getQuotes(getApplicationContext());

        //If there were some invalid symbol, show it in main thread
        if (invalidSymbol != null) {
            //Do Toast from UI thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QuoteIntentService.this, "The symbol " + invalidSymbol + " doesn't exist!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
