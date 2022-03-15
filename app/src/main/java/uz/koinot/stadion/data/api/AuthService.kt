package uz.koinot.stadion.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.koinot.stadion.data.model.*

interface AuthService {

    @POST("koinot/auth/register")
    suspend fun auth(@Body data: Register): ResponseObject<Any>

    @POST("koinot/auth/login")
    suspend fun login(@Body data: Login): ResponseObject<TokenBody>

    @GET("koinot/auth/isBrbtStart/{number}")
    suspend fun isBotStarted(@Path("number") number: String): ResponseObject<Boolean>

    @POST("koinot/auth/sendCode/{phoneNumber}")
    suspend fun sendCode(@Path("phoneNumber") number: String): ResponseObject<Any>

    @GET("koinot/auth/newPassword/{password}")
    suspend fun createPassword(
        @Path("password") password: String
    ): ResponseObject<ResponseRegister>

    @POST("/koinot/auth/verify/{code}/{phoneNumber}")
    suspend fun verifyCode(
        @Path("code") code: String,
        @Path("phoneNumber") phoneNumber: String
    ): ResponseObject<TokenBody>

    @GET("/koinot/stadium")
    suspend fun getUserStadium(): ResponseObject<StadiumData>

    @GET("/koinot/stadium/{id}")
    suspend fun getStadiumById(@Path("id") id: Int): ResponseObject<StadiumOrder>

    @POST("/koinot/order/clientCreate")
    suspend fun userOrder(@Body data: CreateUserOrder): ResponseObject<Any>
}