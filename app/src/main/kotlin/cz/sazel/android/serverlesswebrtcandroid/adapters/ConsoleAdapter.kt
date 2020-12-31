package cz.sazel.android.serverlesswebrtcandroid.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.RecyclerView
import cz.sazel.android.serverlesswebrtcandroid.R

/**
 * This is just to do the printing into the RecyclerView.
 */
class ConsoleAdapter(val items: List<String>) : RecyclerView.Adapter<ConsoleAdapter.ConsoleVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.l_item, parent, false)
        return ConsoleVH(view)
    }


    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ConsoleVH, position: Int) {
        holder.tvText.text = Html.fromHtml(items[position])
    }

    class ConsoleVH(view: View) : RecyclerView.ViewHolder(view) {
        var tvText: TextView = view.findViewById(R.id.tvText)

        init {
            tvText.setOnLongClickListener {
                //clipboard on long touch
                val text = tvText.text.toString()
                val clipboard = tvText.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("text", text))
                Toast.makeText(tvText.context, R.string.clipboard_copy, LENGTH_SHORT).show()
                true
            }
        }
    }
}


