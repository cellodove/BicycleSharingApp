package com.cellodove.presentation.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
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
        STARTING_POINT,ENDING_POINT,FINISH_POINT
    }

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    private var startPoint = Pair(0.0,0.0)
    private var endPoint = Pair(0.0,0.0)
    private var pinStatus = PathStep.STARTING_POINT

    private val centerMarker = Marker()
    private val startMarker = Marker()
    private val endMarker = Marker()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initUi()

        binding.floatingButton.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.btnConfirm.setOnClickListener {
            if (pinStatus == PathStep.STARTING_POINT){
                pinStatus = PathStep.ENDING_POINT
                startPoint = Pair(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
                startMarker.position = LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
                startMarker.map = naverMap
                binding.btnConfirm.text = "도착지 확인"
            }else{
                endPoint = Pair(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
                endMarker.position = LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
                endMarker.map = naverMap

                val path = PathOverlay()
                path.color = ContextCompat.getColor(requireContext(),R.color.teal_200)
                path.coords = listOf(
                    LatLng(startPoint.first, startPoint.second),
                    LatLng(endPoint.first, endPoint.second)
                )
                path.map = naverMap

                Log.e("kkkk","${startPoint.first},${startPoint.second},${endPoint.first},${endPoint.second}")
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
        binding.apply {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            btnConfirm.text = "출발지 등록"
        }
        startMarker.icon = OverlayImage.fromResource(R.drawable.red_icon_location)
        endMarker.icon = OverlayImage.fromResource(R.drawable.blue_icon_location)
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

        // 카메라의 움직임에 대한 이벤트 리스너 인터페이스.
        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
            centerMarker.position = LatLng(
                // 현재 보이는 네이버맵의 정중앙 가운데로 마커 이동
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )

            // 주소 텍스트 세팅 및 확인 버튼 비활성화
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


        // 카메라의 움직임 종료에 대한 이벤트 리스너 인터페이스.
        naverMap.addOnCameraIdleListener {
            centerMarker.position = LatLng(
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            // 좌표 -> 주소 변환 텍스트 세팅, 버튼 활성화
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

    override fun observeViewModel() {}


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