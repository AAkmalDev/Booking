package uz.koinot.stadion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.ActivityMenuBinding
import javax.inject.Inject

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: ActivityMenuBinding? = null
    private val bn get() = _bn!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bn = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(bn.root)

        bn.apply {
            user.setOnClickListener {
                storage.hasAccount = "user"
                startActivity(Intent(this@MenuActivity, UserActivity::class.java))

            }
            client.setOnClickListener {
                startActivity(Intent(this@MenuActivity, MainActivity::class.java))

            }
        }

    }
}