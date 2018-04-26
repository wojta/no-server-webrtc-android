package cz.sazel.android.serverlesswebrtcandroid.console

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import cz.sazel.android.serverlesswebrtcandroid.adapters.ConsoleAdapter
import java.util.*

/**
 * Created on 7.5.16.
 */
class RecyclerViewConsole(val view: RecyclerView) : IConsole {

    lateinit var lines: ArrayList<String>
    private val SAVE_LINES = "SAVE_LINES"

    lateinit private var handler: Handler

    override fun printf(text: String, vararg args: Any) {
        handler.post {
            lines.add(String.format(Locale.getDefault(), text, args))
            view.adapter.notifyItemInserted(lines.size - 1)
            view.smoothScrollToPosition(lines.size - 1)
        }
    }

    override fun printf(resId: Int, vararg args: Any) {
        printf(view.context.getString(resId, args))
    }


    fun onSaveInstanceState(bundle: Bundle) {
        bundle.putStringArrayList(SAVE_LINES, lines)
    }


    fun initialize(bundle: Bundle?) {
        lines = bundle?.getStringArrayList(SAVE_LINES) ?: ArrayList()
        view.adapter = ConsoleAdapter(lines)
        handler = Handler(Looper.getMainLooper())
    }


}