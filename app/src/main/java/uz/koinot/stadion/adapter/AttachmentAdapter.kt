package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Photos
import uz.koinot.stadion.databinding.ItemAttachmentBinding
import uz.koinot.stadion.databinding.ItemImageBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.SingleBlock

class AttachmentAdapter: RecyclerView.Adapter<AttachmentAdapter.VHolder>() {

    private var listener : SingleBlock<Photos>? = null
    private var lll:((String)-> Unit)? = null
    private var acceptListener : SingleBlock<Photos>? = null
    private var rejectListener : SingleBlock<Photos>? = null
    private val list = ArrayList<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemAttachmentBinding): RecyclerView.ViewHolder(view.root){
        @SuppressLint("SetTextI18n")
        fun bind(d: Uri){
            view.imageView.setImageURI(d)

        }
    }

    fun addImage(uri: Uri){
        list.add(uri)
        notifyDataSetChanged()
    }

    fun getList() = list
    fun submitList(ls: List<Uri>){
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }
}