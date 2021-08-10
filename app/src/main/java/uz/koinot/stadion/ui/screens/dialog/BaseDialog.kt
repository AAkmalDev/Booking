package uz.koinot.stadion.ui.screens.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.ImageAdapter
import uz.koinot.stadion.databinding.BaseDialogLayoutBinding
import uz.koinot.stadion.utils.SingleBlock

class BaseDialog(val title: String, val description: String) : DialogFragment() {
    private var _bn: BaseDialogLayoutBinding? = null
    private val bn get() = _bn!!
    val adapter = ImageAdapter()
    private var listener: SingleBlock<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bn = BaseDialogLayoutBinding.inflate(inflater, container, false)
        return bn.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bn.apply {
            textTitle.visibility = View.VISIBLE
            textDescription.visibility = View.VISIBLE
            textTitle.text = title
            textDescription.text = description

            btnNo.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                listener?.invoke(true)
            }
        }
    }

    fun setOnDeleteListener(block: SingleBlock<Boolean>) {
        listener = block
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}