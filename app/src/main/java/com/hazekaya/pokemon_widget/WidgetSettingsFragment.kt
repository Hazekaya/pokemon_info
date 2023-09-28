package com.hazekaya.pokemon_widget

import android.os.Bundle
import android.text.InputType
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import kotlin.math.max

class WidgetSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.widget_settings, rootKey)


        // settings for how often the widget should update.
        val updateWidgetTimedPref: EditTextPreference? = findPreference("widget_update_minutes")
        updateWidgetTimedPref?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        updateWidgetTimedPref?.setOnPreferenceChangeListener { preference, newValue ->
            val updatedValue = max(newValue.toString().toInt(), 15).toString()
            val sharedPreferences = preference.sharedPreferences
            val editor = sharedPreferences?.edit()
            editor?.putString("widget_update_minutes", updatedValue)
            editor?.apply()
            true
        }

        // setting for language in which the pokemon name is shown.
        val updateWidgetLangPref: DropDownPreference? = findPreference("widget_update_lang")

        updateWidgetLangPref?.setOnPreferenceChangeListener { preference, newValue ->
            val updatedValue = newValue.toString()
            val sharedPreference = preference.sharedPreferences
            val editor = sharedPreference?.edit()
            editor?.putString("widget_lang", updatedValue)
            editor?.apply()
            true
        }
    }
}