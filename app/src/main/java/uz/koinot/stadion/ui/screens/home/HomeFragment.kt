package uz.koinot.stadion.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.AuthActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.StadiumAdapter
import uz.koinot.stadion.app.App
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentHomeBinding
import uz.koinot.stadion.utils.*
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: HomeViewModel by viewModels()
    private var _bn: FragmentHomeBinding? = null
    private val bn get() = _bn!!
    private val adapter = StadiumAdapter()
    private var stadiumId = 0L
    private lateinit var navController: NavController
    private val REQUEST_CODE_GALLERY = 1

    @Inject
    lateinit var storage: LocalStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        viewModel.getAllStadium()

        lifecycleScope.launchWhenCreated {
            viewModel.stadiumFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        viewModel.removeAllStadium()
                        it.data?.let { it1 -> viewModel.setAllStadium(it1) }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentHomeBinding.bind(view)
        Utils.closeKeyboard(requireActivity())
        bn.swipeRefresh.setOnRefreshListener(this)

        bn.homeRv.adapter = adapter
        collects()

        adapter.setOnClickListener {
            navController.navigate(
                R.id.pagerFragment,
                bundleOf(CONSTANTS.STADION to Gson().toJson(it)),
                Utils.navOptions()
            )
        }

        adapter.setOnUpdateClickListener {
            navController.navigate(
                R.id.createStadiumFragment,
                bundleOf(
                    CONSTANTS.STADIUM_TYPE to CONSTANTS.EDIT_STADIUM,
                    CONSTANTS.STADIUM_DATA to Gson().toJson(it)
                ),
                Utils.navOptions()
            )
        }

        adapter.setOnAddImageClickListener {
            addImage(it)
        }
        adapter.setOnImageDeleteListener { id ->
            val dialog = BaseDialog(
                getString(R.string.delete),
                getString(R.string.are_you_sure_delete_image)
            )
            dialog.setOnDeleteListener {
                viewModel.deleteImage(id.fileId)
                dialog.dismiss()
            }
            dialog.show(childFragmentManager, "fsgsdfdf")

        }

        adapter.setOnImageClickListener { list, position ->
            ImageDialog(list, position).show(childFragmentManager, "image")
        }
        adapter.setOnDeleteClickListener { stadium ->
            val dialog = BaseDialog(getString(R.string.delete), getString(R.string.are_you_sure))
            dialog.setOnDeleteListener {
                viewModel.deleteStadium(stadium.id)
                dialog.dismiss()
            }
            dialog.show(childFragmentManager, "wrt")

        }
        bn.logOut.setOnClickListener {
            val dialog =
                BaseDialog(getString(R.string.exit), getString(R.string.do_you_want_to_exit))
            dialog.setOnDeleteListener {
                storage.hasAccount = false
                dialog.dismiss()
                storage.firebaseToken = ""
                requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
            dialog.show(childFragmentManager, "fsdf")

        }

        bn.addStadium.setOnClickListener {
            navController.navigate(
                R.id.createStadiumFragment,
                bundleOf(CONSTANTS.STADIUM_TYPE to CONSTANTS.NEW_STADIUM), Utils.navOptions()
            )
        }

    }

    private fun addImage(it: Stadium) {
        stadiumId = it.id
        checkPermission(Manifest.permission.CAMERA) {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                ImagePicker.with(this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start(12345)
            }
        }
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getAllStadiumDb().collect {
                adapter.submitList(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stadiumFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgress(false)
                        bn.swipeRefresh.isRefreshing = false
                        if (it.data != null && it.data.isNotEmpty()) {
                            bn.apply {
                                textNotStadium.isVisible = false
                            }
                        } else {
                            bn.apply {
                                textNotStadium.isVisible = true
                            }
                        }

                    }
                    is UiStateList.ERROR -> {
                        showProgress(false)
                        bn.apply {
                            delay(2000)
                            if (adapter.itemCount == 0) {
                                textNotStadium.isVisible = true
                                textNotStadium.text = it.message
                            }

                            if (it.code == 401) {
                                storage.hasAccount = false
                                requireActivity().startActivity(
                                    Intent(
                                        requireContext(),
                                        AuthActivity::class.java
                                    )
                                )
                                requireActivity().finish()
                            }
                            swipeRefresh.isRefreshing = false
                        }
                    }
                    is UiStateList.LOADING -> {
                        showProgress(true)
                        bn.apply {
                            swipeRefresh.isRefreshing = false
                            textNotStadium.isVisible = false
                        }
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.imageFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        viewModel.getAllStadium()
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage(it.message)
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)

                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.deleteStadiumFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        viewModel.getAllStadium()
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage(it.message)
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.deleteImageFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        viewModel.getAllStadium()
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage(it.message)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("AAA", "data2:$data")
        if (resultCode == Activity.RESULT_OK && requestCode == 12345) {
            Log.d("AAA", "data2:$data")
            val uri = data?.data ?: return
            val path = PathUtil.getPath(requireContext(), uri)
            val file = File(path)
            viewModel.uploadImage(stadiumId, file)

        }
        Log.d("AAA", "data:${resultCode}")
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }

    override fun onRefresh() {
        viewModel.getAllStadium()
    }
}