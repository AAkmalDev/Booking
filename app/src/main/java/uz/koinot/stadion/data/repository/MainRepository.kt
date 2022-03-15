package uz.koinot.stadion.data.repository

import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Path
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.api.AuthService
import uz.koinot.stadion.data.model.*
import uz.koinot.stadion.data.room.OrderDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: ApiService,
    private val authApi: AuthService,
    private val orderDao: OrderDao,
) {

    fun setAllOrder(list: List<Order>) = orderDao.setAllOrder(list)
    fun getAllOrder(id: Long) = orderDao.getAllOrders(id)
    fun removeAllOrder() = orderDao.removeAllOrders()
    fun setUserOrder(orderHistory: OrderHistory) = orderDao.setUserOrder(orderHistory)
    fun getUserOrder() = orderDao.getUserOrder()
    fun setLikeStadium(stadiumOrder: StadiumOrder) = orderDao.setLikeStadium(stadiumOrder)
    fun unLikeStadium(id:Int) = orderDao.unLikeStadium(id)
    fun getStadiumLikeById(id:Int) = orderDao.getStadiumLikeById(id)

    suspend fun setAllStadium(list: List<Stadium>) = orderDao.setAllStadium(list)
    suspend fun getAllStadium() = orderDao.getAllStadiums()
    suspend fun removeAllStadium() = orderDao.removeAllStadiums()

    suspend fun getStadium() = api.getStadium()
    suspend fun getOder(orderId: Long) = api.stadiumOrder(orderId)
    suspend fun accept(orderId: Long) = api.accept(orderId)
    suspend fun reject(orderId: Long) = api.reject(orderId)
    suspend fun dashboard(stadiumId: Long, s1: String, s2: String) = api.graph(stadiumId, s1, s2)
    suspend fun archiveAll(number: Long) = api.archiveAll(number)
    suspend fun archiveAfterCreateTime(number: Long, time: String) =
        api.archiveAfterCreateTime(number, time)

    suspend fun login(data: Login) = authApi.login(data)
    suspend fun attachment(id: Long, image: MultipartBody.Part) = api.uploadPhoto(id, listOf(image))
    suspend fun createStadium(data: CreateStadium) = api.createStadium(data)
    suspend fun createOrder(data: CreateOrder) = api.createOrder(data)
    suspend fun searchUser(data: String) = api.search(data)
    suspend fun orderPrice(id: Long, startDate: String, endDate: String) =
        api.orderPrice(id, startDate, endDate)

    suspend fun deleteStadium(id: Long) = api.deleteStadium(id)
    suspend fun deleteImage(id: Long) = api.deleteImage(id)
    suspend fun getCancel(id: Long) = api.getCancel(id)
    suspend fun deleteCancel(id: Long) = api.deleteCancel(id)
    suspend fun isBotStarted(number: String) = authApi.isBotStarted(number)
    suspend fun register(data: Register) = authApi.auth(data)
    suspend fun sendCode(phoneNumber: String) = authApi.sendCode(phoneNumber)
    suspend fun verifyCode(code: String, phoneNumber: String) =
        authApi.verifyCode(code, phoneNumber)

    suspend fun createPassword(password: String) =
        api.newPassword(password)

    suspend fun getUser() = api.getUser()

    suspend fun getUserStadium() = authApi.getUserStadium()
    suspend fun getStadiumById(id: Int) = authApi.getStadiumById(id)
    suspend fun userOrder(data: CreateUserOrder) = authApi.userOrder(data)

}