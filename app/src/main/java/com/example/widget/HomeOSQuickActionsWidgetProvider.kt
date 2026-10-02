package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeOSQuickActionsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val ACTION_NAVIGATE_PREFIX = "com.example.homeos.widget.NAVIGATE_"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_home_os_quick_actions)

            // Current date label (e.g., "Thu, Oct 1")
            val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val dateStr = dateFormat.format(Date())
            views.setTextViewText(R.id.widget_status_text, dateStr)

            // 1. Header click -> open Home OS app
            val homeIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val homePendingIntent = PendingIntent.getActivity(
                context,
                100,
                homeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_header_container, homePendingIntent)

            // 2. Action: Call Family (navigates to Family screen or dialer)
            val callIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("navigate_route", "family")
            }
            val callPendingIntent = PendingIntent.getActivity(
                context,
                101,
                callIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_call, callPendingIntent)

            // 3. Action: WhatsApp (opens WhatsApp directly or prompt)
            val waIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com"))
            waIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val waPendingIntent = PendingIntent.getActivity(
                context,
                102,
                waIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_whatsapp, waPendingIntent)

            // 4. Action: Shopping List
            val shoppingIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("navigate_route", "shopping")
            }
            val shoppingPendingIntent = PendingIntent.getActivity(
                context,
                103,
                shoppingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_shopping, shoppingPendingIntent)

            // 5. Action: Tasks
            val tasksIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("navigate_route", "tasks")
            }
            val tasksPendingIntent = PendingIntent.getActivity(
                context,
                104,
                tasksIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_tasks, tasksPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, HomeOSQuickActionsWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, HomeOSQuickActionsWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
