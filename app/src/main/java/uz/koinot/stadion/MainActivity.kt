package uz.koinot.stadion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.INotification
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.ActivityMainBinding
import uz.koinot.stadion.ui.screens.dialog.CancelOrderDialog
import uz.koinot.stadion.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var iNotification: INotification? = null

    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var storage: LocalStorage

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private var _bn: ActivityMainBinding? = null
    private val bn get() = _bn!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bn = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bn.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        navController = findNavController(R.id.nav_host_fragment_container)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)

        Firebase.messaging.subscribeToTopic("koinot").addOnCompleteListener { task ->
            Log.e("AAA", "subscribe: ${task.isSuccessful}")
//            Log.e("AAA","subscribe: ${task.result}")
        }
//        Firebase.messaging.isAutoInitEnabled = true

        if (storage.firebaseToken.isEmpty()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                if(!task.isSuccessful) return@OnCompleteListener

                GlobalScope.launch {
                    try {
                        api.token(task.result.toString())
                        storage.firebaseToken = task.result.toString()
                    } catch (e: Exception) {
//                        showMessage(e.localizedMessage?: "not found")
//                        e.printStackTrace()
                    }
                }
                Log.e("AAA", "token is: " + task.result.toString())
            })
        }
        createDialog(intent)
    }

    private fun createDialog(intent: Intent?) {
        if (intent != null) {
            val text = intent.getStringExtra("koinot")
            val stadium = intent.getStringExtra("stadium")
            val natificationType = intent.getStringExtra("natificationType")
            val status = intent.getBooleanExtra("status", false)
            val title = intent.getStringExtra("title")
            val message = intent.getStringExtra("message")
            if (text == "main") {
                if (natificationType == CONSTANTS.ALL) {
                    title?.let {
                        if (message != null) {
                            CancelOrderDialog(it, message, status).show(
                                supportFragmentManager,
                                "hdbch"
                            )
                        }
                    }
                } else {
                    navController.popBackStack()
                    navController.navigate(
                        R.id.pagerFragment, bundleOf(CONSTANTS.STADION to stadium),
                        Utils.navOptions()
                    )
                }
            }

        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        createDialog(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }

    fun setInterface(inter: INotification) {
        iNotification = inter
    }
}