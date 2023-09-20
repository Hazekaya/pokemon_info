package com.hazekaya.pokemon_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Exception
import java.util.Random

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
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.activity_main // Use the correct layout resource for your widget
            ).apply {
//                setOnClickPendingIntent(R.id.your_button_id, pendingIntent) // Configure the button or UI element
            }

            val pokemonApiClient = ApiClient().createRetroFitInstance()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val call = pokemonApiClient.getAllPokemon();
                    val response = call.execute()
                    var pokemonArr: List<PokemonModel>? = null
                    var pokemon: PokemonModel? = null
                    var count: Int = 0;

                    if (response.isSuccessful) {
                        pokemonArr = response.body()?.results
                    } else {
                    }

                    if (pokemonArr != null) {
                        count = pokemonArr.size
                        val randomPokemon: PokemonModel = pokemonArr[Random().nextInt(count) + 1]
                        val randomPokemonCall =
                            pokemonApiClient.getPokemonByName(randomPokemon.name.toString())
                        val randomPokemonResponse = randomPokemonCall.execute()

                        if (randomPokemonResponse.isSuccessful) {
                            pokemon = randomPokemonResponse.body()
                        }
                    }

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
        val view = RemoteViews(context.packageName, R.layout.activity_main)
        view.setTextViewText(R.id.widget_text_view, pokemon?.name)
        view.setImageViewResource(R.id.pokemon_image_view, resourceId)
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, view)
    }

    private fun getRandomPokemon() {

    }

    private fun getAllPokemon(): List<PokemonModel>? {
        return null;
    }
}