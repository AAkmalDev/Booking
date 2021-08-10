package uz.koinot.stadion.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.api.ApiService
import javax.inject.Inject
import kotlin.random.Random

private const val CHANNEL_ID = "channelId"
private const val CHANNEL_NAME = "channelName"

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        GlobalScope.launch {
            try {
                val res = Retrofit.Builder()
                    .baseUrl(CONSTANTS.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java).token(newToken)
                if(res.success == 200){
                    Log.e("AAA","newToken success: $newToken")
                }else{
                    Log.e("AAA","newToken error: $newToken")
                }
            }catch (e:Exception){
                Log.e("AAA","newToken error catch: $newToken")
                e.printStackTrace()
            }
        }
        Log.e("AAA","newToken: $newToken")
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("AAA","received:$message")

            var type = 0
            if(message.data["natificationType"] == CONSTANTS.ORDER){
                type = R.drawable.ic_baseline_record_voice_over_24
            }else if(message.data["natificationType"] == CONSTANTS.CANCEL){
                type = R.drawable.ic_baseline_voice_over_off_24
            }else {
                type = R.drawable.ic_baseline_check_circle_24
            }

            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("koinot","main")
            intent.putExtra("stadium",message.data["stadium"].toString())
            intent.putExtra("status",message.data["status"].toBoolean())
            intent.putExtra("natificationType",message.data["natificationType"].toString())
            intent.putExtra("title",message.data["title"].toString())
            intent.putExtra("message",message.data["message"].toString())


            val requestCode: Int = (0..10).random()
            val pendingIntent = PendingIntent.getActivity(this,requestCode,intent,FLAG_ONE_SHOT)
            val bigStyle = NotificationCompat.BigTextStyle()
            bigStyle.setBigContentTitle(message.data["title"] ?: "Stadium").bigText(message.data["message"] ?: "New Order")

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"] ?: "Stadium")
                .setContentText(message.data["message"] ?: "Sizga yangi zakaz")
                .setSmallIcon(type)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_brbt))
                .setStyle(bigStyle)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                createNotificationChannel(manager)
            val notificationId = Random.nextInt()
            manager.notify(notificationId,notification)

        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH).apply {
            description = "koinot"
            lightColor = Color.RED
            enableLights(true)
        }
        manager.createNotificationChannel(channel)
    }
}
