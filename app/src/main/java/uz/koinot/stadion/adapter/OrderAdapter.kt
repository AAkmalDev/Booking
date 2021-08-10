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

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.VHolder>() {

    private var listener: SingleBlock<Order>? = null
    private var acceptListener: SingleBlock<Order>? = null
    private var rejectListener: SingleBlock<Order>? = null
    private var phoneNumberListener1: SingleBlock<String>? = null
    private var phoneNumberListener2: SingleBlock<String>? = null
    private var cancelOrderListener: SingleBlock<Long>? = null
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
                userName.text =
                    "${if (d.firstName != "null") d.firstName else ""} ${if (d.lastName != "null") d.lastName else ""}"
                timeOrder.text = "${d.startDate}-${d.endDate}"
                textDate.text = d.time
                sum.text = d.sum.toMoneyFormat()
                textPhone.text = if (d.phoneNumber != null) d.phoneNumber else d.originalPhoneNumber
                count.text = d.countOrder.toInt().toString()
                callPhone.setOnClickListener {
                    if (d.phoneNumber != null)
                        phoneNumberListener1?.invoke(d.phoneNumber!!)
                }

                btnAccept.setOnClickListener {
                    acceptListener?.invoke(d)
                }
                btnReject.setOnClickListener {
                    rejectListener?.invoke(d)
                }
                itemView.setOnLongClickListener {
                    cancelOrderListener?.invoke(d.id.toLong())
                    true
                }

                layoutAccept.isVisible = !d.active

            }

        }
    }

    fun submitList(ls: List<Order>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnCancelListener(block: SingleBlock<Long>) {
        cancelOrderListener = block
    }

    fun setOnAcceptListener(block: SingleBlock<Order>) {
        acceptListener = block
    }

    fun setOnRejectListener(block: SingleBlock<Order>) {
        rejectListener = block
    }

    fun setOnPhoneNumber1Listener(block: SingleBlock<String>) {
        phoneNumberListener1 = block
    }

    fun setOnPhoneNumber2Listener(block: SingleBlock<String>) {
        phoneNumberListener2 = block
    }
}