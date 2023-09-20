package com.hazekaya.pokemon_widget

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class WidgetSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.widget_settings, rootKey)

        val updateWidgetPref: EditTextPreference? = findPreference("widget_update_minutes")
        updateWidgetPref?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        updateWidgetPref?.setOnPreferenceChangeListener { preference, newValue ->
            val updatedValue = newValue.toString()
            val sharedPreferences = preference.sharedPreferences
            val editor = sharedPreferences?.edit()
            editor?.putString("widget_update_minutes", updatedValue)
            editor?.apply()
            true
        }
    }
}