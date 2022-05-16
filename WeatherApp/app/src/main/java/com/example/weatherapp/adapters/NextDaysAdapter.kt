package com.example.weatherapp.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.DayTemperatureModel

class NextDaysAdapter(
    private val dataset: List<DayTemperatureModel>
) : RecyclerView.Adapter<NextDaysAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val hourTextView: TextView =
            view.findViewById(R.id.hourly_card_title)
        val weatherImageView: ImageView =
            view.findViewById(R.id.hourly_card_image)
        val temperatureTextView: TextView =
            view.findViewById(R.id.hourly_card_temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}