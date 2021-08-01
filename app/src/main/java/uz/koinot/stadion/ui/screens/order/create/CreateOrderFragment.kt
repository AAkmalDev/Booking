package uz.koinot.stadion.ui.screens.order.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.UserAdapter
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentCreateOrderBinding
import uz.koinot.stadion.utils.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CreateOrderFragment : Fragment(R.layout.fragment_create_order) {

    @Inject
    lateinit var pref:LocalStorage

    private val viewModel: CreateOrderViewModel by viewModels()
    private var _bn: FragmentCreateOrderBinding? = null
    private val bn get() = _bn!!
    private var stadiumId = 0L
    private val adapter = UserAdapter()
    private var currentPhoneNumber = ""
    var job:Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            Log.d("AAA","stadiumId:$stadiumId")
            stadiumId = arguments?.getLong(CONSTANTS.STADION_ID)?:0L
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateOrderBinding.bind(view)

        bn.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            myLocation.setOnClickListener {
                currentPhoneNumber = pref.phoneNumber
                inputPhoneNumber.setText(pref.phoneNumber)
            }
            
            rvUsers.adapter = adapter

            adapter.setOnClickListener {
                adapter.clear()
                currentPhoneNumber = it.phoneNumber
                inputPhoneNumber.setText(it.phoneNumber)
            }
            inputDay.setOnClickListener {
                val dialog = DatePickerDialog.newInstance{ _, year, monthOfYear, dayOfMonth ->
                    inputDay.setText("${year.getString()}-${(monthOfYear + 1).getString()}-${dayOfMonth.getString()}")
                }
                dialog.minDate = Calendar.getInstance()
                    dialog.show(parentFragmentManager,"BBB")

            }
            inputStartDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    val date1 = "${hourOfDay.getString()}:${minute.getString()}"
                    inputStartDate.setText(date1)
                    val date2 = inputEndDate.text.toString()
                    if(date2.isNotEmpty()){
                        viewModel.getOrderPrice(stadiumId,date1,date2)
                    }
                },true).show(parentFragmentManager,"aaa")
            }
            inputEndDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    val date1 = "${hourOfDay.getString()}:${minute.getString()}"
                    inputEndDate.setText(date1)
                    val date2 = inputStartDate.text.toString()
                    if(date2.isNotEmpty()){
                        viewModel.getOrderPrice(stadiumId,date2,date1)
                    }
                },true).show(parentFragmentManager,"ttt")
            }
            inputPhoneNumber.addTextChangedListener { text ->
                if(text.toString().length > 4 && text.toString() != currentPhoneNumber){
                    searchJob(text.toString())
                    currentPhoneNumber = text.toString()
                }
            }
            btnCreateOrder.setOnClickListener {
                Utils.closeKeyboard(requireActivity())
                val number = inputPhoneNumber.text.toString().trim()
                val day = inputDay.text.toString().trim()
                val startTime = inputStartDate.text.toString().trim()
                val endTime = inputEndDate.text.toString().trim()

                when{
                    number.length != 13 ->{
                        bn.inputPhoneNumber.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())
                    }

                    day.isEmpty() ->{
                        bn.inputDay.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())
                    }

                    startTime.isEmpty() ->{
                        bn.inputStartDate.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }

                    endTime.isEmpty() ->{
                        bn.inputEndDate.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }

                    else ->{
                        viewModel.createOrder(CreateOrder(null,stadiumId,day,startTime,endTime,number))
                    }
                }
            }
        }
        collects()
    }

    private fun searchJob(toString: String) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(1000)
            viewModel.searchUser(toString)
        }
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createOrderFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgress(false)
                        showMessage("Successfully created order")
                        findNavController().navigateUp()
//                        clearData()
                    }
                    is UiStateObject.ERROR ->{
                        showProgress(false)
                        showMessage("Error Please Try again")
                    }
                    is UiStateObject.LOADING ->{
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchUserFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        if (it.data != null)
                        adapter.submitList(it.data)
                    }
                    is UiStateList.ERROR ->{
                        showMessage(getString(R.string.pease_try_again))
                    }
                    is UiStateList.LOADING ->{
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderPriceFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        bn.loadingPrice.isVisible = false
                        bn.textOrderPrice.isVisible = true
                        showProgress(false)
                        if(it.data.toString().contains("-")){
                            showMessage(getString(R.string.vaqt_kiritishda_xatolik))
                            bn.textOrderPrice.text = "0"
                            bn.inputEndDate.setText("")
                        }else
                             bn.textOrderPrice.text = it.data.toMoneyFormat()
                    }
                    is UiStateObject.ERROR ->{
                        bn.loadingPrice.isVisible = false
                        bn.textOrderPrice.isVisible = true
                        showProgress(false)
                    }
                    is UiStateObject.LOADING ->{
                        bn.loadingPrice.isVisible = true
                        bn.textOrderPrice.isVisible = false
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun clearData() {
        bn.apply {
            inputPhoneNumber.setText("+998")
            inputDay.text?.clear()
            inputStartDate.text?.clear()
            inputEndDate.text?.clear()
            textOrderPrice.text = "0"
        }
    }

    private fun showProgress(status:Boolean){
        bn.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}