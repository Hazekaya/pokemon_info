package com.hazekaya.pokemon_widget

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreference: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)

        if (!sharedPreference.contains("widget_lang")) {
            val defaultLang = "ja"
            val editor = sharedPreference.edit()
            editor.putString("widget_lang", defaultLang)
            editor.apply()
        }

        val settingsFragment = WidgetSettingsFragment()
        changeFragment(settingsFragment)
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}