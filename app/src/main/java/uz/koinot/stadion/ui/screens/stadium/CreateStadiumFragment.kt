package uz.koinot.stadion.ui.screens.stadium

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import ic0der.justtaxi.utils.AmountFormat
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentCreateStadiumBinding
import uz.koinot.stadion.ui.screens.home.MapFragment
import uz.koinot.stadion.utils.*
import javax.inject.Inject


@AndroidEntryPoint
class CreateStadiumFragment : Fragment(R.layout.fragment_create_stadium) {

    private var _bn: FragmentCreateStadiumBinding? = null
    val bn get() = _bn!!
    private var adress = ""
    private var stadiumId = 0
    private var type = true
    private lateinit var lan_lat: LatLng
    private lateinit var stadium: Stadium
    private val viewModel: CreateStadiumViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments?.getString(CONSTANTS.STADIUM_TYPE) != CONSTANTS.NEW_STADIUM) {
                type = false
                val arg = arguments?.getString(CONSTANTS.STADIUM_DATA)
                stadium = Gson().fromJson(arg, Stadium::class.java)
                lan_lat = LatLng(stadium.latitude,stadium.longitude)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateStadiumBinding.bind(view)

        bn.apply {
            location.setOnClickListener {
                showProgress(true)
                val dialog = MapFragment()
                dialog.setOnClickListener { latLng, s ->
                    showProgress(false)
                    lan_lat = latLng
                    adress = s
                    location.setText(s)
                    dialog.dismiss()
                }
                dialog.show(childFragmentManager,"nfjdbf")
            }
            if (type) {
                inputPhoneNumber.setText(storage.phoneNumber)
            } else {
                toolbar.title = getString(R.string.update_stadium)
                btnAddStadium.text = getString(R.string.update_stadium)
                inputPhoneNumber.setText(stadium.phone_number)
                location.setText(stadium.address)
                nameStadium.setText(stadium.name)
                if(stadium.opening_time != null) timeOpen.setText(stadium.opening_time!!.toNeedTime())
                if(stadium.closing_time != null) timeClose.setText(stadium.closing_time!!.toNeedTime())
                priceDay.setText(stadium.price_day_time.toString())
                priceNight.setText(stadium.price_night_time.toString())
                whenStartNightTime.setText(stadium.change_price_time?.toNeedTime())
                widthStadium.setText(stadium.width.toString())
                heightStadium.setText(stadium.height.toString())
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            timeOpen.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    timeOpen.setText("${hourOfDay.getString()}:${minute.getString()}")
                }, true).show(childFragmentManager, "qqqq")
            }
            timeClose.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    timeClose.setText("${hourOfDay.getString()}:${minute.getString()}")
                }, true).show(childFragmentManager, "zzz")
            }

            priceDay.keyListener = DigitsKeyListener.getInstance("1234567890 ")
            priceDay.addTextChangedListener(AmountFormat{ valid, amount->
                priceDay.requestFocus()

            })
            priceNight.keyListener = DigitsKeyListener.getInstance("1234567890 ")
            priceNight.addTextChangedListener(AmountFormat{ valid, amount->
                priceNight.requestFocus()

            })

            whenStartNightTime.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    whenStartNightTime.setText("${hourOfDay.getString()}:${minute.getString()}")
                }, true).show(childFragmentManager, "mmm")
            }

            btnAddStadium.setOnClickListener {
                Utils.closeKeyboard(requireActivity())
                val number = inputPhoneNumber.text.toString().trim()
                val location = location.text.toString().trim()
                val nameStadium = nameStadium.text.toString().trim()
                val timeOpen = timeOpen.text.toString().trim()
                val timeClose = timeClose.text.toString().trim()
                val priceDay = priceDay.text.toString().replace(" ","")
                val priceNight = priceNight.text.toString().replace(" ","")
                val timeNight = whenStartNightTime.text.toString().trim()
                val widht = widthStadium.text.toString().trim()
                val height = heightStadium.text.toString().trim()

                when{
                    number.length != 13 ->{
                        bn.inputPhoneNumber.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())
                    }

                    location.isEmpty() ->{
                        bn.location.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())
                    }

                    nameStadium.isEmpty() ->{
                        bn.nameStadium.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }

                    priceDay.isEmpty() ->{
                        bn.priceDay.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }

                    priceNight.isEmpty() ->{
                        bn.priceNight.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }

                    widht.isEmpty() ->{
                        bn.widthStadium.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }

                    height.isEmpty() ->{
                        bn.heightStadium.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())

                    }
                    timeNight.isEmpty() ->{
                        bn.whenStartNightTime.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                        vibrate(requireContext())
                    }
                    else ->{
                        if (type) {
                            viewModel.createStadium(
                                CreateStadium(null, nameStadium, lan_lat.latitude, number, lan_lat.longitude, location, timeOpen, timeClose, timeNight, priceDay.toDouble(), priceNight.toDouble(), true, widht.toInt(), height.toInt()))
                            Log.d("AAA","data:")
                        } else{
                            val data = CreateStadium(stadium.id, nameStadium, stadium.latitude, number, stadium.longitude, location, timeOpen, timeClose, timeNight, priceDay.toDouble(), priceNight.toDouble(), true, widht.toInt(), height.toInt())
                            viewModel.createStadium(data)
                            Log.d("AAA","data:$data")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createStadiumFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        showMessage(getString(R.string.success))
                        viewModel.getAllStadium()
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        showMessage(it.message)
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stadiumFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgress(false)
                        setToDb(it.data)
                        findNavController().popBackStack(R.id.homeFragment, false)
                    }
                    is UiStateList.ERROR -> {
                        showProgress(false)
                        showMessage(it.message)
                    }
                    is UiStateList.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setToDb(data: List<Stadium>?) {
            if(data != null && data.isNotEmpty()){
                lifecycleScope.launchWhenCreated {
                    viewModel.removeAllStadium()
                    viewModel.setAllStadium(data)
                }
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