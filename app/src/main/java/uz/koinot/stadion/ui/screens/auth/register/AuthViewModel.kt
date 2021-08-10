package uz.koinot.stadion.ui.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Register
import uz.koinot.stadion.data.model.TokenBody
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository,
    private var storage: LocalStorage
) : ViewModel() {

    private var _registerFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val registerFlow: StateFlow<UiStateObject<String>> get() = _registerFlow

    fun register(data: Register) = viewModelScope.launch {
        _registerFlow.value = UiStateObject.LOADING
        try {
            val res = repository.register(data)
            if (res.success == 200) {
                _registerFlow.value = UiStateObject.SUCCESS(res.message)
                _registerFlow.value = UiStateObject.EMPTY
            } else {
                _registerFlow.value = UiStateObject.ERROR(res.message)
                _registerFlow.value = UiStateObject.EMPTY
            }
        } catch (e: Exception) {
            _registerFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
            _registerFlow.value = UiStateObject.EMPTY
        }
    }

    fun reBot() {
        _isBotStartedFlow.value = UiStateObject.EMPTY
    }

    private var _isBotStartedFlow = MutableStateFlow<UiStateObject<Boolean>>(UiStateObject.EMPTY)
    val isBotStartedFlow: StateFlow<UiStateObject<Boolean>> get() = _isBotStartedFlow

    fun isBotStarted(number: String) = viewModelScope.launch {
        _isBotStartedFlow.value = UiStateObject.LOADING
        try {
            val res = repository.isBotStarted(number)
            if (res.objectKoinot != null && res.objectKoinot!!) {
                _isBotStartedFlow.value = UiStateObject.SUCCESS(true)
            } else {
                _isBotStartedFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _isBotStartedFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
            e.printStackTrace()
        }
    }


    private var _rejectFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val rejectFlow: StateFlow<UiStateObject<String>> get() = _rejectFlow

    fun rejectOrder(orderId: Long) = viewModelScope.launch {
        _rejectFlow.value = UiStateObject.LOADING
        try {
            val res = repository.reject(orderId)
            if (res.success == 200) {
                _rejectFlow.value = UiStateObject.SUCCESS("Success")
            } else {
                _rejectFlow.value = UiStateObject.ERROR(res.message)
            }
        } catch (e: Exception) {
            _rejectFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
            e.printStackTrace()
        }
    }

    private var _verifyFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val verifyFlow: StateFlow<UiStateObject<String>> get() = _verifyFlow

    fun verifyCode(code: String, phoneNumber: String) = viewModelScope.launch {
        _verifyFlow.value = UiStateObject.LOADING
        try {
            val result = repository.verifyCode(code, phoneNumber)
            if (result.success == 202) {
                storage.accessToken = result.objectKoinot?.accessToken!!
                storage.hasAccount = true
                storage.firebaseToken = ""
                _verifyFlow.value = UiStateObject.SUCCESS("Success")
            } else {
                _verifyFlow.value = UiStateObject.ERROR(result.message)
            }
        } catch (e: Exception) {
            _verifyFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
            e.printStackTrace()
        }
    }

    private var _sendCode = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val sendCode: StateFlow<UiStateObject<String>> get() = _sendCode

    fun sendCode(phoneNumber: String) = viewModelScope.launch {
        _sendCode.value = UiStateObject.LOADING
        try {
            val result = repository.sendCode(phoneNumber)
            if (result.success == 200) {
                _sendCode.value = UiStateObject.SUCCESS("Success")
            } else {
                _sendCode.value = UiStateObject.ERROR(result.message)
            }
        } catch (e: Exception) {
            _sendCode.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
        }
    }

}