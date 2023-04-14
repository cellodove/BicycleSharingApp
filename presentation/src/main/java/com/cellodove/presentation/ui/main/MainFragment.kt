package com.cellodove.presentation.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cellodove.presentation.R
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentMainMapBinding
import com.cellodove.presentation.ui.main.MainActivity.Companion.ENDING_POINT
import com.cellodove.presentation.ui.main.MainActivity.Companion.FIND_BICYCLES
import com.cellodove.presentation.ui.main.MainActivity.Companion.FIND_ROOT
import com.cellodove.presentation.ui.main.MainActivity.Companion.PATH_STATUS
import com.cellodove.presentation.ui.main.MainActivity.Companion.STARTING_POINT
import com.cellodove.presentation.ui.main.MainActivity.Companion.START_USING
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainMapBinding>(FragmentMainMapBinding::inflate),OnMapReadyCallback {
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE=1004
        private const val FINISH_DELAY = 3000L
    }

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    private var pathStatus = STARTING_POINT

    private val centerMarker = Marker()
    private val startMarker = Marker()
    private val endMarker = Marker()
    private val bicyclesMarkerList = arrayListOf<Marker>()
    private val prePath = PathOverlay()
    private var bicycleSelect = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initUi()

        pathStatus = arguments?.getString(PATH_STATUS) ?: this.pathStatus

        binding.btnConfirm.setOnClickListener {
            when(pathStatus){
                STARTING_POINT -> {
                    changeUi(ENDING_POINT)
                    viewModel.startPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(startMarker, LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }

                ENDING_POINT -> {
                    changeUi(FIND_ROOT)
                    viewModel.endPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(endMarker,LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)

                    prePath.coords = listOf(
                        LatLng(viewModel.startPoint.second, viewModel.startPoint.first),
                        LatLng(viewModel.endPoint.second, viewModel.endPoint.first)
                    )
                    prePath.color = ContextCompat.getColor(requireContext(),R.color.teal_200)
                    prePath.map = naverMap
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                }

                FIND_ROOT -> {
                    viewModel.getFindRoot("${viewModel.startPoint.first},${viewModel.startPoint.second}","${viewModel.endPoint.first},${viewModel.endPoint.second}")
                }

                FIND_BICYCLES -> {
                    viewModel.getBicyclesLocation(naverMap.cameraPosition.target.longitude,naverMap.cameraPosition.target.latitude)
                }

                START_USING -> {
                    if (bicycleSelect){
                        Toast.makeText(requireContext(),"지금부터 사용을 시작합니다.",Toast.LENGTH_SHORT).show()
                        playKonfetti()
                        lifecycleScope.launch {
                            delay(FINISH_DELAY)
                            requireActivity().finish()
                        }
                    }else{
                        Toast.makeText(requireContext(),"사용할 자전거를 선택해주세요.",Toast.LENGTH_SHORT).show()
                    }
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

            FIND_ROOT -> {
                binding.btnConfirm.text = "길 찾기"
            }

            FIND_BICYCLES -> {
                binding.btnConfirm.text = "내 주변 자전거 찾기"
            }

            START_USING -> {
                binding.btnConfirm.text = "사용 시작하기!"
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
        returnAddressDataSetting()
    }

    fun returnAddressDataSetting(){
        val xValue = arguments?.getDouble(X_VALUE)
        val yValue = arguments?.getDouble(Y_VALUE)
        if (xValue != null && yValue != null){
            pathStatus = when(pathStatus){
                STARTING_POINT -> {
                    ENDING_POINT
                }
                ENDING_POINT -> {
                    FIND_ROOT
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
                    viewModel.startPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(startMarker, LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }
                FIND_ROOT -> {
                    viewModel.endPoint = Pair(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude)
                    settingMarker(startMarker, LatLng(viewModel.startPoint.second, viewModel.startPoint.first),naverMap)
                    settingMarker(endMarker,LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude),naverMap)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackPressedCallback.remove()
    }

    var waitTime = 0L
    private var onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when(pathStatus){
                STARTING_POINT -> {
                    if (System.currentTimeMillis() - waitTime >= 1500) {
                        waitTime = System.currentTimeMillis()
                        Toast.makeText(requireContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        requireActivity().finish()
                    }
                }

                ENDING_POINT -> {
                    changeUi(STARTING_POINT)
                    startMarker.map = null

                }

                FIND_ROOT -> {
                    changeUi(ENDING_POINT)
                    endMarker.map = null
                    prePath.map = null
                }

                FIND_BICYCLES -> {
                    changeUi(FIND_ROOT)
                    prePath.map = naverMap
                    findPath.map = null
                }

                START_USING -> {
                    for(i: Int in 0 until bicyclesMarkerList.size){
                        bicyclesMarkerList[i].map = null
                    }
                    bicyclesMarkerList.clear()
                    bicycleSelect = false
                    changeUi(FIND_BICYCLES)
                }
            }
        }
    }
    private val findPath = PathOverlay()
    override fun observeViewModel() {
        viewModel.findRootData.observe(viewLifecycleOwner){
            if (it.code=="0"){
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                changeUi(START_USING)
                prePath.map = null
                val pathList = it.route.traoptimal

                val pathContainer : MutableList<LatLng> = mutableListOf(LatLng(0.1,0.1))
                for(pathCords in pathList){
                    for(pathCordsXy in pathCords.path){
                        pathContainer.add(LatLng(pathCordsXy[1], pathCordsXy[0]))
                    }
                }
                findPath.coords = pathContainer.drop(1)
                findPath.color = Color.RED
                findPath.map = naverMap
                changeUi(FIND_BICYCLES)
            }else{
                pathStatus = STARTING_POINT
                binding.btnConfirm.text = "출발지 등록"
                startMarker.map = null
                endMarker.map = null
                prePath.map = null
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.getBicyclesLocationData.observe(viewLifecycleOwner){
            for (location in it){
                bicyclesMarkerList.add(Marker(LatLng(location[1], location[0]), OverlayImage.fromResource(R.drawable.bicycle_icon)))
            }

            for(i: Int in 0 until bicyclesMarkerList.size){
                bicyclesMarkerList[i].map = naverMap
                bicyclesMarkerList[i].setOnClickListener {
                    bicyclesMarkerList[i].captionText = "선택"
                    bicycleSelect = true
                    for(j: Int in 0 until bicyclesMarkerList.size){
                        if (i !=j ){
                            bicyclesMarkerList[j].captionText = ""
                        }
                    }
                    true
                }
            }
            changeUi(START_USING)
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

    private fun playKonfetti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)
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