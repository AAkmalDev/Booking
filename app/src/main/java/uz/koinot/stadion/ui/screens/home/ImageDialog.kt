package uz.koinot.stadion.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.ImageAdapter
import uz.koinot.stadion.data.model.Photos
import uz.koinot.stadion.databinding.ImageDialogBinding

class ImageDialog(val photos: List<Photos>,val selectedItem:Int): DialogFragment() {
    private var _bn:ImageDialogBinding? = null
    private val bn get() = _bn!!
    val adapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog2)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bn = ImageDialogBinding.inflate(inflater,container,false)
        return bn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.submitList(photos)
        bn.viewPager.adapter = adapter
        bn.viewPager.currentItem = selectedItem
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}