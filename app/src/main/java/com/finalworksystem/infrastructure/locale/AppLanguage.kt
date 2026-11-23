package com.finalworksystem.infrastructure.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Application language through the per-app language API.
 *
 * Android 13+ stores the choice in the system (it is also editable in the system
 * settings of the app); older versions keep it via AppCompat (`autoStoreLocales`).
 * Changing the language recreates the activity, so all resources are reloaded.
 */
object AppLanguage {
    val supported = listOf("en", "ru")

    fun set(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

    /** Applies a language chosen in an older app version unless the user picked one since. */
    fun applyLegacy(languageTag: String?) {
        if (languageTag.isNullOrBlank() || languageTag !in supported) {
            return
        }

        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            set(languageTag)
        }
    }
}
