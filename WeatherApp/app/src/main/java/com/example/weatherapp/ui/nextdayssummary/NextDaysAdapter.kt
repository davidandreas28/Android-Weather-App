package com.example.weatherapp.ui.nextdayssummary

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.core.models.DayWeatherModel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class NextDaysAdapter(
    private val dataset: List<DayWeatherModel>
) : RecyclerView.Adapter<NextDaysAdapter.ViewHolder>() {

    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    val DEFAULT_CARD_POS = 12

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nextDayIcon: ImageView =
            view.findViewById(R.id.next_days_icon)
        val nextDayDate: TextView =
            view.findViewById(R.id.next_days_date)
        val nextDayTemp: TextView =
            view.findViewById(R.id.next_day_temp)
        val nextDayItem: View =
            view.findViewById(R.id.next_day_item)
        val context = view.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.day_temperature_card_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherObject = dataset[position]
        val defaultHourWeatherObject = weatherObject.hourlyWeatherList[DEFAULT_CARD_POS]
        val imgSrc = defaultHourWeatherObject.weatherType.imgSrc
        holder.nextDayIcon.setImageResource(imgSrc)

        val dayOfMonth =
            weatherObject.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val dateFormatted = weatherObject.date.format(formatter)
        val dateComposed = "$dayOfMonth, $dateFormatted"
        holder.nextDayDate.text = dateComposed

        val tempSummary =
            "${defaultHourWeatherObject.maxTempC}°/${defaultHourWeatherObject.minTempC}°"
        holder.nextDayTemp.text = tempSummary

        holder.nextDayItem.setOnClickListener {
            val intent = Intent(holder.context, IntraDayWeatherActivity::class.java)
            intent.putExtra("itemIndex", position)
            holder.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataset.size


}