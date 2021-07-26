package uz.koinot.stadion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var phoneNumber: String?,
    var originalPhoneNumber: String?,
    var firstName: String?,
    var lastName: String?,
    var language: String?,
    var latitude: Double,
    var longitude: Double,
    var sum: Double,
    var startDate: String,
    var createdAt: String,
    var endDate: String,
    var time: String,
    var active: Boolean,
    var cancelOrder: Boolean,
    var stadiumId: Long = 0,
    var countOrder: Double
)
