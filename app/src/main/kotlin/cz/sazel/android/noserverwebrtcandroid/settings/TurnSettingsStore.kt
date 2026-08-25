package cz.sazel.android.noserverwebrtcandroid.settings

import android.content.Context
import androidx.core.content.edit

/**
 * Keeps the TURN server the user entered in preferences, so it is still there on the next run.
 */
class TurnSettingsStore(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun load() = TurnSettings(
        host = preferences.getString(KEY_HOST, "").orEmpty(),
        username = preferences.getString(KEY_USERNAME, "").orEmpty(),
        password = preferences.getString(KEY_PASSWORD, "").orEmpty(),
    )

    fun save(settings: TurnSettings) = preferences.edit {
        putString(KEY_HOST, settings.host)
        putString(KEY_USERNAME, settings.username)
        putString(KEY_PASSWORD, settings.password)
    }

    private companion object {

        const val PREFERENCES = "turn_settings"
        const val KEY_HOST = "host"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
    }
}
