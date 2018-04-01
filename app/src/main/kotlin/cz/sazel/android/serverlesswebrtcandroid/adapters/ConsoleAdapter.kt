package cz.sazel.android.serverlesswebrtcandroid.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import cz.sazel.android.serverlesswebrtcandroid.R
import org.jetbrains.anko.find
import org.jetbrains.anko.onLongClick

/**
 * This is just to do the printing into the RecyclerView.
 */
class ConsoleAdapter(val items: List<String>) : RecyclerView.Adapter<ConsoleAdapter.ConsoleVH>() {

    override fun onBindViewHolder(holder: ConsoleVH, position: Int) {
        holder.tvText.text = Html.fromHtml(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.l_item, parent, false)
        return ConsoleVH(view)
    }

    override fun getItemCount(): Int = items.count()

    class ConsoleVH(view: View) : RecyclerView.ViewHolder(view) {
        var tvText: TextView = view.find(R.id.tvText)

        init {
            tvText.onLongClick { //clipboard on long touch
                val text = tvText.text.toString()
                if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    val clipboard = tvText.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager;
                    clipboard.text = text;
                } else {
                    val clipboard = tvText.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager;
                    clipboard.primaryClip = ClipData.newPlainText("text", text);
                }
                Toast.makeText(tvText.context, R.string.clipboard_copy, LENGTH_SHORT).show()
                true
            }
        }


    }
}


