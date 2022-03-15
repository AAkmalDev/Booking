package uz.koinot.stadion.ui.screens.order.orderhistory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentOrderHistoryBinding
import uz.koinot.stadion.ui.screens.order.userorder.UserOrderViewModel

@AndroidEntryPoint
class OrderHistoryFragment : Fragment(R.layout.fragment_order_history) {

    private val viewModel: UserOrderViewModel by viewModels()
    private var _bn: FragmentOrderHistoryBinding? = null
    private val binding get() = _bn!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bn = FragmentOrderHistoryBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getUserOrder().collect {

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}