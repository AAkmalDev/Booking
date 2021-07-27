package uz.koinot.stadion.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.compress
import uz.koinot.stadion.utils.userMessage
import java.io.File
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

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
//            e.printStackTrace()
        }
    }

    private var _imageFlow = MutableStateFlow<UiStateObject<Boolean>>(UiStateObject.EMPTY)
    val imageFlow: StateFlow<UiStateObject<Boolean>> get() = _imageFlow

    fun uploadImage(id: Long, file: File) = viewModelScope.launch {
        _imageFlow.value = UiStateObject.LOADING
        try {
            val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
            val image = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val res = repository.attachment(id, image)
            if (res.success == 200) {
                _imageFlow.value = UiStateObject.SUCCESS(true)
            } else {
                _imageFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _imageFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
        }
    }

    private var _deleteStadiumFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val deleteStadiumFlow: StateFlow<UiStateObject<String>> get() = _deleteStadiumFlow

    fun deleteStadium(id: Long) = viewModelScope.launch {
        _deleteStadiumFlow.value = UiStateObject.LOADING
        try {
            val res = repository.deleteStadium(id)
            if (res.success == 200) {
                _deleteStadiumFlow.value = UiStateObject.SUCCESS(res.message)
            } else {
                _deleteStadiumFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _deleteStadiumFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
//            e.printStackTrace()
        }
    }

    private var _deleteImageFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val deleteImageFlow: StateFlow<UiStateObject<String>> get() = _deleteImageFlow

    fun deleteImage(id: Long) = viewModelScope.launch {
        _deleteImageFlow.value = UiStateObject.LOADING
        try {
            val res = repository.deleteImage(id)
            if (res.success == 200) {
                _deleteImageFlow.value = UiStateObject.SUCCESS(res.message)
            } else {
                _deleteImageFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _deleteImageFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
//            e.printStackTrace()
        }
    }

    suspend fun setAllStadium(list: List<Stadium>) = repository.setAllStadium(list)
    suspend fun getAllStadiumDb() = repository.getAllStadium()
    suspend fun removeAllStadium() = repository.removeAllStadium()
}