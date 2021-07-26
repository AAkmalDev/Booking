package uz.koinot.stadion.ui.screens.order

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.databinding.FragmentOrderBinding
import uz.koinot.stadion.ui.screens.home.BaseDialog
import uz.koinot.stadion.utils.*


@AndroidEntryPoint
class OrderFragment : Fragment(R.layout.fragment_order) {

    private val viewModel: OrderViewModel by viewModels()
    private var _bn: FragmentOrderBinding? = null
    private val bn get() = _bn!!
    private val adapter = OrderAdapter()
    private  var stadiumId:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID,0)!!
        Log.d("AAA","fragment stadiumId: $stadiumId")

        viewModel.getOder(stadiumId)

        lifecycleScope.launchWhenCreated {
            viewModel.orderFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{

                        if(it.data != null && it.data.isNotEmpty())

                                adapter.submitList(it.data)

                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentOrderBinding.bind(view)

        bn.rvOrders.adapter = adapter
        bn.rvOrders.layoutManager = LinearLayoutManager(requireContext())


        bn.swipeRefresh.setOnRefreshListener {
            viewModel.getOder(stadiumId)
        }

        bn.btnAddOrder.setOnClickListener {
            findNavController().navigate(R.id.createOrderFragment, bundleOf(CONSTANTS.STADION_ID to stadiumId),Utils.navOptions())
        }

        collects()

        adapter.setOnAcceptListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.acceptOrder(it.id)
            }
        }

        adapter.setOnCancelListener {orderId->
            val dialog = BaseDialog(getString(R.string.cancel),getString(R.string.cancel_order))
            dialog.setOnDeleteListener {
                dialog.dismiss()
                viewModel.rejectOrder(orderId)
            }
            dialog.show(childFragmentManager,"asd")
        }

        adapter.setOnRejectListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.rejectOrder(it.id)
            }
        }
        adapter.setOnPhoneNumber1Listener {
            requireActivity().startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${if(it.contains("+")) it else "+$it"}")))
        }

        adapter.setOnPhoneNumber2Listener {
            requireActivity().startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${if(it.contains("+")) it else "+$it"}")))
        }

    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        showProgress(false)
                        if(it.data != null && it.data.isNotEmpty()){
                            bn.apply {
                                textNoOrder.isVisible = false
                                rvOrders.isVisible = true
                            }
                        }else{
                            bn.apply {
                                textNoOrder.isVisible = true
                                rvOrders.isVisible = false
                            }
                        }

                    }
                    is UiStateList.ERROR ->{
                        showProgress(false)
                        bn.apply {
                            textNoOrder.isVisible = true
                            rvOrders.isVisible = false
                        }
                    }
                    is UiStateList.LOADING ->{
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
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgress(false)
                        viewModel.getOder(stadiumId)
                    }
                    is UiStateObject.ERROR ->{
                        showProgress(false)
                        showMessage("Xatolik")
                    }
                    is UiStateObject.LOADING ->{
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.rejectFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgress(false)
                        viewModel.getOder(stadiumId)
                    }
                    is UiStateObject.ERROR ->{
                        showProgress(false)
                        showMessage(getString(R.string.error))
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