package com.example.weatherapp.ui.todayoverview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.core.models.HourWeatherModel
import com.google.android.material.card.MaterialCardView
import java.util.*

class HourlyListAdapter(
    private val hourlyTempList: List<HourWeatherModel>,
    private val updateMethod: (Int) -> Unit,
    private val selectedIndex: LiveData<Int>,
    private val context: Context,
    private val displayNow: Boolean
) : RecyclerView.Adapter<HourlyListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourTextView: TextView =
            view.findViewById(R.id.hourly_card_title)
        val temperatureTextView: TextView =
            view.findViewById(R.id.hourly_card_temp)
        val weatherImageView: ImageView =
            view.findViewById(R.id.hourly_card_icon)
        val cardContainer: MaterialCardView =
            view.findViewById(R.id.hourly_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_card_item, parent, false)
        val viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHourIn24Format: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val item = hourlyTempList[position]

        if (displayNow && currentHourIn24Format == position) {
            holder.hourTextView.text = context.getString(R.string.now)
        } else {
            holder.hourTextView.text = item.time
        }

        if (position == selectedIndex.value) {
            holder.cardContainer.setCardBackgroundColor(context.resources.getColor(R.color.main_card_color))
            holder.hourTextView.setTextColor(context.resources.getColor(R.color.small_card_color))
            holder.temperatureTextView.setTextColor(context.resources.getColor(R.color.small_card_color))
        } else {
            holder.cardContainer.setCardBackgroundColor(context.resources.getColor(R.color.small_card_color))
            holder.hourTextView.setTextColor(context.resources.getColor(R.color.grey))
            holder.temperatureTextView.setTextColor(context.resources.getColor(R.color.grey))
        }

        holder.cardContainer.setOnClickListener {
            val position = holder.adapterPosition
            holder.cardContainer
            notifyItemChanged(selectedIndex.value!!)
            updateMethod(position)
            notifyItemChanged(selectedIndex.value!!)
        }

        holder.weatherImageView.setImageResource(item.weatherType.imgSrc)
        val tempVal = item.tempC.toString() + "°"
        holder.temperatureTextView.text = tempVal
    }

    override fun getItemCount(): Int = hourlyTempList.size
}