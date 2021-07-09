package io.agora.openlive.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.agora.openlive.AgoraConstant.VIDEO_DIMENSIONS
import io.agora.openlive.R
import java.util.*

class ResolutionAdapter(private val mContext: Context, var selected: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mItems = ArrayList<ResolutionItem>()
    private fun initData(context: Context) {
        val size: Int = VIDEO_DIMENSIONS.size
        val labels = context.resources.getStringArray(R.array.string_array_resolutions)
        for (i in 0 until size) {
            val item = ResolutionItem(labels[i])
            mItems.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.agora_item_resolution, parent, false)
        return ResolutionHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mItems[position]
        val content = (holder as ResolutionHolder).resolution
        content.text = item.label
        content.setOnClickListener {
            selected = position
            notifyDataSetChanged()
        }
        content.isSelected = position == selected
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class ResolutionHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var resolution: TextView = itemView.findViewById(R.id.resolution)

    }

    private class ResolutionItem(var label: String)

    init {
        initData(mContext)
    }
}