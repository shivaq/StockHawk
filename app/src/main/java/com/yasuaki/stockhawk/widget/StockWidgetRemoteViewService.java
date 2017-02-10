package com.yasuaki.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.yasuaki.stockhawk.R;
import com.yasuaki.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

//RemoteViewsService
// →a remote collection view (ListView, GridView, etc) にデータを配置するためのService
public class StockWidgetRemoteViewService extends RemoteViewsService {

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };


    //a remote collection view (ListView, GridView, etc) と
    // それに反映させるdataとのAdapterのためのインターフェイス
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.d("StockWidgetRemoteViewService:onGetViewFactory: ");
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
                Timber.d("StockWidgetRemoteViewService:onCreate: ");
            }

            @Override
            public void onDataSetChanged() {
                Timber.d("StockWidgetRemoteViewService:onDataSetChanged: ");
                if (cursor != null) {
                    cursor.close();
                }
                //Temporarily clear IPC identity
                final long identityToken = Binder.clearCallingIdentity();

                cursor = getContentResolver().query(
                        Contract.Quote.URI,
                        STOCK_COLUMNS,
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                Timber.d("StockWidgetRemoteViewService:onDestroy: ");
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                    Timber.d("StockWidgetRemoteViewService:onDestroy: cursor is closed");
                }
            }

            @Override
            public int getCount() {
                Timber.d("StockWidgetRemoteViewService:getCount: ");
                return cursor == null ? 0 : cursor.getCount();
            }


            //cursorを、該当ポジションに移動して、そこのアイテムにデータを紐付けていく
            @Override
            public RemoteViews getViewAt(int position) {

                //Check validation
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                DecimalFormat dollarFormat =
                        (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");




                String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                String price = dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE));
                float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                Timber.d("StockWidgetRemoteViewService:getViewAt: Symbol is %s, price is %s and rawAbsoluteChange is %s", symbol, price, rawAbsoluteChange);

                remoteViews.setTextViewText(R.id.symbol_widget, symbol);
                remoteViews.setTextViewText(R.id.price_widget, price);
                remoteViews.setTextViewText(R.id.change_widget, change);

                if (rawAbsoluteChange > 0) {
                    Timber.d("StockWidgetRemoteViewService:getViewAt: rawAbsoluteChange is green");
                    remoteViews.setInt(R.id.change_widget, "setBackgroundResource",
                            R.drawable.percent_change_pill_green);
                } else {
                    Timber.d("StockWidgetRemoteViewService:getViewAt: rawAbsoluteChange is red");
                    remoteViews.setInt(R.id.change_widget, "setBackgroundResource",
                            R.drawable.percent_change_pill_red);
                }

                //Create cost effective intent
                final Intent fillInIntent = new Intent();

                //Get uri and set on Intent
                Uri stockUri = Contract.Quote.makeUriForStock(symbol);
                fillInIntent.setData(stockUri);

                remoteViews.setOnClickFillInIntent(R.id.list_item_widget, fillInIntent);

                return remoteViews;
            }


            @Override
            public RemoteViews getLoadingView() {
                Timber.d("StockWidgetRemoteViewService:getLoadingView: ");
//                return null;
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                Timber.d("StockWidgetRemoteViewService:getViewTypeCount: ");
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position)) {
                    Timber.d("StockWidgetRemoteViewService:getItemId: position is %s", position);
                    return cursor.getLong(Contract.Quote.POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                Timber.d("StockWidgetRemoteViewService:hasStableIds: ");
                return true;
            }
        };
    }
}
