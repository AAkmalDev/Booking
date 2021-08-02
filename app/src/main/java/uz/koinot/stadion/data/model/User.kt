package uz.koinot.stadion.data.model

data class User(
    var phoneNumber: String,
    var originalPhoneNumber: String,
    var firstName: String,
    var lastName: String,
    var language: String,
    var money: Int,
    var email: String,
)