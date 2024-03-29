package com.example.weatherapp.ui.nextdayssummary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentNextDaysBinding
import com.example.weatherapp.MainActivity

import android.content.Context
import android.content.Intent
import com.example.weatherapp.ui.todayoverview.HourlyListAdapter


class NextDaysFragment : Fragment() {

    interface OnItemClickedListener {
        fun onBackButtonClicked(item: MenuItem): Boolean
    }

    private val sDummyCallbacks: OnItemClickedListener = object : OnItemClickedListener {
        override fun onBackButtonClicked(item: MenuItem): Boolean {
            return false
        }
    }
    private var onClickListener: OnItemClickedListener = sDummyCallbacks
    private lateinit var adapter: NextDaysAdapter
    private lateinit var binding: FragmentNextDaysBinding
    private val nextDaysViewModel: NextDaysViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentNextDaysBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        observe()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onClickListener.onBackButtonClicked(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            onClickListener = context
        }
    }

    private fun observe() {
        nextDaysViewModel.nextDaysData.observe(viewLifecycleOwner) {
            adapter.weatherData = it
        }
    }

    private fun setupRecycleView() {
        val onItemClicked: (Int) -> Unit = {
            val intent = Intent(context, IntraDayWeatherActivity::class.java)
            intent.putExtra("itemIndex", it)
            startActivity(intent)
        }

        adapter = NextDaysAdapter(
            onItemClicked
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }
}