package com.cellodove.presentation.ui

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.drawerlayout.widget.DrawerLayout
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

    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationSource = FusedLocationSource(requireActivity(),LOCATION_PERMISSION_REQUEST_CODE)
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.floatingButton.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
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

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        startLocationPermissionRequest()

        val locationButtonView: LocationButtonView = binding.fabTracking
        locationButtonView.map = naverMap

        val marker = Marker()
        marker.position = LatLng(
            naverMap.cameraPosition.target.latitude,
            naverMap.cameraPosition.target.longitude
        )
        marker.icon = OverlayImage.fromResource(R.drawable.pin)
        marker.map = naverMap


        // 카메라의 움직임에 대한 이벤트 리스너 인터페이스.
        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
            marker.position = LatLng(
                // 현재 보이는 네이버맵의 정중앙 가운데로 마커 이동
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )

            // 주소 텍스트 세팅 및 확인 버튼 비활성화
            binding.tvLocation.run {
                text = "위치 이동 중"
                setTextColor(Color.parseColor("#c4c4c4"))
            }
            binding.btnConfirm.run {
                setBackgroundResource(R.drawable.rect_round_radius_blue)
                setTextColor(Color.parseColor("#ffffff"))
                isEnabled = false
            }
        }



        // 카메라의 움직임 종료에 대한 이벤트 리스너 인터페이스.
        naverMap.addOnCameraIdleListener {
            marker.position = LatLng(
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            // 좌표 -> 주소 변환 텍스트 세팅, 버튼 활성화
            binding.tvLocation.run {
                text = getAddress(
                    naverMap.cameraPosition.target.latitude,
                    naverMap.cameraPosition.target.longitude
                )
                setTextColor(Color.parseColor("#2d2d2d"))
            }
            binding.btnConfirm.run {
                setBackgroundResource(R.drawable.rect_round_radius_blue)
                setTextColor(Color.parseColor("#FF000000"))
                isEnabled = true
            }
        }
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // 좌표 -> 주소 변환
    private fun getAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult = "주소를 가져 올 수 없습니다."
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
            if (address.size > 0) {
                // 주소 받아오기
                val currentLocationAddress = address[0].getAddressLine(0)
                    .toString()
                addressResult = currentLocationAddress

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressResult
    }


    override fun observeViewModel() {
    }

    private fun launcher() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:${requireContext().packageName}"))
        permissionLauncher.launch(intent)
    }
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        startLocationPermissionRequest()
    }

}