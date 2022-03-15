package uz.koinot.stadion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StadiumOrder(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val phone_number: String,
    val stadium_like: Int,
    val change_price_time: String,
    val price_day_time: Int,
    val price_night_time: Int,
    val width: Int,
    val height: Int,
    val count_order: Int,
    val opening_time: String,
    val closing_time: String,
    val photo: List<Photos>,
    var isLike: Boolean = false
)