package com.cellodove.presentation.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.cellodove.presentation.R
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentMainMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainMapBinding>(FragmentMainMapBinding::inflate),OnMapReadyCallback {
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE=1004
    }
    enum class PathStep{
        STARTING_POINT,ENDING_POINT,FINISH_POINT,FIND_ROOT
    }

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    private var startPoint = Pair(0.0,0.0)
    private var endPoint = Pair(0.0,0.0)
    private var pathStatus = PathStep.STARTING_POINT

    private val centerMarker = Marker()
    private val startMarker = Marker()
    private val endMarker = Marker()
    private val prePath = PathOverlay()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initUi()

        binding.btnConfirm.setOnClickListener {
            when(pathStatus){
                PathStep.STARTING_POINT -> {
                    changeUi(PathStep.ENDING_POINT)
                    startPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(startMarker, LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }

                PathStep.ENDING_POINT -> {
                    changeUi(PathStep.FINISH_POINT)
                    endPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(endMarker,LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)


                    prePath.coords = listOf(
                        LatLng(startPoint.second, startPoint.first),
                        LatLng(endPoint.second, endPoint.first)
                    )
                    prePath.color = ContextCompat.getColor(requireContext(),R.color.teal_200)
                    prePath.map = naverMap
                }

                PathStep.FINISH_POINT -> {
                    changeUi(PathStep.FIND_ROOT)
                    viewModel.getFindRoot("${startPoint.first},${startPoint.second}","${endPoint.first},${endPoint.second}")
                }

                PathStep.FIND_ROOT -> {

                }

            }
        }
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
    }

    private fun settingMarker(marker: Marker , latLng : LatLng, naverMap : NaverMap){
        marker.position = latLng
        marker.map = naverMap
    }

    private fun changeUi(pathStep : PathStep){
        pathStatus = pathStep
        when(pathStep){
            PathStep.STARTING_POINT -> {
                binding.btnConfirm.text = "출발지 등록"
            }

            PathStep.ENDING_POINT -> {
                binding.btnConfirm.text = "도착지 확인"
            }

            PathStep.FINISH_POINT -> {
                binding.btnConfirm.text = "길 찾기"
            }

            PathStep.FIND_ROOT -> {
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
                pathStatus = PathStep.STARTING_POINT
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