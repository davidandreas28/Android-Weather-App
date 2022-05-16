package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.models.HourlyTemperatureModel
import java.util.*

class HourlyListAdapter(
    private val hourlyTempList: List<LiveData<HourlyTemperatureModel>>
) : RecyclerView.Adapter<HourlyListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val hourTextView: TextView =
            view.findViewById(R.id.hourly_card_title)
        val weatherImageView: ImageView =
            view.findViewById(R.id.hourly_card_image)
        val temperatureTextView: TextView =
            view.findViewById(R.id.hourly_card_temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_card_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHourIn24Format: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val item = hourlyTempList[position].value!!

        if (currentHourIn24Format == position) {
            holder.hourTextView.text = "Now"
        } else {
            holder.hourTextView.text = item.hour
        }

        holder.weatherImageView.setImageResource(item.iconSrc)
        holder.temperatureTextView.text = item.temp.toString()
    }

    override fun getItemCount(): Int = hourlyTempList.size
}