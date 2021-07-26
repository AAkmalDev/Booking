package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Photos
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.RvImageBinding
import uz.koinot.stadion.utils.SingleBlock

class RvImageAdapter(val stadium: Stadium, val list: List<Photos>) :
    RecyclerView.Adapter<RvImageAdapter.VHolder>() {

    private var imageListener: ((List<Photos>, Int) -> Unit)? = null
    private var deleteImageListener: SingleBlock<Photos>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        RvImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: RvImageBinding) : RecyclerView.ViewHolder(view.root) {
        @SuppressLint("SetTextI18n")
        fun bind(d: Photos) {
            view.imageView.load(d.link) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)

            }
            itemView.setOnClickListener {
                imageListener?.invoke(list, adapterPosition)
            }
            itemView.setOnLongClickListener {
                deleteImageListener?.invoke(d)
                true
            }

        }
    }

    fun setOnClickListener(block: (List<Photos>, Int) -> Unit) {
        imageListener = block
    }

    fun setOnImageDeleteListener(block: SingleBlock<Photos>) {
        deleteImageListener = block
    }
}