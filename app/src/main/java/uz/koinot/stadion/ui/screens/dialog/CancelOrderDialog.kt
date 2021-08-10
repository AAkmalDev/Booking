package uz.koinot.stadion.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.CancelOrderLayoutBinding

class CancelOrderDialog(val txt1:String, val txt2: String,val status:Boolean) : DialogFragment() {

    private var _bn: CancelOrderLayoutBinding? = null
    private val bn get() = _bn!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bn = CancelOrderLayoutBinding.inflate(inflater, container, false)
        return bn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        bn.btnOk.setOnClickListener {
            dismiss()
        }
        bn.apply {
            text1.text = txt1
            text2.text = txt2
            if(status) image.setImageResource(R.drawable.ic_checked)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}