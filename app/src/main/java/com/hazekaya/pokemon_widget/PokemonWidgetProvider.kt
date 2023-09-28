// PokemonWidgetProvider.kt
package com.hazekaya.pokemon_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PokemonWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        scheduleWidgetUpdateWork(context)
    }

    private fun scheduleWidgetUpdateWork(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val name = sharedPreferences.getString("widget_update_minutes", "")

        val duration: Long = name?.toLong() ?: 15

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<PokemonWidgetWorker>(
            duration,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "widgetUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }
}