package uz.koinot.stadion.ui.screens.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.model.SmsBroadcastReceiver
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentVerificationBinding
import uz.koinot.stadion.ui.screens.auth.register.AuthViewModel
import uz.koinot.stadion.utils.TextWatcherWrapper
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.showMessage
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : Fragment(R.layout.fragment_verification) {

    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: FragmentVerificationBinding? = null
    val bn get() = _bn!!

    private var phoneNumber = ""
    private var isForgot = false

    private val viewModel: AuthViewModel by viewModels()

    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneNumber = arguments?.getString("phoneKey", "") ?: ""
        //phoneNumber = arguments?.getString("phoneKey", "") ?: ""
        isForgot = arguments?.getBoolean("isForgot") ?: false

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentVerificationBinding.bind(view)

        startTimer()

        startSmsUserConsent()

        registerBroadcastReceiver()

        bn.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        if (isForgot) {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.verifyFlow.collect {
                    try {
                        when (it) {
                            is UiStateObject.SUCCESS -> {
                                showProgress(false)
                                findNavController().navigate(
                                    R.id.passwordFragment,
                                    null,
                                    Utils.navOptions()
                                )
                            }
                            is UiStateObject.LOADING -> {
                                showProgress(true)
                            }
                            is UiStateObject.ERROR -> {
                                showProgress(false)
                            }
                            else -> Unit
                        }
                    } catch (e: Exception) {
                        showProgress(false)
                        showMessage("xato")
                    }
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.verifyFlow.collect {
                    try {
                        when (it) {
                            is UiStateObject.SUCCESS -> {
                                showProgress(false)
                                showMessage(getString(R.string.success_register))
                                requireActivity().finishAffinity()
                                requireActivity().startActivity(
                                    Intent(requireContext(), MainActivity::class.java)
                                )
                            }
                            is UiStateObject.LOADING -> {
                                showProgress(true)
                            }
                            is UiStateObject.ERROR -> {
                                showProgress(false)
                            }
                            else -> Unit
                        }
                    } catch (e: Exception) {
                        showProgress(false)
                        showMessage(getString(R.string.error))
                    }
                }
            }
        }
        bn.tryAgainText.setOnClickListener {
            bn.tryAgainText.visibility = View.GONE
            startTimer()
            showProgress(true)
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                try {
                    viewModel.sendCode(phoneNumber)
                    val res = api.recode()
                    showProgress(false)
                    if (res.success == 200) {
                        showMessage("Мы отправили проверочный код")
                    } else {
                        showMessage(getString(R.string.error))
                    }

                } catch (e: Exception) {
                    showProgress(false)
                    showMessage(getString(R.string.error))
                }
            }
        }

        bn.inputVerificationNumber.addTextChangedListener(textWatcherName)
        bn.btnVerification.setOnClickListener {
            btnClick()
        }
    }

    private fun getOtpFromMessage(message: String) { // This will match any 6 digit number in the message
        val matcher = Pattern.compile("(|^)\\d{6}").matcher(message)
        if (matcher.find()) {
            bn.inputVerificationNumber.setText(matcher.group(0))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK && data != null) { //That gives all message to us.
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                message?.let {
                    getOtpFromMessage(message)
                }
            }
        }
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireContext())
        client.startSmsUserConsent(null)
            .addOnSuccessListener { /**/ }
            .addOnFailureListener { /**/ }
    }


    private fun btnClick() {
        val number = bn.inputVerificationNumber.text.toString().trim()
        if (number.isNotEmpty()) {
            viewModel.verifyCode(number, phoneNumber)
        } else {
            showMessage("error")
        }
    }


    private fun startTimer() {
        bn.countDownTimerLinear.visibility = View.VISIBLE
        bn.coutdownView.start(120000)
        bn.coutdownView.setOnCountdownEndListener {
            bn.tryAgainText.visibility = View.VISIBLE
            bn.countDownTimerLinear.visibility = View.GONE
        }
    }

    private val textWatcherName = object : TextWatcherWrapper() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bn.inputVerificationNumber.text!!.length > 5) {
                Utils.closeKeyboard(requireActivity())
                bn.btnVerification.isEnabled = true
                btnClick()
            }
        }
    }


    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent, 200)
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        context?.registerReceiver(smsBroadcastReceiver, intentFilter)
    }
    private fun showProgress(status: Boolean) {
        bn.progressBar.isVisible = status
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (smsBroadcastReceiver != null)
                context?.unregisterReceiver(smsBroadcastReceiver)
        } catch (ex: IllegalArgumentException) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}