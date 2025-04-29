package com.example.summarynews.db

import android.content.Context
import androidx.core.content.edit

class SesionManager {
    object SesionManager {
        private const val PREF_NAME = "sesion_pref"
        private const val KEY_EMAIL = "email"

        fun guardarSesion(context: Context, email: String) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit() { putString(KEY_EMAIL, email) }
        }

        fun obtenerEmail(context: Context): String? {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_EMAIL, null)
        }

        fun cerrarSesion(context: Context) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit() { remove(KEY_EMAIL) }
        }
    }

}