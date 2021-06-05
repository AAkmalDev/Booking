package uz.koinot.stadion.di

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uz.koinot.stadion.data.api.AuthService
import uz.koinot.stadion.utils.CONSTANTS
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun getAuthRetrofit(client: OkHttpClient.Builder): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(CONSTANTS.BASE_URL)
        .client(client.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

    @Provides
    @Singleton
    fun getAuthService(retrofit: Retrofit.Builder): AuthService = retrofit.build().create(
        AuthService::class.java)

    @Provides
    @Singleton
    fun getAuthClient(@ApplicationContext context: Context):OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
//        .addInterceptor(ChuckInterceptor(context))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(Interceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.header("Content-Type", "application/x-www-form-urlencoded")
            builder.header("Accept", "application/json")
            chain.proceed(builder.build())
        })
}