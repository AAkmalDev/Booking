package uz.koinot.stadion.ui.screens.userclient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.model.StadiumData
import uz.koinot.stadion.data.model.StadiumOrder
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class StadiumViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    private var _objectStadiumFlow =
        MutableStateFlow<UiStateObject<StadiumData>>(UiStateObject.EMPTY)
    val objectStadiumFlow: StateFlow<UiStateObject<StadiumData?>> get() = _objectStadiumFlow

    fun getSsd() = viewModelScope.launch {
        _objectStadiumFlow.value = UiStateObject.LOADING
        try {
            val res = repository.getUserStadium()
            if (res.success == 200) {
                _objectStadiumFlow.value = UiStateObject.SUCCESS(res.objectKoinot!!)!!
            } else {
                _objectStadiumFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _objectStadiumFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
        }
    }

    private var _objectStadiumById = MutableStateFlow<UiStateObject<StadiumOrder>>(UiStateObject.EMPTY)
    val objectStadiumById: StateFlow<UiStateObject<StadiumOrder>> get() = _objectStadiumById

    fun getStadiumById(id: Int) = viewModelScope.launch {
        _objectStadiumById.value = UiStateObject.LOADING
        try {
            val res = repository.getStadiumById(id)
            if (res.success == 200){
                _objectStadiumById.value = UiStateObject.SUCCESS(res.objectKoinot!!)
            }else{
                _objectStadiumById.value = UiStateObject.ERROR(res.message,true)
            }
        }catch (e:Exception){
            _objectStadiumById.value = UiStateObject.ERROR(e.userMessage()?:"not found")
        }
    }

    fun setLikeStadium(stadiumOrder: StadiumOrder) = repository.setLikeStadium(stadiumOrder)
    fun unLikeStadium(id: Int) = repository.unLikeStadium(id)
    fun getStadiumLikeById(id: Int) = repository.getStadiumLikeById(id)

}