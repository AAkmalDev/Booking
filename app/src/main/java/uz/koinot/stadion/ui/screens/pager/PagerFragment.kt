package uz.koinot.stadion.ui.screens.pager

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.PagerAdapter
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.FragmentPagerBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.Utils


@AndroidEntryPoint
class PagerFragment : Fragment(R.layout.fragment_pager) {

    private var _bn: FragmentPagerBinding? = null
    val bn get() = _bn!!
    private lateinit var adapter : PagerAdapter
    private lateinit var stadium:Stadium
    private lateinit var navController:NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arg = arguments?.getString(CONSTANTS.STADION,"")
        stadium = Gson().fromJson(arg, Stadium::class.java)
        Log.d("AAA","pager stadiumId: ${stadium.id}")
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentPagerBinding.bind(view)

        bn.toolbar.apply {
            title = stadium.name
            setNavigationOnClickListener {
                navController.navigateUp()
            }
            setOnMenuItemClickListener { item ->
                val bundle = Bundle()
                bundle.putLong(CONSTANTS.STADION_ID, stadium.id)
                when (item!!.itemId) {
                    R.id.action_search -> {
                        bundle.putString("key_search", "search")
                        navController.navigate(R.id.action_pagerFragment_to_filterFragment,
                            bundle,
                            Utils.navOptions())
                        Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
                    }
                    R.id.action_favorite -> {
                        bundle.putString("key_search", "short")
                        navController.navigate(R.id.action_pagerFragment_to_filterFragment,
                            bundle,
                            Utils.navOptions())
                        Toast.makeText(requireContext(), "qisqa", Toast.LENGTH_SHORT).show()
                    }
                    R.id.action_notification -> {
                        bundle.putString("key_search", "filter")
                        navController.navigate(R.id.action_pagerFragment_to_filterFragment,
                            bundle,
                            Utils.navOptions())
                        Toast.makeText(requireContext(), "notification", Toast.LENGTH_SHORT).show()
                    }
                }
                false
            }
        }
        adapter = PagerAdapter(this, stadium.id)
        bn.viewPager.adapter = adapter
        bn.tabLayout.setTabTextColors(resources.getColor(R.color.lightGray),
            resources.getColor(R.color.colorPrimaryDarkE))
        TabLayoutMediator(bn.tabLayout, bn.viewPager) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.orders)
            } else if (position == 1) {
                tab.text = getString(R.string.dashboard)
            } else {
                tab.text = getString(R.string.cancel)
            }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}
