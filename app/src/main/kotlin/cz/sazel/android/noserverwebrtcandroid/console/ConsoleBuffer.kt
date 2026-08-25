package cz.sazel.android.noserverwebrtcandroid.console

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

/**
 * Console that only accumulates the printed lines and publishes them as immutable snapshots,
 * so it can live in a view model and outlive the views that render it.
 */
class ConsoleBuffer(private val context: Context) : IConsole {

    private val _lines = MutableStateFlow(emptyList<String>())

    /** All lines printed so far, in the order they were printed. */
    val lines = _lines.asStateFlow()

    override fun printf(text: String, vararg args: Any) {
        val line = String.format(Locale.getDefault(), text, *args)
        _lines.update { it + line }
    }

    override fun printf(resId: Int, vararg args: Any) = printf(context.getString(resId, *args))
}
