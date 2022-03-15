package uz.koinot.stadion.ui.screens.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.like.LikeButton
import com.like.OnLikeListener
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.RvImageAdapter
import uz.koinot.stadion.data.model.StadiumOrder
import uz.koinot.stadion.databinding.BaseStadiumDialogBinding
import uz.koinot.stadion.utils.SingleBlock

class StadiumDialog(val data: StadiumOrder, val stadiumName: String?) : DialogFragment() {

    private var _bn: BaseStadiumDialogBinding? = null
    private val binding get() = _bn!!
    private var imageAdapter: RvImageAdapter? = null
    private var listener: SingleBlock<Boolean>? = null
    private var likeListener: SingleBlock<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _bn = BaseStadiumDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            openTime.text = data.opening_time
            closeTime.text = data.closing_time
            stdDayPrice.text = data.price_day_time.toString()
            stdNightPrice.text = data.price_night_time.toString()
            timeTill.text = data.change_price_time
            likeCount.text = data.stadium_like.toString()
            countOrder.text = data.count_order.toString()
            stdName.text = stadiumName ?: "name yoq"
            imageAdapter = RvImageAdapter(data.photo)
            rvImages.adapter = imageAdapter
            btnLike.isLiked = data.isLike


            btnNo.setOnClickListener {
                dismiss()
            }
            btnYes.setOnClickListener {
                listener?.invoke(true)

            }


            btnLike.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    likeListener?.invoke(true)
                }

                override fun unLiked(likeButton: LikeButton?) {
                    likeListener?.invoke(false)
                }
            })
        }
    }

    fun setOnLikeListener(block: SingleBlock<Boolean>) {
        likeListener = block
    }

    fun setOnUnLikeListener(block: SingleBlock<Boolean>) {
        likeListener = block
    }

    fun setOnYesClickListener(block: SingleBlock<Boolean>) {
        listener = block
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }

}