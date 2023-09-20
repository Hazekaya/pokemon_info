package com.hazekaya.pokemon_widget

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("pokemon/{name}")
    fun getPokemonByName(@Path("name") name: String): Call<PokemonModel>

    @GET("pokemon/{id}")
    fun getPokemonById(@Path("id") id: Int): Call<PokemonModel>

    @GET("pokemon/offset={offset}&limit={limit}")
    fun getPokemonList(@Path("offset") offset: Int, limit: Int): Call<PokemonModel>

    @GET("pokemon?offset=0&limit=2000")
    fun getAllPokemon(): Call<PokemonList>
}