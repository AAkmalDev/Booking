package uz.koinot.stadion.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.User
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var _getUserFlow = MutableStateFlow<UiStateObject<User>>(UiStateObject.EMPTY)
    val getUserFlow: StateFlow<UiStateObject<User>> get() = _getUserFlow

    fun getUser() = viewModelScope.launch {
        _getUserFlow.value = UiStateObject.LOADING
        try {
            val res = repository.getUser()
            if (res.success == 202) {
                _getUserFlow.value = UiStateObject.SUCCESS(res.objectKoinot!!)
            } else {
                _getUserFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _getUserFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
            e.printStackTrace()
        }
    }

}