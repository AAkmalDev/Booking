package uz.koinot.stadion.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.koinot.stadion.data.model.*

@Database(entities = [Order::class, Stadium::class, StadiumOrder::class, OrderHistory::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun orderDao(): OrderDao
}