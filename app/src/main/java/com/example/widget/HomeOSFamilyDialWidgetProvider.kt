package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import com.example.util.CommunicationHelper

class HomeOSFamilyDialWidgetProvider : AppWidgetProvider() {

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
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_home_os_family_dial)

            // Open app on header click
            val homeIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("navigate_route", "family")
            }
            val homePendingIntent = PendingIntent.getActivity(
                context,
                200,
                homeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_dial_app_link, homePendingIntent)

            // Load contacts from DB dynamically
            var contact1Name = "Add Contact"
            var contact1Rel = "Family"
            var contact1Phone = ""

            var contact2Name = "Add Contact"
            var contact2Rel = "Family"
            var contact2Phone = ""

            try {
                val db = AppDatabase.getDatabase(context)
                val contacts = db.familyDao().getAllContactsSync()
                if (contacts.isNotEmpty()) {
                    contact1Name = contacts[0].name
                    contact1Rel = contacts[0].relationship
                    contact1Phone = contacts[0].phoneNumber
                }
                if (contacts.size > 1) {
                    contact2Name = contacts[1].name
                    contact2Rel = contacts[1].relationship
                    contact2Phone = contacts[1].phoneNumber
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Contact 1 UI
            views.setTextViewText(R.id.widget_contact_1_name, contact1Name)
            views.setTextViewText(R.id.widget_contact_1_rel, contact1Rel)

            val call1Intent = if (contact1Phone.isNotBlank()) {
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${CommunicationHelper.sanitizePhoneNumber(contact1Phone)}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else homeIntent

            val wa1Intent = if (contact1Phone.isNotBlank()) {
                val cleanPhone = CommunicationHelper.sanitizePhoneNumber(contact1Phone).removePrefix("+")
                Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else homeIntent

            views.setOnClickPendingIntent(
                R.id.widget_contact_1_btn_call,
                PendingIntent.getActivity(context, 201, call1Intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )
            views.setOnClickPendingIntent(
                R.id.widget_contact_1_btn_wa,
                PendingIntent.getActivity(context, 202, wa1Intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )

            // Contact 2 UI
            views.setTextViewText(R.id.widget_contact_2_name, contact2Name)
            views.setTextViewText(R.id.widget_contact_2_rel, contact2Rel)

            val call2Intent = if (contact2Phone.isNotBlank()) {
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${CommunicationHelper.sanitizePhoneNumber(contact2Phone)}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else homeIntent

            val wa2Intent = if (contact2Phone.isNotBlank()) {
                val cleanPhone = CommunicationHelper.sanitizePhoneNumber(contact2Phone).removePrefix("+")
                Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else homeIntent

            views.setOnClickPendingIntent(
                R.id.widget_contact_2_btn_call,
                PendingIntent.getActivity(context, 203, call2Intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )
            views.setOnClickPendingIntent(
                R.id.widget_contact_2_btn_wa,
                PendingIntent.getActivity(context, 204, wa2Intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
