package uz.koinot.stadion.ui.screens.order.userorder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.CreateUserOrder
import uz.koinot.stadion.data.model.OrderHistory
import uz.koinot.stadion.data.model.StadiumOrder
import uz.koinot.stadion.data.room.AppDatabase
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentUserOrderBinding
import uz.koinot.stadion.utils.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UserOrderFragment : Fragment(R.layout.fragment_user_order) {


    private val viewModel: UserOrderViewModel by viewModels()
    private var _bn: FragmentUserOrderBinding? = null
    private val binding get() = _bn!!
    private val ber = true
    private var orderHistory: OrderHistory? = null

    private var stadiumId = 0L
    var arg: String? = null

    @Inject
    lateinit var storage: LocalStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arg = arguments?.getString(CONSTANTS.STADIUM_ORDER)
        val stadium = Gson().fromJson(arg, StadiumOrder::class.java)
        stadiumId = stadium.id.toLong()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bn = FragmentUserOrderBinding.bind(view)

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            if (ber) {
                Log.d("LocalPhoneNum", "onViewCreated: ${storage.userPhoneNumber}")
                inputPhoneNumber.setText(storage.userPhoneNumber)
            }


            inputDay.setOnClickListener {
                val calendar = Calendar.getInstance()
                val data = DatePickerDialog.newInstance { _, year, monthOfYear, dayOfMonth ->
                    val date = "$year-${monthOfYear + 1}-$dayOfMonth"
                    inputDay.setText(date)
                }
                data.minDate = calendar
                data.show(parentFragmentManager, "inputDay")
            }

            inputStartDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    val date1 = "${hourOfDay.getString()}:${minute.getString()}"
                    inputStartDate.setText(date1)
                    val date2 = inputEndDate.text.toString()
                    if (date2.isNotEmpty()) {
                        viewModel.getOrderPrice(stadiumId, date1, date2)
                    }
                }, true).show(parentFragmentManager, "aaa")
            }
            inputEndDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    val date1 = "${hourOfDay.getString()}:${minute.getString()}"
                    inputEndDate.setText(date1)
                    val date2 = inputStartDate.text.toString()
                    if (date2.isNotEmpty()) {
                        viewModel.getOrderPrice(stadiumId, date2, date1)
                    }
                }, true).show(parentFragmentManager, "ttt")
            }

            btnCreateOrder.setOnClickListener {
                Utils.closeKeyboard(requireActivity())
                val number = inputPhoneNumber.text.toString().trim()
                storage.userPhoneNumber = inputPhoneNumber.text.toString().trim()
                val day = inputDay.text.toString().trim()
                val startTime = inputStartDate.text.toString().trim()
                val endTime = inputEndDate.text.toString().trim()

                when {
                    number.length != 13 -> {
                        binding.inputPhoneNumber.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.shake
                            )
                        )
                        vibrate(requireContext())
                    }

                    day.isEmpty() -> {
                        binding.inputDay.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.shake
                            )
                        )
                        vibrate(requireContext())
                    }

                    startTime.isEmpty() -> {
                        binding.inputStartDate.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.shake
                            )
                        )
                        vibrate(requireContext())

                    }

                    endTime.isEmpty() -> {
                        binding.inputEndDate.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.shake
                            )
                        )
                        vibrate(requireContext())

                    }

                    else -> {
                        orderHistory = OrderHistory(
                            stadiumOrder = arg!!,
                            time = day,
                            stadiumId = stadiumId,
                            startDate = startTime,
                            endDate = endTime,
                            phoneNumber = number,
                            id = 1)

                        viewModel.createOrder(
                            CreateUserOrder(
                                null,
                                stadiumId,
                                day,
                                startTime,
                                endTime,
                                number
                            )
                        )
                    }
                }
            }
        }
        collects()
    }

    private fun addOrderDb(orderHistory: OrderHistory?) {
        viewModel.setUserOrder(orderHistory!!)
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createOrderFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        if (it.data){
                            addOrderDb(orderHistory)
                        }
                        showProgress(false)
                        showMessage("Successfully created order")
                        findNavController().navigateUp()
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage("Error Please Try again")
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderPriceFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        binding.loadingPrice.isVisible = false
                        binding.textOrderPrice.isVisible = true
                        showProgress(false)
                        if (it.data.toString().contains("-")) {
                            showMessage(getString(R.string.vaqt_kiritishda_xatolik))
                            binding.textOrderPrice.text = "0"
                            binding.inputEndDate.setText("")
                        } else
                            binding.textOrderPrice.text = it.data.toMoneyFormat()
                    }
                    is UiStateObject.ERROR -> {
                        binding.loadingPrice.isVisible = false
                        binding.textOrderPrice.isVisible = true
                        showProgress(false)
                    }
                    is UiStateObject.LOADING -> {
                        binding.loadingPrice.isVisible = true
                        binding.textOrderPrice.isVisible = false
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showProgress(status: Boolean) {
        binding.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }


}