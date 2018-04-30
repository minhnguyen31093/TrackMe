package com.github.minhnguyen31093.trackme.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.minhnguyen31093.trackme.R
import com.github.minhnguyen31093.trackme.model.Record
import com.github.minhnguyen31093.trackme.model.RecordTime
import com.github.minhnguyen31093.trackme.utils.MapUtils
import kotlinx.android.synthetic.main.item_record_location.view.*

class RecordLocationAdapter(private var items: ArrayList<Record>) : RecyclerView.Adapter<RecordLocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record_location, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun add(newItems: List<Record>) {
        items.addAll(newItems)
        notifyItemRangeInserted(items.size - newItems.size, newItems.size)
    }

    fun addNew(item: Record) {
        items.add(0, item)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Record) {
            val context = itemView.context
            val km = context.getString(R.string.format_km, MapUtils.round(item.distance))
            val kmH = context.getString(R.string.format_km_h, MapUtils.round(item.avgSpeed))
            val recordTime = RecordTime.convertToRecordTime(item.recordTime)
            itemView.tvDistance.text = context.getString(R.string.format_distance, km)
            itemView.tvSpeed.text = context.getString(R.string.format_avg_speed, kmH)
            itemView.tvTime.text = context.getString(R.string.format_time,
                    MapUtils.numberToString(recordTime.hours),
                    MapUtils.numberToString(recordTime.minutes),
                    MapUtils.numberToString(recordTime.seconds))

            Log.d("Map Image", MapUtils.getStaticMapImage(Record.convertPointsToObject(item.points)))
            Glide.with(itemView.context).load(item.mapImage).into(itemView.ivLocation)
        }
    }
}