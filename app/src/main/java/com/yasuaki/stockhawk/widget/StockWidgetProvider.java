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
import com.yasuaki.stockhawk.ui.MainActivity;

import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockWidgetProvider extends AppWidgetProvider {

    //ブロードキャストレシーバを受信した際にコールされる
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        //AppWidgetProvider のサブクラス名を渡して、対象Widget と紐付いたIDを取得
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        Timber.d("StockWidgetProvider:onReceive: appWidgetIds[0] is %s", appWidgetIds[0]);
        //onUpdate をコール
        this.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //パラメータで受け取ったIDの Widget たちに処理をしていく
        for (int appWidgetId : appWidgetIds) {


            Intent intentService = new Intent(context, StockWidgetRemoteViewService.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_stock_list);
            //AdapterView のIDと、そのAdapterView にdataを紐付けてくれるService のIntent とを渡す
            //※AdapterView のサブクラス →ListView, GridView, Spinner and Gallery
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intentService);

            boolean useDetailActivity = context.getResources()
                    .getBoolean(R.bool.use_detail_activity);

            Intent clickIntentTemplate = useDetailActivity
                    ? new Intent(context, GraphActivity.class)
                    : new Intent(context, MainActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.text_error_widget);
            Timber.d("StockWidgetProvider:onUpdate: appWidgetId is %s", appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
