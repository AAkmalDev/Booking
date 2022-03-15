package uz.koinot.stadion.ui.screens.order

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.FragmentFilterBinding
import uz.koinot.stadion.utils.*

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {


    private val viewModel: OrderViewModel by viewModels()
    private var _bn: FragmentFilterBinding? = null
    private val bn get() = _bn!!
    private val adapter = OrderAdapter()
    private var stadiumId: Long = 0
    private var module: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID)!!
        module = arguments?.getString("key_search")


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bn = FragmentFilterBinding.bind(view)

        bn.rvOrders.adapter = adapter
        bn.rvOrders.layoutManager = LinearLayoutManager(requireContext())

      bn.toolbar.setNavigationOnClickListener {
          findNavController().navigateUp()
      }

        when (module) {
            "search" -> {
                viewModel.getOder(stadiumId)

                lifecycleScope.launchWhenCreated {
                    viewModel.orderFlow.collect {
                        when (it) {
                            is UiStateList.SUCCESS -> {
                                if (it.data != null && it.data.isNotEmpty()) {
                                    adapter.submitList(it.data)
                                    bn.searchView.addTextChangedListener(object : TextWatcherWrapper() {
                                        override fun onTextChanged(
                                            s: CharSequence?,
                                            start: Int,
                                            before: Int,
                                            count: Int,
                                        ) {
                                            super.onTextChanged(s, start, before, count)
                                            val arrayList = ArrayList<Order>()
                                            for (order in it.data) {
                                                if (order.phoneNumber!!.contains(s!!))
                                                    arrayList.add(order)
                                            }
                                            adapter.submitList(arrayList)
                                        }
                                    })
                                }
                            }
                            else -> Unit
                        }
                    }
                }
                collects()
            }
            "short" -> {

            }
            "filter" -> {

            }
        }

        adapter.setOnAcceptListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.acceptOrder(it.id)
            }
        }

    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgress(false)
                        if (it.data != null && it.data.isNotEmpty()) {
                            bn.apply {
                                textNoOrder.isVisible = false
                                rvOrders.isVisible = true
                            }
                        } else {
                            bn.apply {
                                textNoOrder.isVisible = true
                                rvOrders.isVisible = false
                            }
                        }

                    }
                    is UiStateList.ERROR -> {
                        showProgress(false)
                        bn.apply {
                            textNoOrder.isVisible = true
                            rvOrders.isVisible = false
                        }
                    }
                    is UiStateList.LOADING -> {
                        showProgress(true)
                        bn.apply {
                            textNoOrder.isVisible = false
                            rvOrders.isVisible = false
                        }
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.acceptFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        viewModel.getOder(stadiumId)
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage("Xatolik")
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.rejectFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        viewModel.getOder(stadiumId)
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage(getString(R.string.error))
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showProgress(status: Boolean) {
        bn.progressBar.isVisible = status
    }

}