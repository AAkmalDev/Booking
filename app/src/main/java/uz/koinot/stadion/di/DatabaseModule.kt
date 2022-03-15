package uz.koinot.stadion.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.koinot.stadion.data.model.Converters
import uz.koinot.stadion.data.room.AppDatabase
import uz.koinot.stadion.data.room.OrderDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun getDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "block")
            .addTypeConverter(Converters())
            .allowMainThreadQueries()
           // .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun getRouteDao(database: AppDatabase): OrderDao = database.orderDao()
}
