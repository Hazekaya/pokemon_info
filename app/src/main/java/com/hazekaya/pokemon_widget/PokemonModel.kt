package com.hazekaya.pokemon_widget

data class PokemonModel(
    var id: Int? = 0,
    var name: String? = null,
    var url: String? = null,
    var names: ArrayList<PokemonName>? = null
)

data class PokemonName(
    var language: Language? = null,
    var name: String? = null
)

data class Language(
    var name: String? = null,
    var url: String? = null
)