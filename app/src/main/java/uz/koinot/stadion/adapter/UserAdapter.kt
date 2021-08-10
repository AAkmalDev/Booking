package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.koinot.stadion.data.model.SearchUser
import uz.koinot.stadion.databinding.ItemUsersBinding
import uz.koinot.stadion.utils.SingleBlock

class UserAdapter : RecyclerView.Adapter<UserAdapter.VHolder>() {

    private var listener: SingleBlock<SearchUser>? = null
    private val list = ArrayList<SearchUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemUsersBinding) : RecyclerView.ViewHolder(view.root) {

        @SuppressLint("SetTextI18n")
        fun bind(d: SearchUser) {
            view.apply {
                txPhoneNumber.text = d.phoneNumber
                txFullName.text = "${d.firstName} ${d.lastName}"

            }
            itemView.setOnClickListener {
                listener?.invoke(d)
            }
        }
    }

    fun submitList(ls: List<SearchUser>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block: SingleBlock<SearchUser>) {
        listener = block
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}