package com.yasuaki.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
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
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
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
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }


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

                remoteViews.setTextViewText(R.id.symbol_widget, symbol);
                remoteViews.setTextViewText(R.id.price_widget, price);
                remoteViews.setTextViewText(R.id.change_widget, change);

                if (rawAbsoluteChange > 0) {
                    remoteViews.setInt(R.id.change_widget, "setBackgroundResource",
                            R.drawable.percent_change_pill_green);
                } else {
                    remoteViews.setInt(R.id.change_widget, "setBackgroundResource",
                            R.drawable.percent_change_pill_red);
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Intent.EXTRA_TEXT, symbol);

                remoteViews.setOnClickFillInIntent(R.id.list_item_widget, fillInIntent);

                return remoteViews;
            }


            @Override
            public RemoteViews getLoadingView() {
//                return null;
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position)) {
                    return cursor.getLong(Contract.Quote.POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
