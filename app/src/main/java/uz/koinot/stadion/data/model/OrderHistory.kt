package uz.koinot.stadion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderHistory(
    @PrimaryKey(autoGenerate = true)
    var orderId:Int? = null,
    var id: Int? = null,
    var stadiumOrder: String,
    var stadiumId: Long,
    var time: String,
    var startDate: String,
    var endDate: String,
    var phoneNumber: String,
)