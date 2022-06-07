package com.example.weatherapp.ui.todayoverview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.databinding.HourlyCardItemBinding
import java.util.*

class HourlyListAdapter(
    private val onItemClicked: (Int) -> Unit,
    private val displayNow: Boolean
) : RecyclerView.Adapter<HourlyListAdapter.ViewHolder>() {

    var selectedIndex: Int? = null
        set(value) {
            field?.let {
                notifyItemChanged(it)
            }
            field = value
            field?.let {
                notifyItemChanged(it)
            }
        }

    var hourlyTempList: List<HourWeatherModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(private var binding: HourlyCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            viewHolderItem: HourWeatherModel,
            displayNow: Boolean,
            position: Int,
            selectedIndex: Int?,
            onItemClicked: (Int) -> Unit
        ) {
            val currentHourIn24Format: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val context = itemView.context

            with(binding) {
                if (displayNow && currentHourIn24Format == position) {
                    hourlyCardTitle.text = context.getString(R.string.now)
                } else {
                    hourlyCardTitle.text = viewHolderItem.time
                }

                val tempString = DetailedWeatherViewModel.getTempPref(viewHolderItem, false)
                hourlyCardTemp.text = tempString
                hourlyCardIcon.setImageResource(viewHolderItem.weatherType.imgSrc)
                hourlyCard.setOnClickListener {
                    onItemClicked(position)
                }
                hourlyCardIcon.setImageResource(viewHolderItem.weatherType.imgSrc)
                selectedIndex?.let {
                    if (position == it) {
                        hourlyCard.setCardBackgroundColor(context.resources.getColor(R.color.main_card_color))
                        hourlyCardTitle.setTextColor(context.resources.getColor(R.color.small_card_color))
                        hourlyCardTemp.setTextColor(context.resources.getColor(R.color.small_card_color))
                    } else {
                        hourlyCard.setCardBackgroundColor(context.resources.getColor(R.color.small_card_color))
                        hourlyCardTitle.setTextColor(context.resources.getColor(R.color.grey))
                        hourlyCardTemp.setTextColor(context.resources.getColor(R.color.grey))
                    }
                } ?: run {
                    hourlyCard.setCardBackgroundColor(context.resources.getColor(R.color.small_card_color))
                    hourlyCardTitle.setTextColor(context.resources.getColor(R.color.grey))
                    hourlyCardTemp.setTextColor(context.resources.getColor(R.color.grey))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HourlyCardItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            hourlyTempList[position],
            displayNow,
            position,
            selectedIndex,
            onItemClicked
        )
    }

    override fun getItemCount(): Int = hourlyTempList.size
}