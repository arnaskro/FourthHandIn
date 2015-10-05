package ark.viauc.mobi.fourthhandin;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Created by user on 9/30/2015.
 */
public class Widget_class extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            int currentWidgetId = appWidgetIds[i];

//            DateFormat df = new SimpleDateFormat("HH:mm:ss");
//            String timetext = df.format(new Date());
//            timetext = currentWidgetId + ")   " + timetext;

            SharedPreferences prefs = context.getSharedPreferences(Intro.CAMERA_PREFS, Intro.MODE_PRIVATE);
            String pictureTime = "Last picture taken: " + prefs.getString(Intro.LAST_PICTURE_TIME, "");
            Intent myIntent = new Intent(context, Intro.class);
            PendingIntent pending = PendingIntent.getActivity(context, 0, myIntent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.btn, pending);
            views.setTextViewText(R.id.update, pictureTime);

            appWidgetManager.updateAppWidget(currentWidgetId, views);
        }
    }
}
