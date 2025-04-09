package com.techsavvy.showcaseme.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Helpers @Inject constructor(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String = ""): String {
        return pref.getString(key, default) ?: default
    }

    fun saveBoolean(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return pref.getBoolean(key, default)
    }

    fun saveInt(key: String, value: Int) {
        pref.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, default: Int = 0): Int {
        return pref.getInt(key, default)
    }

    fun clearAll() {
        pref.edit().clear().apply()
    }

    fun remove(key: String) {
        pref.edit().remove(key).apply()
    }
}
