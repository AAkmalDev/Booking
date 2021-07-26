package uz.koinot.stadion.ui.screens.auth.forgot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentPasswordBinding
import uz.koinot.stadion.ui.screens.auth.login.LoginViewModel
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.vibrate
import javax.inject.Inject

@AndroidEntryPoint
class PasswordFragment : Fragment(R.layout.fragment_password) {

    private var _bind: FragmentPasswordBinding? = null
    private val bind get() = _bind!!


    private val viewmodel: LoginViewModel by viewModels()

    @Inject
    lateinit var pref: LocalStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind = FragmentPasswordBinding.bind(view)

        bind.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bind.btnPhoneNumber.setOnClickListener {
            val password = bind.inputPassword.text.toString().trim()
            val confirm = bind.inputConfirmPassword.text.toString().trim()
            when {
                password.length < 4 -> {
                    bind.inputPassword.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.shake
                        )
                    )
                    vibrate(requireContext())
                }
                confirm.isEmpty() -> {
                    bind.inputConfirmPassword.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.shake
                        )
                    )
                    vibrate(requireContext())

                }
                password != confirm -> {
                    bind.inputConfirmPassword.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.shake
                        )
                    )
                    vibrate(requireContext())
                }
                else -> {
                    viewmodel.sendForgotPassword(password)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewmodel.createPasswordFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        requireActivity().startActivity(
                            Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
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

    private fun showProgress(status: Boolean) {
        bind.progressBar.isVisible = status
    }
}