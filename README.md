# 네이버 맵API를 사용해 어플 만들기

네이버 맵 api를 사용해 공유 자전거 어플을 만들었습니다.

출발 지점과 도착지점은 지도에서 선택하거나 주소를 검색해 설정할 수 있습니다.

지도에서 출발지점과 도착지점을 설정하면 최적의 루트가 지도에 표시됩니다.

루트가 설정되면 내위치 주변에 랜덤하게 공유자전거를 표시해 선택할 수 있습니다.

어플을 만드는데 사용한 기술들은 아래와 같습니다.

## 기술

### Language

- Kotlin

### Architecture

- MVVM

### Library

- Retrofit2
- Paging3
- Naver Map
- Konfetti

### Design Pattern

- Clean Architecture

### DI

- Hilt

### Etc

- Coroutine - Flow
- Multi Module
- Navigation Component
- Lifecycle
- DataBinding

## 구조

먼저 멀티모듈로 클린아키텍처 구조를 만들었습니다. 그리고 MVVM패턴을 사용했습니다.

- buildSrc - dependency 관리
- domain - UseCase 및 Model
- data - 서버 통신 및 모델 변환
- presentation - 화면과 입력에 대한 처리
- app - DI 세팅

모듈별로 앱을 설명해 나가겠습니다.

![image1.png](/image/image1.png)

## ****buildSrc****

모듈이 많다보니 dependency를 관리가 까다롭습니다. 이때 buildSrc를 사용해 한번해 관리할 수가있습니다.

- Versions.kt

```kotlin
object Versions{
    const val KOTLINX_COROUTINES = "1.5.0"
    const val STDLIB ="1.6.21"

    const val CORE_KTX = "1.8.0"
    const val APP_COMPAT = "1.4.2"
    const val ACTIVITY_KTX = "1.4.0"
    const val FRAGMENT_KTX = "1.4.1"

    const val LIFECYCLE_KTX = "2.4.1"

    const val HILT = "2.40.5"
    const val MATERIAL = "1.6.1"

    const val RETROFIT = "2.9.0"
    const val OKHTTP = "4.9.3"

    const val JUNIT = "4.13.2"
    const val ANDROID_JUNIT = "1.1.2"
    const val ESPRESSO_CORE = "3.4.0"

    const val GLIDE_VER = "4.12.0"
    const val PAGING_VERSION = "3.1.1"

    const val JETPACK_NAVIGATION = "2.5.3"
    const val NAVER_MAP = "3.16.0"
    const val PLAY_SERVICES_LOCATION = "18.0.0"
    const val KONFETTI = "2.0.2"

}

object Kotlin {
    const val KOTLIN_STDLIB      = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.STDLIB}"
    const val COROUTINES_CORE    = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLINX_COROUTINES}"
}

object AndroidX {
    const val CORE_KTX       = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val APP_COMPAT     = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"

    const val ACTIVITY_KTX            = "androidx.activity:activity-ktx:${Versions.ACTIVITY_KTX}"
    const val FRAGMENT_KTX            = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX}"
    const val LIFECYCLE_VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE_KTX}"
    const val LIFECYCLE_LIVEDATA_KTX  = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE_KTX}"
}

object Google {
    const val HILT_ANDROID          = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val HILT_ANDROID_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.HILT}"
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
}

object JetpackPack{
    const val NAVIGATION_FRAGMENT  = "androidx.navigation:navigation-fragment-ktx:${Versions.JETPACK_NAVIGATION}"
    const val NAVIGATION_UI        = "androidx.navigation:navigation-ui-ktx:${Versions.JETPACK_NAVIGATION}"
    const val NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:${Versions.JETPACK_NAVIGATION}"
    const val NAVIGATION_FRAGMENT_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.JETPACK_NAVIGATION}"

    const val NAVIGATION_DYNAMIC   = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.JETPACK_NAVIGATION}"
    const val NAVIGATION_TEST_IMP  = "androidx.navigation:navigation-testing:${Versions.JETPACK_NAVIGATION}"
}

object Libraries {
    const val RETROFIT                   = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    const val RETROFIT_CONVERTER_GSON    = "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT}"
    const val OKHTTP_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP}"
    const val NAVER_MAP                  = "com.naver.maps:map-sdk:${Versions.NAVER_MAP}"
    const val PLAY_SERVICES_LOCATION     = "com.google.android.gms:play-services-location:${Versions.PLAY_SERVICES_LOCATION}"
    const val PAGING_RUNTIME = "androidx.paging:paging-runtime:${Versions.PAGING_VERSION}"
    const val PAGING_COMMON  = "androidx.paging:paging-common:${Versions.PAGING_VERSION}"
    const val KONFETTI       = "nl.dionsegijn:konfetti-xml:${Versions.KONFETTI}"
}

object Test {
    const val JUNIT         = "junit:junit:${Versions.JUNIT}"
    const val ANDROID_JUNIT = "androidx.test.ext:junit:${Versions.ANDROID_JUNIT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}
```

- build.gradle(:app)

```kotlin
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-parcelize'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

apply from: rootProject.file("android.gradle")

dependencies {
    implementation project(":data")
    implementation project(":domain")
    implementation project(":presentation")

    implementation(AndroidX.CORE_KTX)
    implementation(AndroidX.APP_COMPAT)
    implementation(Kotlin.KOTLIN_STDLIB)

    implementation(Libraries.RETROFIT)
    implementation(Libraries.RETROFIT_CONVERTER_GSON)
    implementation(Libraries.OKHTTP_LOGGING_INTERCEPTOR)

    implementation(Google.HILT_ANDROID)
    kapt (Google.HILT_ANDROID_COMPILER)

    implementation(Libraries.PAGING_RUNTIME)
    implementation(Libraries.PAGING_COMMON)

    testImplementation ('junit:junit:4.13.2')
    androidTestImplementation ('androidx.test.ext:junit:1.1.5')
    androidTestImplementation ('androidx.test.espresso:espresso-core:3.5.1')

}
```

## Domain 계층

![image2.png](/image/image2.png)

### Model

- FindRootResponse

```kotlin
package com.cellodove.domain.model

data class FindRootResponse(
    val code : String,
    val message : String,
    val currentDateTime : String,
    val route : Route
)

data class Route(
    val traoptimal : List<RouteUnitEnt>
)

data class RouteUnitEnt(
    val summary : ResultDistance,
    val path : List<List<Double>>
)

data class ResultDistance(
    val distance : String
)
```

- SearchAddressResponse

```kotlin
package com.cellodove.domain.model

data class SearchAddressResponse(
    val status : String,
    val errorMessage : String,
    val meta : AddressMeta,
    val addresses : List<DomainAddresses>,
    )

data class AddressMeta(
    val totalCount : Int,
    val page : Int,
    val count : Int,
)

data class DomainAddresses(
    val roadAddress : String,
    val jibunAddress :String,
    val x : Double,
    val y : Double
)
```

Domain 계층의 Model입니다. MapRepository의 정보를 가지고 있으며 안드로이드의 의존성을 갖지 않도록 작성해줍니다. 다만 데이터를 표출할때 Paging3 라이브러리를 사용하는데 페이징데이터를 가지고와야하기 때문에 페이징관련 라이브러리는 추가해주어야합니다.

### **Repository**

- MapRepository

```kotlin
interface MapRepository {
    suspend fun findRoot(startPoint : String, endPoint : String) : FindRootResponse

    fun searchAddressPaging(addressQuery : String) : Flow<PagingData<DomainAddresses>>
}
```

길찾기 및 주소 검색 API의 Repository 목록을 가져오기 위한 Repository의 인터페이스를 만들어줍니다. MapRepository의 구현체는 Data 계층에 위치합니다.

### **UseCase**

- FindRootUseCase

```kotlin
interface FindRootUseCase {
    suspend fun getRootData(startPoint: String, endPoint: String): FindRootResponse
}

class FindRootUseCaseImpl @Inject constructor(private val mapRepository: MapRepository) :
    FindRootUseCase {
    override suspend fun getRootData(startPoint: String, endPoint: String): FindRootResponse {
        return mapRepository.findRoot(startPoint, endPoint)
    }
}
```

- SearchAddressUseCase

```kotlin
interface SearchAddressUseCase {
    fun getAddressPagingData(addressQuery: String): Flow<PagingData<DomainAddresses>>
}

class SearchAddressUseCaseImpl @Inject constructor(private val mapRepository: MapRepository) :
    SearchAddressUseCase {
    override fun getAddressPagingData(addressQuery: String): Flow<PagingData<DomainAddresses>> {
        return mapRepository.searchAddressPaging(addressQuery)
    }
}
```

길찾기 및 주소 검색 API에서 Repository 목록을 가져오는 기능을 제공하는 유스케이스입니다. MapRepository를 생성자로 주입받아 데이터를 가져오는 역할을 합니다.

- BicyclesLocationUseCase

```kotlin
interface BicyclesLocationUseCase {
    fun getRandomLocation(x:Double, y:Double) : List<List<Double>>
}

class BicyclesLocationUseCaseImpl @Inject constructor() : BicyclesLocationUseCase {

    override fun getRandomLocation(x:Double, y:Double) : List<List<Double>> {
        val xPlus = x + 0.0009
        val xMinus = x - 0.0009
        val yPlus = y + 0.0009
        val yMinus = y - 0.0009

        val bicyclesLocationList = arrayListOf<List<Double>>()

        for (i in 0 .. 7){
            val x = ThreadLocalRandom.current().nextDouble( xMinus, xPlus)
            val y = ThreadLocalRandom.current().nextDouble( yMinus, yPlus)
            bicyclesLocationList.add(mutableListOf(x,y))
        }
        return bicyclesLocationList
    }
}
```

내 주변 공유자전거의 랜덤한 위치를 가져오는 기능을 제공하는 유스케이스입니다.

## Data 계층

![image3.png](/image/image3.png) 

### Model

- NaverDrivingResponse

```kotlin
data class NaverDrivingResponse(
    @SerializedName("code") val code : String,
    @SerializedName("message") val message : String,
    @SerializedName("currentDateTime") val currentDateTime : String,
    @SerializedName("route") val route : Route
)

data class Route(
    @SerializedName("traoptimal") val traoptimal : List<RouteUnitEnt>
)

data class RouteUnitEnt(
    @SerializedName("summary") val summary : ResultDistance,
    @SerializedName("path") val path : List<List<Double>>
)

data class ResultDistance(
    @SerializedName("distance") val distance : String
)
```

길 찾기 API를 사용해 데이터를 가져올 model입니다.

- NaverSearchAddressResponse

```kotlin
data class NaverSearchAddressResponse(
    @SerializedName("status") val status : String,
    @SerializedName("errorMessage") val errorMessage : String,
    @SerializedName("meta") val meta : AddressMeta,
    @SerializedName("addresses") val addresses : List<Addresses>,

    )

data class AddressMeta(
    @SerializedName("totalCount") val totalCount : Int,
    @SerializedName("page") val page : Int,
    @SerializedName("count") val count : Int,
)

data class Addresses(
    @SerializedName("roadAddress") val roadAddress : String,
    @SerializedName("jibunAddress") val jibunAddress : String,
    @SerializedName("x") val x : Double,
    @SerializedName("y") val y : Double
)
```

주소 검색 API를 사용해 데이터를 가져올 model입니다.

### Mapper

```kotlin
fun mapperToFindRootResponse(naverDrivingResponse : NaverDrivingResponse) : FindRootResponse {

    val mapperRoute = Route(
        traoptimal = naverDrivingResponse.route.traoptimal.map {
            RouteUnitEnt(
                summary = ResultDistance(it.summary.distance),
                path = it.path
            )
        }
    )

    return FindRootResponse(
        code = naverDrivingResponse.code,
        message = naverDrivingResponse.message,
        currentDateTime = naverDrivingResponse.currentDateTime,
        route = mapperRoute
    )
}

fun mapperToAddress(addresses: List<Addresses>) : List<DomainAddresses> {
    return addresses.toList().map {
        DomainAddresses(
            roadAddress = it.roadAddress,
            jibunAddress = it.jibunAddress,
            x = it.x,
            y = it.y
        )
    }
}
```

mapper를 사용해 서버에서오는 데이터를 domain 데이터로 변환해 줍니다.

### Service

- NaverService

```kotlin
interface NaverService {
    @GET("/map-direction/v1/driving")
    suspend fun getDrivingRoot(@Query("start", encoded = true) start : String, @Query("goal", encoded = true) goal : String): NaverDrivingResponse

    @GET("/map-geocode/v2/geocode")
    suspend fun getAddress(
        @Query(value = "query") query : String,
        @Query(value = "page") page: Int,
    ) : NaverSearchAddressResponse
}
```

네이버 맵 API를 호출할 수 있게 레트로핏으로 세팅해줍니다.

### Source

- FindRootDataSource

```kotlin
interface FindRootDataSource {
    suspend fun getRoot(start : String, goal : String) : FindRootResponse
}

class FindRootDataSourceImpl @Inject constructor(
    private val naverDrivingService: NaverService
) : FindRootDataSource {
    override suspend fun getRoot(start: String, goal: String): FindRootResponse {
        val response = naverDrivingService.getDrivingRoot(start, goal)
        return if (ResultCode.by(response.code) == ResultCode.SUCCESS){
            mapperToFindRootResponse(naverDrivingService.getDrivingRoot(start, goal))
        }else{
            arrayListOf<RouteUnitEnt>()
            val route = Route(
                traoptimal = listOf()
            )
            FindRootResponse(response.code,response.message,"",route)
        }
    }
}

enum class ResultCode(val value : String){
    SUCCESS("0"),
    START_GOAL_SAME("1"),
    START_OR_GOAL_IS_NOT_ROAD("2"),
    NOT_CAR_ROOT_RESULT("3"),
    STOPOVER_IS_NOT_AROUND_ROOD("4"),
    GOAL_IS_SO_FAR("5");
    companion object {
        fun by(value : String) = enumValues<ResultCode>().find { it.value == value } ?: ""
    }
}
```

시작 좌표와 도착 좌표를 보내면 시작부터 도착까지 필요한 경로의 좌표를 리스트로 보내줍니다.

- SearchAddressDataSource

```kotlin
interface SearchAddressDataSource{
    fun getAddress(query:String) : PagingSource<Int, DomainAddresses>
}
const val SEARCH_STARTING_PAGE_INDEX = 1

class SearchAddressDataSourceImpl @Inject constructor(private val naverService: NaverService):SearchAddressDataSource{

    override fun getAddress(query:String): PagingSource<Int, DomainAddresses> {
        return object : PagingSource<Int, DomainAddresses>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DomainAddresses> {
                val position = params.key ?: SEARCH_STARTING_PAGE_INDEX
                return try {
                    val response = naverService.getAddress(query, position)
                    val addressResponse = response.addresses
                    val nextKey = if (response.addresses.isEmpty()) {
                        null
                    } else {
                        position + 1
                    }
                    LoadResult.Page(
                        data = mapperToAddress(addressResponse),
                        prevKey = if (position == SEARCH_STARTING_PAGE_INDEX) null else position - 1,
                        nextKey = nextKey
                    )
                } catch (exception: IOException) {
                    LoadResult.Error(exception)
                } catch (exception: HttpException) {
                    LoadResult.Error(exception)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, DomainAddresses>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }
}
```

페이징을 사용하기에 페이징에 맞는 데이터를 가져와야합니다. 그래서 PagingSource를 사용해 DataSource를 만들어줍니다.

### RepositoryImpl

- MapRepositoryImpl

```kotlin
class MapRepositoryImpl@Inject constructor(
    private val findRootDataSource : FindRootDataSource,
    private val searchAddressDataSource: SearchAddressDataSource
) : MapRepository {

    override suspend fun findRoot(startPoint: String, endPoint: String): FindRootResponse {
        return findRootDataSource.getRoot(startPoint,endPoint)
    }

    override fun searchAddressPaging(addressQuery: String): Flow<PagingData<DomainAddresses>> {
        return Pager(config = PagingConfig(pageSize = NETWORK_PAGE_SIZE , enablePlaceholders = false),
        pagingSourceFactory = {searchAddressDataSource.getAddress(addressQuery)}).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}
```

Domain 계층의 MapRepository 인터페이스를 구현합니다.

NaverService를 생성자로 주입받아 findRootDataSource, searchAddressDataSource로 전달해 데이터를 가져오게 됩니다.

## ****Presentation 계층****

![image4.png](/image/image4.png)

### Manifest

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.cellodove.presentation">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.BicycleSharing"
        tools:targetApi="31">

        <meta-data
            android:name="com.naver.maps.map.CLIENT_ID"
            android:value="CLIENT_ID" />

        <activity
            android:name="com.cellodove.presentation.ui.main.MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>

</manifest>
```

지도를 사용하려면 정확한 위치 권한이 필요하기에 해당권한을 추가해줍니다.

그리고 네이버맵 SDK를 사용하기 위해서는 매타 데이터에 아이디를 입력해야합니다.

### Base

중복 되는 코드들을 따로빼서 관리해줍니다. 여기서는 간단하게 컴포넌트별 기본으로 구현해야하는 내용만 작성하였습니다.

- BaseActivity

```kotlin
abstract class BaseActivity<VB: ViewBinding>(private val bindingInflater:(inflater : LayoutInflater) -> VB): AppCompatActivity() {
    lateinit var binding: VB

    abstract fun observeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
    }
}
```

- BaseFragment

```kotlin
abstract class BaseFragment<VB: ViewBinding>(private val bindingInflater:(inflater: LayoutInflater) -> VB) : Fragment() {
    lateinit var binding : VB
    abstract fun observeViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }
}
```

### Main

- MainViewModel

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val findRootUseCase : FindRootUseCase,
    private val searchAddressUseCase : SearchAddressUseCase,
    private val bicyclesLocationUseCase : BicyclesLocationUseCase
) : ViewModel() {

    private val _findRootData = MutableLiveData<FindRootResponse>()
    val findRootData : LiveData<FindRootResponse> = _findRootData

    private val _getBicyclesLocationData = MutableLiveData<List<List<Double>>>()
    val getBicyclesLocationData : LiveData<List<List<Double>>> = _getBicyclesLocationData

    var startPoint = Pair(0.0,0.0)
    var endPoint = Pair(0.0,0.0)

    fun getAddress(lat: Double, lng: Double, context : Context): String {
        val geoCoder = Geocoder(context, Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult = "주소를 가져 올 수 없습니다."
        try {
            address = geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
            if (address.size > 0) {
                val currentLocationAddress = address[0].getAddressLine(0).toString()
                addressResult = currentLocationAddress
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressResult
    }

    fun getFindRoot(startPoint : String, endPoint : String){
        viewModelScope.launch {
            _findRootData.value = findRootUseCase.getRootData(startPoint,endPoint)
        }
    }

    fun searchAddress(query: String): Flow<PagingData<DomainAddresses>> {
        return searchAddressUseCase.getAddressPagingData(query).cachedIn(viewModelScope)
    }

    fun getBicyclesLocation(x:Double, y:Double){
        _getBicyclesLocationData.value = bicyclesLocationUseCase.getRandomLocation(x,y)
    }
}
```

ViewModel에서는 Domain 계층의 유스케이스를 주입받아 데이터를 가져옵니다. Presentation 계층에서는 Data 계층의 의존성이 없기 때문에 Data 계층 데이터를 가져오는 구현체에 직접적으로 접근은 불가능합니다.

LiveData와 Flow를 사용해 데이터를 처리합니다.

- MainActivity

```kotlin
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    
		override fun observeViewModel() = Unit

    companion object{
        const val PATH_STATUS = "PATH_STATUS"

        const val STARTING_POINT = "STARTING_POINT"
        const val ENDING_POINT = "ENDING_POINT"

        const val FIND_ROOT = "FIND_ROOT"
        const val FIND_BICYCLES = "FIND_BICYCLES"
        const val START_USING = "START_USING"

        const val X_VALUE = "X_VALUE"
        const val Y_VALUE = "Y_VALUE"
    }
}
```

네비게이션 컴포넌트를 사용하여 액티비티에는 따로 작성을 하지 않았습니다.

### navigation

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_main"
    app:startDestination="@id/fragment_splash">

    <fragment
        android:id="@+id/fragment_splash"
        android:name="com.cellodove.presentation.ui.splash.SplashFragment"
        tools:layout="@layout/fragment_splash">
        <action
            android:id="@+id/action_splash_to_home"
            app:destination="@id/fragment_main_map"/>
    </fragment>

    <fragment
        android:id="@+id/fragment_main_map"
        android:name="com.cellodove.presentation.ui.main.MainFragment"
        tools:layout="@layout/fragment_main_map">
        <action
            android:id="@+id/openAddressSearch"
            app:destination="@id/fragment_search_address"/>
    </fragment>

    <fragment
        android:id="@+id/fragment_search_address"
        android:name="com.cellodove.presentation.ui.search.SearchFragment"
        tools:layout="@layout/fragment_address_search">
    </fragment>

</navigation>
```

![image5.png](/image/image5.png)

네비게이션에서 액션을 사용해 화면을 이동합니다.

- MainFragment

```kotlin
@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainMapBinding>(FragmentMainMapBinding::inflate),OnMapReadyCallback {
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE=1004
        private const val FINISH_DELAY = 3000L
    }

    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initUi()
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
```

해당 프래그먼트는 네이버 맵을 호출합니다. 그리고 맵 콜백을 등록하여 네이버 맵이 준비되면 초기화를 진행해 줍니다. 내위치를 가져오기 위해서는 먼저 위치 권한을 받아야합니다. 앱 설치후 권한을 받지 않았으면 권한을 받아옵니다.

![gif1.gif](/image/gif1.gif)

만약 권한을 거부했을경우 팝업을 띄운뒤 설정화면으로 유도합니다.

![gif2.gif](/image/gif2.gif)

화면은 출발지, 도착지, 길찾기, 주변 자전거 찾기, 시작하기, 총 5개의 스탭으로 바뀌도록 만들었습니다.

1. 출발지 등록을 누르면 해당 위치의 좌표값을 뷰모델에 저장합니다.
2. 도착지 등록을 누르면 해당 위치의 좌표값을 뷰모델에 저장합니다. 그리고 출발지와 도착지를 가로지르는 라인이 생성됩니다.
3. 길찾기를 누르면 해당 좌표를 사용해 길찾기 데이터를 가지고와 맵에 표시해줍니다.
4. 주변 자전거 찾기 버튼을 누르면 랜덤하게 자전거 위치가 표시됩니다.
5. 하나의 자전거를 선택한뒤 시작하기 버튼을 누르면 사용시작 토스트 메시지가 발생하고 앱이 종료됩니다.

뒤로가기 키를 누르면 한 단계씩 뒤로 돌아가게됩니다.

```kotlin
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

}
```

### Search

- AddressViewHolder

```kotlin
class AddressViewHolder(private val binding : AddressListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(domainAddresses: DomainAddresses){
        showData(domainAddresses)
    }

    private fun showData(domainAddresses: DomainAddresses){
        binding.apply {
            roadAddress.text = domainAddresses.roadAddress
            jibunAddress.text = domainAddresses.jibunAddress
        }
    }

    companion object{
        fun create(parent : ViewGroup) : AddressViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            return AddressViewHolder(AddressListItemBinding.inflate(layoutInflater,parent,false))
        }
    }
}
```

리사이클러뷰 표출을위해 먼저 뷰홀더를 만들어주었습니다.

- SearchAdapter

```kotlin
class SearchAdapter : PagingDataAdapter<DomainAddresses,AddressViewHolder>(ADDRESS_COMPARATOR){
    private lateinit var itemClickListener : OnItemClickListener
    interface OnItemClickListener{
        fun onClick(domainAddresses : DomainAddresses)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bind(item)
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
       return AddressViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object{
        private val ADDRESS_COMPARATOR = object : DiffUtil.ItemCallback<DomainAddresses>(){
            override fun areItemsTheSame(
                oldItem: DomainAddresses, newItem: DomainAddresses
            ): Boolean = oldItem.roadAddress == newItem.roadAddress && oldItem.jibunAddress == newItem.jibunAddress

            override fun areContentsTheSame(
                oldItem: DomainAddresses, newItem: DomainAddresses
            ): Boolean =  oldItem == newItem
        }
    }
}
```

어댑터에 클릭리스너를 추가해주었습니다.

- SearchFragment

```kotlin
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate){

    private val viewModel : MainViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter()
    private var searchJob: Job? = null
    private var oldQuery = ""
    private var newQuery = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi(){
        val pathStatus = arguments?.getString(PATH_STATUS) ?: ""

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.searchRecycler.adapter = searchAdapter
        binding.searchRecycler.addItemDecoration(decoration)

        binding.etQuery.setOnEditorActionListener { _, actionId, _ ->
            newQuery = binding.etQuery.text.toString()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                when {
                    binding.etQuery.text.toString().isEmpty() -> {
                        binding.inputLayout.error = "텍스트를 입력해 주세요."
                    }
                    newQuery == oldQuery -> Unit
                    else -> {
                        oldQuery = newQuery
                        hideKeyboard()
                        binding.inputLayout.error = null
                        searchAddress(binding.etQuery.text.toString())
                        binding.searchRecycler.scrollToPosition(0)
                    }
                }
                true
            } else {
                false
            }
        }

        binding.etQuery.doOnTextChanged { _, _, _, _ ->
            binding.inputLayout.error = null
        }

        binding.inputLayout.setEndIconOnClickListener {
            newQuery = binding.etQuery.text.toString()
            when {
                binding.etQuery.text.toString().isEmpty() -> {
                    binding.inputLayout.error = "텍스트를 입력해 주세요."
                }
                newQuery == oldQuery -> Unit
                else -> {
                    oldQuery = newQuery
                    hideKeyboard()
                    binding.inputLayout.error = null
                    searchAddress(binding.etQuery.text.toString())
                    binding.searchRecycler.scrollToPosition(0)
                }
            }
        }

        searchAdapter.setItemClickListener(object : SearchAdapter.OnItemClickListener{
            override fun onClick(domainAddresses: DomainAddresses) {
                val bundle = Bundle()
                bundle.putString(PATH_STATUS , pathStatus)
                bundle.putDouble(X_VALUE , domainAddresses.x)
                bundle.putDouble(Y_VALUE ,domainAddresses.y)
                findNavController().navigate(R.id.fragment_main_map,bundle)
            }
        })

        binding.retryButton.setOnClickListener {
            searchAdapter.retry()
        }
    }

    private fun searchAddress(query :String){
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchAddress(query).collectLatest {
                binding.searchNothing.isVisible = false
                binding.errorLayout.isVisible = false
                binding.searchRecycler.visibility = View.VISIBLE
                searchAdapter.submitData(it)
            }
        }
    }

    private fun statusList() {
        searchAdapter.addLoadStateListener { loadState ->
            loadState.decideOnState(
                adapter = searchAdapter,
                showLoading = { visible ->
                    binding.progressBar.isVisible = visible
                    binding.searchNothing.isVisible = false
                    binding.errorLayout.isVisible = false
                },
                showEmptyState = { visible ->
                    binding.searchNothing.isVisible = visible
                    binding.errorLayout.isVisible = false
                },
                showError = {
                    binding.searchRecycler.visibility = View.INVISIBLE
                    binding.searchNothing.isVisible = false
                    binding.errorLayout.isVisible = true
                }
            )
        }
    }

    override fun observeViewModel() {
        statusList()
    }

    private fun hideKeyboard(){
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.etQuery.windowToken, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackPressedCallback.remove()
    }
    private var onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }
}
```

검색 버튼을 따로 추가하지않고 `TextInputLayout` 에서 아이콘을 추가해 버튼처럼 동작하도록 하였습니다.

api를 중복으로 호출하는 상황을 막기위해 검색어가 같다면 api를 호출하지 않도록 만들었습니다.

검색어가없으면 검색되지 않도록 만들었습니다.

리사이클러뷰 상태에따라 UI가 변경되도록 만들었습니다.

리스트를 클릭하면 해당 데이터를 번들로만들어 지도 화면으로 돌아가 마커를 생성합니다.

### Util

- ValueExtension

```kotlin
fun CombinedLoadStates.decideOnState(
    adapter : PagingDataAdapter<DomainAddresses, AddressViewHolder>,
    showLoading: (Boolean) -> Unit,
    showEmptyState: (Boolean) -> Unit,
    showError: () -> Unit
) {
    showLoading(refresh is LoadState.Loading)

    showEmptyState(
        source.append is LoadState.NotLoading
                && source.append.endOfPaginationReached
                && adapter.itemCount == 0
    )

    val errorState = source.append as? LoadState.Error
        ?: source.prepend as? LoadState.Error
        ?: source.refresh as? LoadState.Error
        ?: append as? LoadState.Error
        ?: prepend as? LoadState.Error
        ?: refresh as? LoadState.Error

    errorState?.let { showError() }
}
```

데이터를 처리하기위한 코드들입니다. 여기서는 페이징 상태를 처리하기위해 따로 빼 두었습니다.

## App

App모듈에서는 힐트 설정만 해줍니다.

- App

```kotlin
@HiltAndroidApp
class App :Application()
```

힐트를 초기화 해줍니다.

힐트 모듈을 설정해줍니다.

- UseCaseModule

```kotlin
@InstallIn(ViewModelComponent::class)
@Module
abstract class UseCaseModule {

    @ViewModelScoped
    @Binds
    abstract fun bindsFindRootUseCase(impl: FindRootUseCaseImpl): FindRootUseCase

    @ViewModelScoped
    @Binds
    abstract fun bindsBicyclesLocationUseCase(impl: BicyclesLocationUseCaseImpl): BicyclesLocationUseCase

    @ViewModelScoped
    @Binds
    abstract fun bindsSearchAddressUseCase(impl: SearchAddressUseCaseImpl): SearchAddressUseCase

}
```

- DataSourceModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule{
    @Singleton
    @Provides
    fun providesFindRootDataSourceDataSource(source: FindRootDataSourceImpl): FindRootDataSource {
        return source
    }

    @Singleton
    @Provides
    fun providesSearchAddressDataSource(source: SearchAddressDataSourceImpl): SearchAddressDataSource {
        return source
    }
}
```

- RepositoryModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesBookRepository(repository: MapRepositoryImpl): MapRepository {
        return repository
    }

}
```

- NetworkModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com"
    private const val CLIENT_ID = "CLIENT_ID"
    private const val CLIENT_SECRET = "CLIENT_SECRET"

    @Provides
    @Singleton
    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(getHttpClient())
            .build()
    }

    @Provides
    @Singleton
    fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            readTimeout(10, TimeUnit.SECONDS)
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }.addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .also {
                        it.addHeader("X-NCP-APIGW-API-KEY-ID",CLIENT_ID)
                        it.addHeader("X-NCP-APIGW-API-KEY",CLIENT_SECRET)
                    }
                    .build()
            )
        }
            .build()
    }

    @Provides
    @Singleton
    fun provideDeliveryService(retrofit: Retrofit): NaverService {
        return retrofit.create(NaverService::class.java)
    }
}
```

실행 화면입니다.

![gif3.gif](/image/gif3.gif)