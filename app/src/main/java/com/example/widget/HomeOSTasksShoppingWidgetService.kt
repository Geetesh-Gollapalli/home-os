package com.example.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.R
import com.example.data.db.AppDatabase

class HomeOSTasksShoppingWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return HomeOSTasksShoppingViewsFactory(applicationContext)
    }
}

class HomeOSTasksShoppingViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    data class WidgetItem(
        val id: Long,
        val title: String,
        val detail: String,
        val isTask: Boolean,
        val route: String
    )

    private val items = mutableListOf<WidgetItem>()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    private fun loadData() {
        items.clear()
        try {
            val db = AppDatabase.getDatabase(context)
            val pendingTasks = db.taskDao().getPendingTasksSync()
            val activeShopping = db.shoppingDao().getActiveItemsSync()

            // Interleave or list tasks first, then shopping
            pendingTasks.forEach { task ->
                val timeInfo = if (task.timeLabel.isNotBlank()) task.timeLabel else "Pending task"
                items.add(
                    WidgetItem(
                        id = task.id,
                        title = task.title,
                        detail = timeInfo,
                        isTask = true,
                        route = "tasks"
                    )
                )
            }

            activeShopping.forEach { shop ->
                val qtyInfo = if (shop.quantity.isNotBlank()) "${shop.quantity} • ${shop.category}" else shop.category
                items.add(
                    WidgetItem(
                        id = shop.id,
                        title = shop.title,
                        detail = qtyInfo,
                        isTask = false,
                        route = "shopping"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        items.clear()
    }

    override fun getCount(): Int = items.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= items.size) return null
        val item = items[position]

        val views = RemoteViews(context.packageName, R.layout.widget_item_task_shopping)

        views.setTextViewText(R.id.widget_item_title, item.title)
        views.setTextViewText(R.id.widget_item_subtitle, item.detail)

        if (item.isTask) {
            views.setTextViewText(R.id.widget_item_badge, "TASK")
            views.setTextColor(R.id.widget_item_badge, Color.parseColor("#5B5BD6"))
            views.setTextColor(R.id.widget_item_status, Color.parseColor("#5B5BD6"))
        } else {
            views.setTextViewText(R.id.widget_item_badge, "BUY")
            views.setTextColor(R.id.widget_item_badge, Color.parseColor("#2E7D32"))
            views.setTextColor(R.id.widget_item_status, Color.parseColor("#2E7D32"))
        }

        // FillInIntent for the list item click
        val fillInIntent = Intent().apply {
            putExtra("navigate_route", item.route)
            putExtra("item_id", item.id)
        }
        views.setOnClickFillInIntent(R.id.widget_item_root, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
