package uz.koinot.stadion

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.ActivitySplashBinding
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: ActivitySplashBinding? = null
    private val bn get() = _bn!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bn = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bn.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setLocal()

        val arg = intent.getStringExtra("language")

        if (storage.language == "" || arg.equals("language")) {
            bn.progressBar.visibility = View.GONE
            bn.imageBrand.visibility = View.GONE
            bn.linear.visibility = View.VISIBLE
            bn.apply {
                btnKirill.setOnClickListener {
                    storage.language = "be"
                    setLocal()
                    unSplash()
                }
                btnRu.setOnClickListener {
                    storage.language = "ru"
                    setLocal()
                    unSplash()
                }
                btnUz.setOnClickListener {
                    storage.language = "uz"
                    setLocal()
                    unSplash()
                }

            }
        } else {
            unSplash()
        }
    }

    private fun unSplash() {
        lifecycleScope.launchWhenCreated {
            delay(1500)
            when (storage.hasAccount) {
                "" -> {
                    startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                    finish()
                }
                "client" -> {
                    startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                    finish()
                }
                "user" -> {
                    startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                    finish()
                }
                "registerclient" -> {
                    startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setLocal() {
        val local = Locale(storage.language)
        Locale.setDefault(local)
        val config: Configuration = resources.configuration
        config.locale = local
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}