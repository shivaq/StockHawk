package com.yasuaki.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.yasuaki.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY_DATE = "history_date";
        public static final String COLUMN_HISTORY_CLOSING_PRICE = "history_closing_price";
        public static final String COLUMN_DAY_HIGH = "day_high";
        public static final String COLUMN_DAY_LOW = "day_low";
        public static final String COLUMN_YEAR_HIGH = "year_high";
        public static final String COLUMN_YEAR_LOW = "year_low";
        public static final String COLUMN_EPS = "eps";
        public static final String COLUMN_ROE = "roe";

        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final int POSITION_HISTORY_DATE = 5;
        public static final int POSITION_HISTORY_CLOSING_PRICE = 6;
        public static final int POSITION_DAY_HIGH = 7;
        public static final int POSITION_DAY_LOW = 8;
        public static final int POSITION_YEAR_HIGH = 9;
        public static final int POSITION_YEAR_LOW = 10;
        public static final int POSITION_EPS = 11;
        public static final int POSITION_ROE = 12;

        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY_DATE,
                COLUMN_HISTORY_CLOSING_PRICE,
                COLUMN_DAY_HIGH,
                COLUMN_DAY_LOW,
                COLUMN_YEAR_HIGH,
                COLUMN_YEAR_LOW,
                COLUMN_EPS,
                COLUMN_ROE
        );

        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
