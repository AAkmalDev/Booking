package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.ItemOrderBinding
import uz.koinot.stadion.utils.SingleBlock
import uz.koinot.stadion.utils.toMoneyFormat

class DashboardOrderAdapter : RecyclerView.Adapter<DashboardOrderAdapter.VHolder>() {

    private var listener: SingleBlock<Order>? = null
    private var acceptListener: SingleBlock<Order>? = null
    private var rejectListener: SingleBlock<Order>? = null
    private val list = ArrayList<Order>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemOrderBinding) : RecyclerView.ViewHolder(view.root) {
        @SuppressLint("SetTextI18n")
        fun bind(d: Order) {
            view.apply {
                layoutAccept.isVisible = false
                userName.text =
                    "${if (d.firstName != "null") d.firstName else ""} ${if (d.lastName != "null") d.lastName else ""}"
                startDate.text = d.startDate
                endDate.text = d.endDate
                day.text = d.time
                sum.text = d.sum.toMoneyFormat()
                phone1.text = if (d.phoneNumber != null) d.phoneNumber else d.originalPhoneNumber
                count.text = d.countOrder.toInt().toString()
                btnAccept.setOnClickListener {
                    acceptListener?.invoke(d)
                }
                btnReject.setOnClickListener {
                    rejectListener?.invoke(d)
                }
            }
        }
    }

    fun submitList(ls: List<Order>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block: SingleBlock<Order>) {
        listener = block
    }

    fun setOnAcceptListener(block: SingleBlock<Order>) {
        acceptListener = block
    }

    fun setOnRejectListener(block: SingleBlock<Order>) {
        rejectListener = block
    }
}