package com.example.textexplainer

import android.content.Context
import android.content.SharedPreferences
import kotlin.math.max
import kotlin.math.min

object Settings {
    private const val PREFS = "textexplainer_prefs"

    private const val KEY_LANG = "lang"
    private const val KEY_MAX_TOKENS = "max_tokens"
    private const val KEY_TEMPERATURE = "temperature"
    private const val KEY_WORD_LIMIT = "word_limit"

    private const val DEF_LANG = "fa"
    private const val DEF_MAX_TOKENS = 600
    private const val DEF_TEMPERATURE = 0.3f
    private const val DEF_WORD_LIMIT = 100

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getLang(ctx: Context) = prefs(ctx).getString(KEY_LANG, DEF_LANG) ?: DEF_LANG
    fun setLang(ctx: Context, v: String) = prefs(ctx).edit().putString(KEY_LANG, v).apply()

    fun getMaxTokens(ctx: Context) = prefs(ctx).getInt(KEY_MAX_TOKENS, DEF_MAX_TOKENS)
    fun setMaxTokens(ctx: Context, v: Int) =
        prefs(ctx).edit().putInt(KEY_MAX_TOKENS, v.coerceIn(64, 4096)).apply()

    fun getTemperature(ctx: Context) = prefs(ctx).getFloat(KEY_TEMPERATURE, DEF_TEMPERATURE)
    fun setTemperature(ctx: Context, v: Float) =
        prefs(ctx).edit().putFloat(KEY_TEMPERATURE, v.coerceIn(0f, 1f)).apply()

    fun getWordLimit(ctx: Context) = prefs(ctx).getInt(KEY_WORD_LIMIT, DEF_WORD_LIMIT)
    fun setWordLimit(ctx: Context, v: Int) =
        prefs(ctx).edit().putInt(KEY_WORD_LIMIT, max(10, min(5000, v))).apply()
}
