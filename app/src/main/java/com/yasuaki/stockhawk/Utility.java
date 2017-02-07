package com.yasuaki.stockhawk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class Utility {

    private static final String BASE_URI = "http://chart.finance.yahoo.com";

    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment,
                                             int frameId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameId, fragment);
        fragmentTransaction.commit();
    }

    //i.e)http://chart.finance.yahoo.com/z?s=GOOG&t=1d&q=l&l=on&z=s&p=m50,m200
    public static Uri buildUri(String symbol, String period){

        return Uri.parse(BASE_URI).buildUpon()
                .appendPath("z")
                .appendQueryParameter("s", symbol)
                .appendQueryParameter("t", period)
                .appendQueryParameter("l", "on")
                .appendQueryParameter("z", "s")
                .appendQueryParameter("p", "m50, m200")
                .build();
    }

    /**
     * Check network status
     */
    public static boolean networkUp(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
