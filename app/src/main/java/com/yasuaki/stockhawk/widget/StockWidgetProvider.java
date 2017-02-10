package com.yasuaki.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.yasuaki.stockhawk.R;
import com.yasuaki.stockhawk.ui.GraphActivity;

import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        this.onUpdate(context, appWidgetManager, appWidgetIds);
        Timber.d("StockWidgetProvider:onReceive: ");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent intentService = new Intent(context, StockWidgetRemoteViewService.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_stock_list);
            
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intentService);

            Intent clickIntentTemplate =  new Intent(context, GraphActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Timber.d("StockWidgetProvider:onUpdate: clickPendingIntentTemplate is %s", clickPendingIntentTemplate);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.text_error_widget);
            Timber.d("StockWidgetProvider:onUpdate: ");
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
