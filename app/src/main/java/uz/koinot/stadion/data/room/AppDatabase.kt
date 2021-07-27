package uz.koinot.stadion.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import uz.koinot.stadion.data.model.Converters
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium

@Database(entities = [Order::class,Stadium::class],version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun orderDao(): OrderDao
}