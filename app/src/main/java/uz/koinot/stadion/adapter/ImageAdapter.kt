package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Photos
import uz.koinot.stadion.databinding.ItemImageBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.SingleBlock
import uz.koinot.stadion.utils.toMoneyFormat

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.VHolder>() {

    private val list = ArrayList<Photos>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind(list[position].link)

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemImageBinding): RecyclerView.ViewHolder(view.root){
        @SuppressLint("SetTextI18n")
        fun bind(d: String){
            view.imageView.load(d){
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        }
    }

    fun submitList(ls: List<Photos>){
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }
}