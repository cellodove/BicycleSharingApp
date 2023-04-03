package com.cellodove.presentation.ui

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.cellodove.presentation.R
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentMainMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainMapFragment : BaseFragment<FragmentMainMapBinding>(FragmentMainMapBinding::inflate),OnMapReadyCallback {
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

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapFragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapFragment, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled=true
        startLocationPermissionRequest()
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