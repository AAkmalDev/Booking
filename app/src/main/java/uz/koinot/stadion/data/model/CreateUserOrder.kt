package uz.koinot.stadion.data.model

import androidx.room.PrimaryKey

data class CreateUserOrder(
    var id: Int?,
    var stadiumId: Long,
    var time: String,
    var startDate: String,
    var endDate: String,
    var phoneNumber: String,
) {
}