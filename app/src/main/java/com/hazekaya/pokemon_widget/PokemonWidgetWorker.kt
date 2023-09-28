package com.hazekaya.pokemon_widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Random

class PokemonWidgetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val pokemonApiClient = ApiClient().createRetroFitInstance()
    override fun doWork(): Result {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pokemon = getRandomPokemon()
                    updateWidgetContent(context, pokemon)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun updateWidgetContent(context: Context, pokemon: PokemonModel?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetProvider = ComponentName(context, PokemonWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider)
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        if (pokemon != null) {
            appWidgetIds.forEach { appWidgetId ->
                val resourceId =
                    context.resources.getIdentifier(
                        "pokemon_${pokemon?.id}",
                        "drawable",
                        context.packageName
                    )

                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val language = sharedPreferences.getString("widget_lang", "").toString()

                var name: String = pokemon?.name.toString()

                if (language != "en") {
                    pokemon.names?.let { names ->
                        for (pokemonName in names) {
                            if (pokemonName.language?.name == language) {
                                name = pokemonName.name.toString()
                            }
                        }
                    }
                }

                views.setTextViewText(R.id.widget_text_view, name)
                views.setImageViewResource(R.id.pokemon_image_view, resourceId)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        } else {
            val name = "だれだ？"
            val resourceId = R.drawable.pokemon_0

            appWidgetIds.forEach { appWidgetId ->
                views.setTextViewText(R.id.widget_text_view, name)
                views.setImageViewResource(R.id.pokemon_image_view, resourceId)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private suspend fun getRandomPokemon(): PokemonModel? {
        return try {
            val randomPokemon = CoroutineScope(Dispatchers.IO).async {
                val call = pokemonApiClient.getAllPokemon()
                val response = call.execute()
                var pokemonArr: List<PokemonModel>? = null

                if (response.isSuccessful) {
                    pokemonArr = response.body()?.results
                }

                if (pokemonArr != null) {
                    val count = pokemonArr.size
                    val randomIndex = Random().nextInt(count)
                    val randomPokemonName = pokemonArr[randomIndex].name ?: ""
                    val randomPokemonCall = pokemonApiClient.getPokemonByName(randomPokemonName)
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

            randomPokemon.await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}