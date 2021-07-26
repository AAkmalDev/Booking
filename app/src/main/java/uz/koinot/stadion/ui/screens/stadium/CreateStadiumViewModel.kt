package uz.koinot.stadion.ui.screens.stadium

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.PathUtil
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CreateStadiumViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var _createStadiumFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val createStadiumFlow: StateFlow<UiStateObject<String>> get() = _createStadiumFlow

    fun createStadium(data: CreateStadium) = viewModelScope.launch {
        _createStadiumFlow.value = UiStateObject.LOADING
        try {
            val res = repository.createStadium(data)

            if (res.success == 200) {
                _createStadiumFlow.value = UiStateObject.SUCCESS(res.message)
            } else {
                _createStadiumFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _createStadiumFlow.value = UiStateObject.ERROR(e.userMessage() ?: "not found")
            e.printStackTrace()
        }
    }

    private var _stadiumFlow = MutableStateFlow<UiStateList<Stadium>>(UiStateList.EMPTY)
    val stadiumFlow: StateFlow<UiStateList<Stadium>> get() = _stadiumFlow

    fun getAllStadium() = viewModelScope.launch {
        _stadiumFlow.value = UiStateList.LOADING
        try {
            val res = repository.getStadium()
            if (res.success == 200) {
                _stadiumFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            } else {
                _stadiumFlow.value = UiStateList.ERROR(res.message, true, res.success)
            }
        } catch (e: Exception) {
            _stadiumFlow.value = UiStateList.ERROR(e.userMessage() ?: "not found")
        }
    }

    suspend fun setAllStadium(list: List<Stadium>) = repository.setAllStadium(list)
    suspend fun removeAllStadium() = repository.removeAllStadium()

}