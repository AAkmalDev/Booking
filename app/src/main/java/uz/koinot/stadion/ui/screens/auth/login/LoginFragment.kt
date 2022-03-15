package uz.koinot.stadion.ui.screens.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentLoginBinding
import uz.koinot.stadion.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var storage: LocalStorage
    private var number = ""

    private var _bn: FragmentLoginBinding? = null
    val bn get() = _bn!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentLoginBinding.bind(view)
        navController = findNavController()

        val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+998 __ ___ __ __")
        val format = MaskFormatWatcher(MaskImpl.createTerminated(slots))
        format.installOn(bn.inputPhoneNumber)

        bn.inputPhoneNumber.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                bn.inputPhoneNumber.setText("+998")
            }
        }
        bn.inputPhoneNumber.addTextChangedListener(textWatcherName)
        bn.txForgotPassword.setOnClickListener {
            navController.navigate(R.id.forgotFragment, null, Utils.navOptions())
        }
        bn.apply {
            btnLogin.setOnClickListener {
                Utils.closeKeyboard(requireActivity())
                number = inputPhoneNumber.text.toString().replace(" ", "")
                val password = inputPassword.text.toString().trim()
                when {
                    number.length != 13 -> {
                        bn.inputPhoneNumber.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.shake
                            )
                        )
                        vibrate(requireContext())
                    }

                    password.length < 4 -> {
                        bn.inputPassword.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.shake
                            )
                        )
                        vibrate(requireContext())
                    }

                    else -> {
                        viewModel.login(Login(number, password))
                    }
                }
            }
            btnSignup.setOnClickListener {
                navController.navigate(R.id.phoneNumberFragment, null, Utils.navOptions())
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.loginFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        storage.hasAccount = "registerclient"
                        storage.phoneNumber = number
                        storage.firebaseToken = ""
                        requireActivity().startActivity(
                            Intent(
                                requireContext(),
                                MainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                        viewModel.reLogin()
                    }
                    is UiStateObject.ERROR -> {
                        storage.hasAccount = ""
                        storage.phoneNumber = ""
                        storage.firebaseToken = ""
                        showProgress(false)
                        if (it.message == "Bad Request") {
                            bn.txForgotError.visibility = View.VISIBLE
                        }
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

    private val textWatcherName = object : TextWatcherWrapper() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bn.inputPhoneNumber.text.toString().length == 17) {
                bn.inputPassword.requestFocus()
            }
        }
    }

    private fun showProgress(status: Boolean) {
        bn.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}