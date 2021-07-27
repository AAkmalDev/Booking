package uz.koinot.stadion.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Photos
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.ItemStadiumBinding
import uz.koinot.stadion.utils.SingleBlock
import uz.koinot.stadion.utils.setForceShowIcon
import uz.koinot.stadion.utils.toMoneyFormat

class StadiumAdapter : RecyclerView.Adapter<StadiumAdapter.VHolder>() {

    private var listener: SingleBlock<Stadium>? = null
    private var listenerAddImage: SingleBlock<Stadium>? = null
    private var updateListener: SingleBlock<Stadium>? = null
    private var deleteListener: SingleBlock<Stadium>? = null
    private var deleteImageListener: SingleBlock<Photos>? = null
    private var imageListener: ((List<Photos>, Int) -> Unit)? = null
    private val list = ArrayList<Stadium>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemStadiumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: VHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemStadiumBinding) : RecyclerView.ViewHolder(view.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(d: Stadium) {
            view.apply {
                stdName.text = d.name
                likeCount.text = d.stadium_like.toMoneyFormat()
                verifyCount.text = d.countVerify.toString()
                notVerifyCount.text = d.countNotVerify.toString()
                //  val ls = Gson().fromJson<List<String>>(d.photos,object : TypeToken<List<String>>(){}.type)
                val imageAdapter = RvImageAdapter(d, d.photos!!)
                rvImages.adapter = imageAdapter

                imageAdapter.setOnClickListener { stadium, pos ->
                    imageListener?.invoke(stadium, pos)
                }
                imageAdapter.setOnImageDeleteListener {
                    deleteImageListener?.invoke(it)
                }
                btnMore.setOnClickListener {
                    val popup = PopupMenu(it.context, it)
                    popup.inflate(R.menu.popup_menu)
                    popup.setForceShowIcon()
                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.update_menu -> {
                                updateListener?.invoke(d)
                            }
                            R.id.add_image_menu -> {
                                if (list.size < 10) listenerAddImage?.invoke(d)
                            }
                            R.id.delete_stadium -> {
                                deleteListener?.invoke(d)
                            }
                            else -> Unit
                        }
                        true
                    }
                    popup.show()
                }
            }
            itemView.setOnClickListener {
                listener?.invoke(d)
            }
        }
    }

    fun submitList(ls: List<Stadium>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block: SingleBlock<Stadium>) {
        listener = block
    }

    fun setOnUpdateClickListener(block: SingleBlock<Stadium>) {
        updateListener = block
    }

    fun setOnDeleteClickListener(block: SingleBlock<Stadium>) {
        deleteListener = block
    }

    fun setOnImageClickListener(block: (List<Photos>, Int) -> Unit) {
        imageListener = block
    }

    fun setOnImageDeleteListener(block: SingleBlock<Photos>) {
        deleteImageListener = block
    }

    fun setOnAddImageClickListener(block: (Stadium) -> Unit) {
        listenerAddImage = block
    }
}