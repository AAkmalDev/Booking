package uz.koinot.stadion.ui.screens.order.delete

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.FtsOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.DeleteOrderAdapter
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.FragmentOrderDeleteBinding
import uz.koinot.stadion.utils.*

@AndroidEntryPoint
class OrderDeleteFragment : Fragment(R.layout.fragment_order_delete) {

    private val viewModel: OrderDeleteViewModel by viewModels()
    private var _bn: FragmentOrderDeleteBinding? = null
    private val bn get() = _bn!!
    private val adapter = DeleteOrderAdapter()
    private  var stadiumId : Long = 0
    private lateinit var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID,0)!!
        Log.d("AAA","fragment stadiumId: $stadiumId")

        viewModel.getCancel(stadiumId)

        lifecycleScope.launchWhenCreated {
            viewModel.getCancelFlow.collect {
                when(it){
                    is UiStateList.SUCCESS -> {
                        if (it.data != null && it.data.isNotEmpty()) {
                            adapter.submitList(it.data)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentOrderDeleteBinding.bind(view)

        bn.rvDeleteOrders.adapter = adapter
        bn.rvDeleteOrders.layoutManager = LinearLayoutManager(requireContext())


        bn.swipeRefresh.setOnRefreshListener {
            viewModel.getCancel(stadiumId)
        }

        collects()

        adapter.setOnDeleteListener {
            order = it
            viewModel.orderDelete(it.id)
        }

    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getCancelFlow.collect {
                when(it){
                    is UiStateList.SUCCESS -> {
                        bn.swipeRefresh.isRefreshing = false
                        showProgress(false)
                    }
                    is UiStateList.ERROR ->{
                        showProgress(false)
                    }
                    is UiStateList.LOADING ->{
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderDeleteFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgress(false)
                        adapter.deleteItem(order)
                    }
                    is UiStateObject.ERROR ->{
                        showProgress(false)
                    }
                    is UiStateObject.LOADING ->{
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun showProgress(status:Boolean){
        bn.swipeRefresh.isRefreshing = false
        bn.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}