package com.hazekaya.pokemon_widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import java.util.Calendar

class AlarmHandler(private val context: Context) {
    fun setAlarmManager() {
        val intent: Intent = Intent(context, PokemonWidgetProvider::class.java)
        var sender: PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sender = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            sender = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar: Calendar = Calendar.getInstance()

        val timeMili = calendar.timeInMillis + 10000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when {
                alarmManager.canScheduleExactAlarms() -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeMili,
                        sender
                    )
                }

                else -> {
                    context.startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }

            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMili, sender)
        }

        if (alarmManager != null) {


        }
    }
}