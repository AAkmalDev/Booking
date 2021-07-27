package uz.koinot.stadion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stadium(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var name: String? = "",
    var countVerify: Int,
    var countNotVerify: Int,
    var latitude: Double,
    var phone_number: String,
    var longitude: Double,
    var address: String,
    var opening_time: String?,
    var closing_time: String?,
    var stadium_like: Int,
    var change_price_time: String?,
    var price_day_time: Double,
    var price_night_time: Double,
    var width: Int,
    var height: Int,
    var count_order: Double,
    var open: Boolean,
    var active: Boolean,
    var verify: Boolean,
    var money: Double,
    var photos: List<Photos>
)