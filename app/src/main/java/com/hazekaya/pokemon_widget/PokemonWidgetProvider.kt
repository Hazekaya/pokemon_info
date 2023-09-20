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
        for (appWidgetId in appWidgetIds) {
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, WidgetActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pokemon = getRandomPokemon()

                    updateWidgetContent(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        pokemon
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        scheduleWidgetUpdateWork(context)
    }

    private fun updateWidgetContent(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        pokemon: PokemonModel?
    ) {
        val resourceId =
            context.resources.getIdentifier(
                "pokemon_${pokemon?.id}",
                "drawable",
                context.packageName
            )
        val view = RemoteViews(context.packageName, R.layout.widget_layout)
        view.setTextViewText(R.id.widget_text_view, pokemon?.name)
        // Update the widget

        view.setImageViewResource(R.id.pokemon_image_view, resourceId)
        appWidgetManager.updateAppWidget(appWidgetId, view)
    }

    private suspend fun getRandomPokemon(): PokemonModel? {
        return try {
            val pokemonApiClient = ApiClient().createRetroFitInstance()

            val randomPokemon = CoroutineScope(Dispatchers.IO).async {
                val call = pokemonApiClient.getAllPokemon()
                val response = call.execute()
                var pokemonArr: List<PokemonModel>? = null

                if (response.isSuccessful) {
                    pokemonArr = response.body()?.results
                }

                if (pokemonArr != null) {
                    val count = pokemonArr.size
                    val randomIndex = (0 until count).random()
                    val randomPokemonName = pokemonArr[randomIndex].name ?: ""
                    val randomPokemonCall =
                        pokemonApiClient.getPokemonByName(randomPokemonName)
                    val randomPokemonResponse = randomPokemonCall.execute()

                    if (randomPokemonResponse.isSuccessful) {
                        randomPokemonResponse.body()
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            randomPokemon.await() // Wait for the result of the async coroutine
        } catch (e: Exception) {
            e.printStackTrace()
            null // Handle the exception and return null or handle the error as needed
        }
    }

    private fun scheduleWidgetUpdateWork(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val name = sharedPreferences.getString("widget_update_minutes", "")

        val duration: Long = name?.toLong() ?: 15

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        Log.d("update duration", duration.toString())

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