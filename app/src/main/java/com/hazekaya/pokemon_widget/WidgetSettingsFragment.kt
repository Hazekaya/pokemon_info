package com.hazekaya.pokemon_widget

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class WidgetSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.widget_pokemon, rootKey)
    }
}