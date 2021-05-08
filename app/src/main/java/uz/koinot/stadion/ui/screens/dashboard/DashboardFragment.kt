package uz.koinot.stadion.ui.screens.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ir.farshid_roohi.linegraph.ChartEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.data.model.Dashboard
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.FragmentDashboardBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.showMessage

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _bn: FragmentDashboardBinding? = null
    val bn get() = _bn!!
    private val viewModel: DashboardViewModel by viewModels()
    private var stadiumId = 0
    private val adapter = OrderAdapter()
    private var ordersList = ArrayList<Order>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getInt(CONSTANTS.STADION_ID)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentDashboardBinding.bind(view)

        viewModel.getDashboard(stadiumId)

        collects()

        bn.rvOrders.apply {
            adapter = adapter
        }

    }

    private fun collects() {
        GlobalScope.launch {
            val orders = viewModel.getAllOrder()
            if (orders.isNotEmpty()) {
                viewModel.afterCreateFlow(stadiumId, orders[0].startDate)
                    ordersList.addAll(orders)
            } else {
                viewModel.archiveAll(stadiumId)
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.archiveAllFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        bn.rvOrders.isVisible = true
                        GlobalScope.launch {
                            viewModel.setAllOrder(it.data!!)
                        }
                        it.data?.let { it1 -> adapter.submitList(it1) }
                    }
                    is UiStateList.ERROR -> {
                        bn.rvOrders.isVisible = false
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING -> {
                        bn.rvOrders.isVisible = false
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.afterCreateFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        bn.rvOrders.isVisible = true
                        val list = ArrayList<Order>()
                        it.data?.let { it1 -> list.addAll(it1) }
                        list.addAll(ordersList)
                        adapter.submitList(list)
                        GlobalScope.launch {
                            viewModel.removeAllOrder()
                            viewModel.setAllOrder(list)
                        }
                    }
                    is UiStateList.ERROR -> {
                        bn.rvOrders.isVisible = false
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING -> {
                        bn.rvOrders.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.dashboardFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
//                        showMessage("Muvaffaqiyatli")
                        if (it.data != null && it.data.isNotEmpty()){
                            createChart(it.data)
                            bn.nothing.isVisible = false
                        }else{
                            bn.nothing.isVisible = true
                        }

                    }
                    is UiStateList.ERROR -> {
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING -> {
                        bn.nothing.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun createChart(list: List<Dashboard>) {

        val day = ArrayList<String>()
        val benefit = ArrayList<Float>()
        val count = ArrayList<Float>()
        list.forEach {
            day.add(it.day)
            benefit.add(it.benefit)
            count.add(it.count.toFloat())
        }

        val one = ChartEntity(Color.WHITE,benefit.toFloatArray())
        val two = ChartEntity(Color.YELLOW,count.toFloatArray())
        val list = ArrayList<ChartEntity>()
        list.add(one)
        list.add(two)
        bn.lineChart.isVisible = true
        bn.lineChart.legendArray = day.toArray() as Array<String>
        bn.lineChart.setList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}