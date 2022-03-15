package uz.koinot.stadion.ui.screens.order.userorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.data.model.CreateUserOrder
import uz.koinot.stadion.data.model.OrderHistory
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class UserOrderViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    private var _createOrderFlow = MutableStateFlow<UiStateObject<Boolean>>(UiStateObject.EMPTY)
    val createOrderFlow: StateFlow<UiStateObject<Boolean>> get() = _createOrderFlow

    fun createOrder(data: CreateUserOrder) = viewModelScope.launch {
        _createOrderFlow.value = UiStateObject.LOADING
        try {
            val res = repository.userOrder(data)
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

    fun setUserOrder(orderHistory: OrderHistory) = repository.setUserOrder(orderHistory)
    suspend fun getUserOrder() = repository.getUserOrder()

}