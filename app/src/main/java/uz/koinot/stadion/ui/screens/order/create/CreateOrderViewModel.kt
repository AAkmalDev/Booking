package uz.koinot.stadion.ui.screens.order.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.SearchUser
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _searchUserFlow = MutableStateFlow<UiStateList<SearchUser>>(UiStateList.EMPTY)
    val searchUserFlow: StateFlow<UiStateList<SearchUser>> get() = _searchUserFlow

    fun searchUser(number: String) = viewModelScope.launch {
        _searchUserFlow.value = UiStateList.LOADING
        try {
            val res = repository.searchUser(number)
            if (res.success == 200) {
                _searchUserFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            } else {
                _searchUserFlow.value = UiStateList.ERROR(res.message)
            }
        } catch (e: Exception) {
            _searchUserFlow.value = UiStateList.ERROR(e.userMessage()?:"not found")
            e.printStackTrace()
        }
    }

    private var _createOrderFlow = MutableStateFlow<UiStateObject<Boolean>>(UiStateObject.EMPTY)
    val createOrderFlow: StateFlow<UiStateObject<Boolean>> get() = _createOrderFlow

    fun createOrder(data: CreateOrder) = viewModelScope.launch {
        _createOrderFlow.value = UiStateObject.LOADING
        try {
            val res = repository.createOrder(data)
            if (res.success == 200) {
                _createOrderFlow.value = UiStateObject.SUCCESS(true)
            } else {
                _createOrderFlow.value = UiStateObject.ERROR(res.message)
            }
        } catch (e: Exception) {
            _createOrderFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
            e.printStackTrace()
        }
    }

    private var _orderPriceFlow = MutableStateFlow<UiStateObject<Double>>(UiStateObject.EMPTY)
    val orderPriceFlow: StateFlow<UiStateObject<Double>> get() = _orderPriceFlow

    fun getOrderPrice(id:Long,startDate:String,endDate:String) = viewModelScope.launch {
        _orderPriceFlow.value = UiStateObject.LOADING
        try {
            val res = repository.orderPrice(id, startDate, endDate)
            if (res.success == 200 && res.objectKoinot != null) {
                _orderPriceFlow.value = UiStateObject.SUCCESS(res.objectKoinot!!)
            } else {
                _orderPriceFlow.value = UiStateObject.ERROR(res.message)
            }
        } catch (e: Exception) {
            _orderPriceFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
            e.printStackTrace()
        }
    }
}