package com.example.weatherapp.ui.nextdayssummary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.databinding.DayTemperatureCardItemBinding
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel.Companion.getMaxTempPref
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel.Companion.getMinTempPref
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class NextDaysAdapter(
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<NextDaysAdapter.ViewHolder>() {

    var weatherData: List<DayWeatherModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(private var binding: DayTemperatureCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val DEFAULT_CARD_POS = 12
        val formatter = DateTimeFormatter.ofPattern("dd MMM")

        fun bind(
            dayWeatherObject: DayWeatherModel,
            position: Int,
            onItemClicked: (Int) -> Unit
        ) {
            with(binding) {
                val defaultWeatherObject = dayWeatherObject.hourlyWeatherList[DEFAULT_CARD_POS]
                val dayOfMonth =
                    dayWeatherObject.date.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    )
                val dateFormatted = dayWeatherObject.date.format(formatter)
                val dateComposed = "$dayOfMonth, $dateFormatted"
                val tempSummary =
                    "${getMaxTempPref(defaultWeatherObject,false)}/${getMinTempPref(defaultWeatherObject, false)}"

                nextDaysIcon.setImageResource(defaultWeatherObject.weatherType.imgSrc)
                nextDaysDate.text = dateComposed.trim()
                nextDayTemp.text = tempSummary.trim()
                nextDayItem.setOnClickListener {
                    onItemClicked(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DayTemperatureCardItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            weatherData[position],
            position,
            onItemClicked
        )
    }

    override fun getItemCount(): Int = weatherData.size

}