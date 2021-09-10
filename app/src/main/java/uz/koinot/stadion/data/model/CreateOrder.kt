package uz.koinot.stadion.data.model

data class CreateOrder(
    var id: Int?,
    var stadiumId: Long,
    var time: List<String>,
    var startDate: String,
    var endDate: String,
    var phoneNumber: String
)