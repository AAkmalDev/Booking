package uz.koinot.stadion.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.model.ResponseRegister
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository,
    private val storage: LocalStorage
) : ViewModel() {

    private var _loginFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val loginFlow: StateFlow<UiStateObject<String>> get() = _loginFlow

    fun login(data: Login) = viewModelScope.launch {
        _loginFlow.value = UiStateObject.LOADING
        try {
            val res = repository.login(data)
            if (res.success == 200) {
                storage.accessToken = res.objectKoinot!!.accessToken
                _loginFlow.value = UiStateObject.SUCCESS(res.message)
            } else {
                _loginFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _loginFlow.value = UiStateObject.ERROR(e.userMessage())
        }
    }

    fun reLogin() {
        _loginFlow.value = UiStateObject.EMPTY
    }


    private val _forgotPhoneFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val forgotPhoneFlow: StateFlow<UiStateObject<String>> get() = _forgotPhoneFlow

    fun getVerificationCode(sendNumber: String) = viewModelScope.launch {
        _forgotPhoneFlow.value = UiStateObject.LOADING
        try {
            val verification = repository.sendCode(sendNumber)
            if (verification.success == 200) {
                _forgotPhoneFlow.value = UiStateObject.SUCCESS(verification.message)
            } else {
                _forgotPhoneFlow.value = UiStateObject.ERROR(verification.message)
            }
        } catch (e: Exception) {
            _forgotPhoneFlow.value = UiStateObject.ERROR(e.userMessage())
        }
    }

    fun reCode() {
        _forgotPhoneFlow.value = UiStateObject.EMPTY
    }

    private val _createPasswordFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val createPasswordFlow: StateFlow<UiStateObject<String>> get() = _createPasswordFlow

    fun sendForgotPassword(password: String) = viewModelScope.launch {
        _createPasswordFlow.value = UiStateObject.LOADING
        try {
            val res = repository.createPassword(password)
            if (res.success == 202) {
                _createPasswordFlow.value = UiStateObject.SUCCESS(res.message)
            } else {
                _createPasswordFlow.value = UiStateObject.ERROR(res.message)
            }
        } catch (e: Exception) {
            _createPasswordFlow.value = UiStateObject.ERROR(e.userMessage())
        }
    }
}