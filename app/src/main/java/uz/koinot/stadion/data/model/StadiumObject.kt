package uz.koinot.stadion.data.model

data class StadiumObject(
    val id: Int,
    val lat: Double,
    val lng: Double,
    val like: Int,
    val distance: Int,
    val name: String,
    val roof: Boolean,
)