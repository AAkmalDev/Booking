package uz.koinot.stadion.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.ImageAdapter
import uz.koinot.stadion.databinding.LayoutToTelegramBinding
import uz.koinot.stadion.utils.SingleBlock

class GoToTelegramDialog: DialogFragment() {
    private var _bn: LayoutToTelegramBinding? = null
    private val bn get() = _bn!!
    val adapter = ImageAdapter()
    private var listener:SingleBlock<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bn = LayoutToTelegramBinding.inflate(inflater,container,false)
        return bn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bn.apply {
            btnYes.setOnClickListener {
                listener?.invoke(true)
            }
        }
    }
    fun setOnDeleteListener(block: SingleBlock<Boolean>){
        listener = block
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}