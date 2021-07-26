package uz.koinot.stadion.utils

import android.content.Context
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.ConnectException

fun Fragment.showMessage(message:Any){
    Toast.makeText(requireContext(),message.toString(),Toast.LENGTH_SHORT).show()
}
fun FragmentActivity.showMessage(message:Any){
    Toast.makeText(this,message.toString(),Toast.LENGTH_SHORT).show()
}
fun Fragment.customLog(message: Any){
    Log.e("AAA",message.toString())
}

fun String.toNeedDate():String{
    return  this.replace("T"," ").substring(0,this.length-6)
}

fun String.toNeedTime():String{
    return  this.substring(this.indexOf("T")+1,this.indexOf("T")+6)
//    return  this.replace("T"," ").substring(0,this.length-6)
}

fun vibrate(context: Context) {
    val vibrate = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrate.vibrate(100)
}

fun Int.getString():String{
    return if (this.toString().length == 1) "0$this" else this.toString()
}
fun String.getTimeStamp():String{
    return "2021-05-16T$this:15.050000"
}

fun Throwable.userMessage() = when (this) {
    is ConnectException -> "Не интернет"
    is HttpException -> when (this.code()) {
        304 -> "Not Modified"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        405 -> "Method Not Allowed"
        409 -> "Драйвер не найден"
        422 -> "Unprocessable"
        in 500..600 -> "Server Error"
        else -> "Something went wrong"
    }
    is IOException -> "Network error"
    else -> "Unknown error"
}
typealias SingleBlock<T> = (T) -> Unit

