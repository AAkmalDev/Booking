package uz.koinot.stadion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Photos(
    var fileId: Long,
    var fileName: String,
    var fileType: String,
    var why: String,
    var size: Long,
    var link: String,
)