package com.example.weatherapp.ui.todayoverview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.core.models.HourWeatherModel
import java.util.*

class HourlyListAdapter(
    private val hourlyTempList: List<HourWeatherModel>
) : RecyclerView.Adapter<HourlyListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val hourTextView: TextView =
            view.findViewById(R.id.hourly_card_title)
        val weatherImageView: ImageView =
            view.findViewById(R.id.hourly_card_icon)
        val temperatureTextView: TextView =
            view.findViewById(R.id.hourly_card_temp)
        val cardContainer: View =
            view.findViewById(R.id.hourly_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_card_item, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.cardContainer.setOnClickListener {
            val position = viewHolder.adapterPosition
            Toast.makeText(parent.context, position.toString(), Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHourIn24Format: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val item = hourlyTempList[position]

        if (currentHourIn24Format == position) {
            holder.hourTextView.text = "Now"
        } else {
            holder.hourTextView.text = item.time
        }

        holder.weatherImageView.setImageResource(item.weatherType.imgSrc)
        val tempVal = item.tempC.toString() + "°"
        holder.temperatureTextView.text = tempVal
    }

    override fun getItemCount(): Int = hourlyTempList.size
}