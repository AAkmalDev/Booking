package uz.koinot.stadion.data.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromPhoto(value: String?):List<Photos>?{
        return Gson().fromJson(value,object : TypeToken<List<Photos>>(){}.type)
    }
    @TypeConverter
    fun listToString(ls:List<Photos>?):String?{
        return Gson().toJson(ls)
    }

}