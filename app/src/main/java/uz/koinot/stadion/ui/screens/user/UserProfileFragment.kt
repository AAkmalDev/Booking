package uz.koinot.stadion.ui.screens.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.AuthActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.User
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentUserProfileBinding
import uz.koinot.stadion.ui.screens.home.BaseDialog
import uz.koinot.stadion.utils.UiStateObject
import javax.inject.Inject


@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private val viewModel: UserProfileViewModel by viewModels()
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var storage: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserProfileBinding.bind(view)
        viewModel.getUser()
        logOut()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getUserFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        getUserData(it.data)
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun logOut() {
        binding.logOut.setOnClickListener {
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
    }

    @SuppressLint("SetTextI18n")
    fun getUserData(user: User) {
        binding.apply {
            userName.text = "${user.lastName.replace("null","")} ${user.firstName.replace("null","" )}"
            userPhoneNumber.text = user.phoneNumber
            userMoney.text = user.money.toString()
        }
    }

    private fun showProgress(status: Boolean) {
        binding.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}