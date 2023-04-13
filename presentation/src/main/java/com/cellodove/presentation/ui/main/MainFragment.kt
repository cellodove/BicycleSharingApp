package com.cellodove.presentation.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cellodove.presentation.R
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentMainMapBinding
import com.cellodove.presentation.ui.main.MainActivity.Companion.ENDING_POINT
import com.cellodove.presentation.ui.main.MainActivity.Companion.FIND_ROOT
import com.cellodove.presentation.ui.main.MainActivity.Companion.FINISH_POINT
import com.cellodove.presentation.ui.main.MainActivity.Companion.PATH_STATUS
import com.cellodove.presentation.ui.main.MainActivity.Companion.STARTING_POINT
import com.cellodove.presentation.ui.main.MainActivity.Companion.X_VALUE
import com.cellodove.presentation.ui.main.MainActivity.Companion.Y_VALUE
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainMapBinding>(FragmentMainMapBinding::inflate),OnMapReadyCallback {
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE=1004
    }

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    private var startPoint = Pair(0.0,0.0)
    private var endPoint = Pair(0.0,0.0)
    private var pathStatus = STARTING_POINT
    private var xValue : Double = 0.0
    private var yValue : Double = 0.0

    private val centerMarker = Marker()
    private val startMarker = Marker()
    private val endMarker = Marker()
    private val prePath = PathOverlay()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initUi()

        pathStatus = arguments?.getString(PATH_STATUS) ?: this.pathStatus

        binding.btnConfirm.setOnClickListener {
            when(pathStatus){
                STARTING_POINT -> {
                    changeUi(ENDING_POINT)
                    startPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(startMarker, LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }

                ENDING_POINT -> {
                    changeUi(FINISH_POINT)
                    endPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(endMarker,LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)


                    prePath.coords = listOf(
                        LatLng(startPoint.second, startPoint.first),
                        LatLng(endPoint.second, endPoint.first)
                    )
                    prePath.color = ContextCompat.getColor(requireContext(),R.color.teal_200)
                    prePath.map = naverMap
                }

                FINISH_POINT -> {
                    changeUi(FIND_ROOT)
                    viewModel.getFindRoot("${startPoint.first},${startPoint.second}","${endPoint.first},${endPoint.second}")
                }

                FIND_ROOT -> {

                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun initMap(){
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapFragmentMain) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapFragmentMain, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun initUi(){
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        startMarker.icon = OverlayImage.fromResource(R.drawable.red_icon_location)
        endMarker.icon = OverlayImage.fromResource(R.drawable.blue_icon_location)
        binding.floatingButton.setOnClickListener { binding.drawerLayout.openDrawer(Gravity.LEFT)}
        changeUi(pathStatus)

        binding.tvLocation.setOnClickListener{
            val bundle = Bundle()
            bundle.putString( PATH_STATUS , pathStatus)
            findNavController().navigate(R.id.fragment_search_address,bundle)
        }
    }

    private fun settingMarker(marker: Marker , latLng : LatLng, naverMap : NaverMap){
        marker.position = latLng
        marker.map = naverMap
    }

    private fun changeUi(pathStep : String){
        pathStatus = pathStep
        when(pathStep){
            STARTING_POINT -> {
                binding.btnConfirm.text = "출발지 등록"
            }

            ENDING_POINT -> {
                binding.btnConfirm.text = "도착지 확인"
            }

            FINISH_POINT -> {
                binding.btnConfirm.text = "길 찾기"
            }

            FIND_ROOT -> {
                binding.btnConfirm.text = "내 주변 자전거 찾기"
            }
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        startLocationPermissionRequest()
        val locationButtonView: LocationButtonView = binding.fabTracking
        locationButtonView.map = naverMap
        centerMarker.position = LatLng(
            naverMap.cameraPosition.target.latitude,
            naverMap.cameraPosition.target.longitude
        )
        centerMarker.icon = OverlayImage.fromResource(R.drawable.pin)
        centerMarker.map = naverMap

        naverMap.addOnCameraChangeListener { reason, animated ->
            centerMarker.position = LatLng(
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            binding.tvLocation.run {
                text = "위치 이동 중"
                setTextColor(ContextCompat.getColor(this.context,R.color.grey))
            }
            binding.btnConfirm.run {
                setBackgroundResource(R.drawable.rect_round_radius_blue)
                setTextColor(ContextCompat.getColor(this.context,R.color.grey))
                isEnabled = false
            }
        }

        naverMap.addOnCameraIdleListener {
            centerMarker.position = LatLng(
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            binding.tvLocation.run {
                text = viewModel.getAddress(
                    naverMap.cameraPosition.target.latitude,
                    naverMap.cameraPosition.target.longitude,
                    requireContext()
                )
                setTextColor(ContextCompat.getColor(this.context,R.color.black))
            }
            binding.btnConfirm.run {
                setBackgroundResource(R.drawable.rect_round_radius_blue)
                setTextColor(ContextCompat.getColor(this.context,R.color.black))
                isEnabled = true
            }
        }



        val xValue = arguments?.getDouble(X_VALUE)
        val yValue = arguments?.getDouble(Y_VALUE)
        if (xValue != null && yValue != null){
            pathStatus = when(pathStatus){
                STARTING_POINT -> {
                    ENDING_POINT
                }
                ENDING_POINT -> {
                    FINISH_POINT
                }
                else  -> {
                    STARTING_POINT
                }
            }
            changeUi(pathStatus)
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(yValue,xValue ))
            naverMap.moveCamera(cameraUpdate)
            when(pathStatus){
                ENDING_POINT -> {
                    startPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(startMarker, LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }
                FINISH_POINT -> {
                    endPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(endMarker,LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }
            }
        }
    }

    override fun observeViewModel() {
        viewModel.findRootData.observe(viewLifecycleOwner){
            if (it.code=="0"){
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                prePath.map = null
                val pathList = it.route.traoptimal
                val findPath = PathOverlay()
                val pathContainer : MutableList<LatLng> = mutableListOf(LatLng(0.1,0.1))
                for(pathCords in pathList){
                    for(pathCordsXy in pathCords.path){
                        pathContainer.add(LatLng(pathCordsXy[1], pathCordsXy[0]))
                    }
                }
                findPath.coords = pathContainer.drop(1)
                findPath.color = Color.RED
                findPath.map = naverMap
            }else{
                pathStatus = STARTING_POINT
                binding.btnConfirm.text = "출발지 등록"
                startMarker.map = null
                endMarker.map = null
                prePath.map = null
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationSource = FusedLocationSource(requireActivity(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            naverMap.locationSource = locationSource
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        } else {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("위치 서비스")
                setMessage("서비스 사용을 위해 위치 권한이 꼭 필요합니다.\n앱 설정에서 위치 권한을 허용해주세요.")
                setPositiveButton("확인") { _, _ -> launcher() }
                show()
            }
        }
    }
    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    private fun launcher() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:${requireContext().packageName}"))
        permissionLauncher.launch(intent)
    }
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        startLocationPermissionRequest()
    }

}