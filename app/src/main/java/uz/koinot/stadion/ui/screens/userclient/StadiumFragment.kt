package uz.koinot.stadion.ui.screens.userclient

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.StadiumObject
import uz.koinot.stadion.databinding.FragmentStadiumBinding
import uz.koinot.stadion.ui.screens.dialog.StadiumDialog
import uz.koinot.stadion.utils.*

@AndroidEntryPoint
class StadiumFragment : Fragment(R.layout.fragment_stadium) {

    private val viewModel: StadiumViewModel by viewModels()
    private var _bn: FragmentStadiumBinding? = null
    private val binding get() = _bn!!

    private var mMap: GoogleMap? = null
    private var location: LatLng? = null
    private var marker: Marker? = null
    private lateinit var tracker: GPSTracker
    private var adress = ""
    private var name: String = ""

    private var listener: ((LatLng, String) -> Unit)? = null

    @SuppressLint("PotentialBehaviorOverride")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _bn = FragmentStadiumBinding.inflate(inflater, container, false)

        val map = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        map.getMapAsync {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
                mMap = it
                tracker = GPSTracker(requireContext())

                viewModel.getSsd()

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    viewModel.objectStadiumFlow.collect {
                        when (it) {
                            is UiStateObject.SUCCESS -> {
                                Log.d("StadiumList", "onCreate: ${it.data.toString()}")
                                val sta = it.data?.stadiums!!
                                for (stadium in sta) {
                                    val location = LatLng(stadium.lat, stadium.lng)
                                    marker = mMap!!.addMarker(MarkerOptions()
                                        .position(location)
                                        .title(stadium.name))
                                    marker!!.tag = stadium

                                    mMap!!.setOnMarkerClickListener { markerMap ->
                                        val ter = markerMap.tag as StadiumObject
                                        name = ter.name
                                        Log.d("OnMarkerListener", "onCreateView: ${ter.id}")
                                        Log.d("OnMarkerListener", "onCreateView: ${ter.name}")
                                        viewModel.getStadiumById(ter.id)
                                        true
                                    }
                                }
                            }
                            is UiStateObject.ERROR -> {
                                Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                            }
                            is UiStateObject.LOADING -> {
                                Toast.makeText(requireContext(), "LOADING", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else -> Unit
                        }
                    }
                }


                if (tracker.canGetLocation()) {
                    location = LatLng(tracker.getLatitude(), tracker.getLongitude())
                    adress = getCompleteAddressString(location!!.latitude,
                        location!!.longitude).toString()
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location!!, 13f))
                } else {
                    createLocationRequest()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.objectStadiumById.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        val stadiumOrder = it.data
                        val stadiumDialog = StadiumDialog(stadiumOrder, name)
                        Log.d("StadiumData", "onCreateView: ${stadiumOrder}")
                        stadiumDialog.setOnYesClickListener { _ ->
                            findNavController().navigate(R.id.userOrderFragment,
                                bundleOf(CONSTANTS.STADIUM_ORDER to Gson().toJson(stadiumOrder)))
                            stadiumDialog.dismiss()
                        }
                        stadiumDialog.show(parentFragmentManager, "asdasd")

                        Log.d("StadiumData", "onCreateView: ${it.data}")
                    }
                    is UiStateObject.LOADING -> {

                    }
                    is UiStateObject.ERROR -> {

                    }
                }
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnZoomIncrement.setOnClickListener {
            var zoom = mMap?.cameraPosition?.zoom!!
            mMap?.animateCamera(CameraUpdateFactory.zoomTo(++zoom))
        }

        binding.btnZoomDecriment.setOnClickListener {
            var zoom = mMap?.cameraPosition?.zoom!!
            mMap?.animateCamera(CameraUpdateFactory.zoomTo(--zoom))
        }

        binding.btnMyLocation.setOnClickListener {
            location = LatLng(tracker.getLatitude(), tracker.getLongitude())
            marker?.apply {
                position = location!!
                isVisible = true
            }
            adress = getCompleteAddressString(location!!.latitude, location!!.longitude).toString()
            //bn.textManzil.text = adress
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(tracker.getLatitude(),
                tracker.getLongitude()), 13f))
        }
    }

    private fun createLocationRequest() {
        try {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 2000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)

                val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
                val task: Task<LocationSettingsResponse> =
                    client.checkLocationSettings(builder.build())
                try {
                    task.addOnCompleteListener {
                        try {
                            it.getResult(ApiException::class.java)
                        } catch (e: ApiException) {
                            when (e.statusCode) {
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                    try {
                                        val resolvableApiException = e as ResolvableApiException
                                        try {
                                            resolvableApiException.startResolutionForResult(activity,
                                                101)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } catch (e: IntentSender.SendIntentException) {
                                        e.printStackTrace()
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}