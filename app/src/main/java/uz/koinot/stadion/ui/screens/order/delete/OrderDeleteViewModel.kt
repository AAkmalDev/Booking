package uz.koinot.stadion.ui.screens.order.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Dashboard
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class OrderDeleteViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _orderDeleteFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val orderDeleteFlow: StateFlow<UiStateObject<String>> get() = _orderDeleteFlow

    fun orderDelete(id:Long) = viewModelScope.launch {
        _orderDeleteFlow.value = UiStateObject.LOADING
        try {
            val res = repository.deleteCancel(id)
            if(res.success == 200){
                _orderDeleteFlow.value = UiStateObject.SUCCESS(res.message)
            }else{
                _orderDeleteFlow.value = UiStateObject.ERROR(res.message)
            }
        }catch (e:Exception){
            _orderDeleteFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
        }
    }

    private var _getCancelFlow = MutableStateFlow<UiStateList<Order>>(UiStateList.EMPTY)
    val getCancelFlow: StateFlow<UiStateList<Order>> get() = _getCancelFlow

    fun getCancel(id:Long) = viewModelScope.launch {
        _getCancelFlow.value = UiStateList.LOADING
        try {
            val res = repository.getCancel(id)
            if(res.success == 200){
                _getCancelFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            }else{
                _getCancelFlow.value = UiStateList.ERROR(res.message)
            }
        }catch (e:Exception){
            _getCancelFlow.value = UiStateList.ERROR(e.userMessage()?:"not found")
//            e.printStackTrace()
        }
    }



}