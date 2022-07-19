package com.example.weatherapp.ui.nextdayssummary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentNextDaysBinding
import com.example.weatherapp.ui.todayoverview.MainActivity

import android.content.Context
import android.content.Intent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.core.repositories.asString
import com.example.weatherapp.MyApplication
import com.example.weatherapp.core.utils.DataState
import javax.inject.Inject

class NextDaysFragment : Fragment() {

    interface OnItemClickedListener {
        fun onBackButtonClicked(item: MenuItem): Boolean
        fun setupToolbar(title: String)
    }

    private val sDummyCallbacks: OnItemClickedListener = object : OnItemClickedListener {
        override fun onBackButtonClicked(item: MenuItem): Boolean {
            return false
        }

        override fun setupToolbar(title: String) {
            return
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val nextDaysViewModel: NextDaysViewModel by lazy {
        ViewModelProvider(viewModelStore, viewModelFactory)[NextDaysViewModel::class.java]
    }
    private var onClickListener: OnItemClickedListener = sDummyCallbacks
    private lateinit var adapter: NextDaysAdapter
    private lateinit var binding: FragmentNextDaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        (requireActivity().application as MyApplication).appComponent.inject(this)
        if (context is MainActivity) {
            onClickListener = context
        }
    }

    private fun observe() {
        nextDaysViewModel.networkStatus.observe(viewLifecycleOwner) {
            if (it == DataState.LOADING) {
                setMainComponentsVisibility(false)
                setBufferingSpinnerVisibility(true)
            }

            if (it == DataState.SUCCESS) {
                setMainComponentsVisibility(true)
                setBufferingSpinnerVisibility(false)
            }
        }

        nextDaysViewModel.nextDaysData.observe(viewLifecycleOwner) {
            adapter.weatherData = it
        }

        nextDaysViewModel.todayBigCardData.observe(viewLifecycleOwner) {
            if (it.first == null || it.second == null || it.third == null) {
                return@observe
            }
            adapter.userPreferences = it.third!!
        }

        nextDaysViewModel.locationData.observe(viewLifecycleOwner) {
            nextDaysViewModel.provideWeatherData(it)
            onClickListener.setupToolbar(it.asString())
        }

    }

    private fun setBufferingSpinnerVisibility(visible: Boolean) {
        if (visible) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = INVISIBLE
        }
    }

    private fun setMainComponentsVisibility(visible: Boolean) {
        if (visible) {
            binding.recyclerView.visibility = VISIBLE
        } else {
            binding.recyclerView.visibility = INVISIBLE
        }
    }

    private fun setupRecycleView() {
        val onItemClicked: (Int) -> Unit = {
            val intent = Intent(context, IntraDayWeatherActivity::class.java)
            intent.putExtra("itemIndex", it)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        }

        adapter = NextDaysAdapter(
            onItemClicked
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }
}