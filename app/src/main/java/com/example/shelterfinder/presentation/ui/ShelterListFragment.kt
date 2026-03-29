package com.example.shelterfinder.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shelterfinder.R
import com.example.shelterfinder.ShelterApplication
import com.example.shelterfinder.presentation.adapter.ShelterListAdapter
import com.example.shelterfinder.presentation.viewmodel.ShelterViewModel
import com.example.shelterfinder.presentation.viewmodel.ShelterViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.location.Location
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.shelterfinder.data.local.entity.ShelterEntity
import com.example.shelterfinder.data.remote.dto.ShelterDto
import com.example.shelterfinder.data.remote.service.KakaoApiService
import com.example.shelterfinder.databinding.FragmentShelterListBinding
import com.example.shelterfinder.presentation.viewmodel.ShelterListViewModel
import com.example.shelterfinder.presentation.viewmodel.ShelterListViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.shelterfinder.BuildConfig

class ShelterListFragment : Fragment() {
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShelterListAdapter

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            requestLocation()
        } else {
            Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var webView: WebView
    private lateinit var searchButton: ImageButton
    private lateinit var btnMyLocation: ImageButton
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var detailContainer: LinearLayout
    private lateinit var txtDetailName: TextView
    private lateinit var txtDetailAddress: TextView
    private lateinit var txtDetailInfo: TextView
    private lateinit var btnHideDetail: Button

    private lateinit var viewModel2: ShelterListViewModel

    private var pageLoaded: Boolean = false
    private var pendingLat: Double? = null
    private var pendingLng: Double? = null
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    private var _binding: FragmentShelterListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShelterViewModel by viewModels {
        val app = requireActivity().application as ShelterApplication
        ShelterViewModelFactory(app.shelterRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShelterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("DEBUG", "ShelterListFragment onViewCreated 호출됨")

        searchEditText = view.findViewById(R.id.searchEditText)
        webView = view.findViewById(R.id.mapWebView)
        recyclerView = view.findViewById(R.id.recyclerShelterList)
        searchButton = view.findViewById(R.id.searchButton)
        btnMyLocation = view.findViewById(R.id.btnMyLocation)

        adapter = ShelterListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        detailContainer = view.findViewById(R.id.detailInfoContainer)
        txtDetailName = view.findViewById(R.id.txtDetailName)
        txtDetailAddress = view.findViewById(R.id.txtDetailAddress)
        txtDetailInfo = view.findViewById(R.id.txtDetailInfo)
        btnHideDetail = view.findViewById(R.id.btnHideDetail)

        // 아이템 클릭 리스너 설정(수용시설 상세 정보 표시)
        adapter.setOnItemClickListener { clickedShelter ->
            val address = buildAddress(clickedShelter)

            txtDetailName.text = clickedShelter.name
            txtDetailAddress.text = address
            txtDetailInfo.text = clickedShelter.detailInfo ?: "세부정보 없음"

            detailContainer.visibility = View.VISIBLE
            detailContainer.translationY = detailContainer.height.toFloat()
            detailContainer.animate().translationY(0f)

            bindDetailInfo(clickedShelter)

            val lat = clickedShelter.ndmsLat
            val lng = clickedShelter.ndmsLng
            val name = clickedShelter.name.replace("'", "\\'")

            // 현재 좌표 기반으로 카카오맵 api 웹뷰 형태로
            if (lat != null && lng != null) {
                val js = "focusOn($lat, $lng, '$name');"
                webView.evaluateJavascript(js, null)
            }
            
            // 수용시설명에 전경을 붙여서 카카오 이미지 검색 API에 대표이미지 검색
            val refinedQuery = "${clickedShelter.name} 전경"
            viewModel2.loadShelterImage(refinedQuery)
        }

        // 숨기기 버튼 동작
        btnHideDetail.setOnClickListener {
            detailContainer.animate()
                .translationY(detailContainer.height.toFloat())
                .withEndAction {
                    detailContainer.visibility = View.GONE
                }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        WebView.setWebContentsDebuggingEnabled(true)
        setupWebView()
        setupPermissionLauncher()

        checkLocationPermissionAndRequest()

        // 초기 데이터 로드
        viewModel.loadAll()

        // Room에서 Flow로 가져오기
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shelters.collectLatest { shelters ->
                shelters.forEachIndexed { index, shelter ->
                    Log.d("API_RESPONSE", "ShelterDto: ${shelter.toString()}")
                }
                val lat = currentLat
                val lng = currentLng
                if (lat != null && lng != null) {
                    val updated = shelters.mapNotNull { shelter ->
                        val sLat = shelter.ndmsLat
                        val sLng = shelter.ndmsLng
                        if (sLat != null && sLng != null) {
                            val results = FloatArray(1)
                            Location.distanceBetween(lat, lng, sLat, sLng, results)
                            shelter.distanceFromCurrent = results[0] / 1000.0
                            shelter
                        } else null
                    }.sortedBy { it.distanceFromCurrent }
                    adapter.submitList(updated)
                } else {
                    adapter.submitList(shelters)
                }
            }
        }

        // 검색 버튼
        searchButton.setOnClickListener {
            val keyword = searchEditText.text.toString()
            performSearch(keyword)
        }

        // 검색창
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = searchEditText.text.toString()
                performSearch(keyword)
                true
            } else {
                false
            }
        }

        // 지도 우측 하단 GPS 버튼(현재 위치로)
        btnMyLocation.setOnClickListener {
            checkLocationPermissionAndRequest()
        }

        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val kakaoService = retrofit.create(KakaoApiService::class.java)

        // ViewModel 생성
        val factory = ShelterListViewModelFactory(kakaoService)
        viewModel2 = ViewModelProvider(this, factory)[ShelterListViewModel::class.java]

        // LiveData 관찰 및 이미지 로딩
        viewModel2.imageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_delete)
                .into(binding.imgShelterPhoto)
        }
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                pageLoaded = true
                pendingLat?.let { lat ->
                    pendingLng?.let { lng ->
                        webView.evaluateJavascript("initMap($lat, $lng);", null)
                    }
                }
            }
        }

        //webView.loadUrl("file:///android_asset/map.html")
        // assets의 map.html을 읽어와서 키를 주입합니다.
        try {
            val htmlContent = requireContext().assets.open("map.html").bufferedReader().use { it.readText() }

            // map.html 파일 내의 실제 키(f3bf...) 혹은 "YOUR_KEY"라는 텍스트를 찾아 가려진 키로 바꿉니다.
            val finalHtml = htmlContent.replace("YOUR_KEY", BuildConfig.KAKAO_MAP_KEY)

            // 키가 치환된 HTML 문자열을 WebView에 로드합니다.
            webView.loadDataWithBaseURL("https://dapi.kakao.com", finalHtml, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            Log.e("WEBVIEW", "HTML 로드 및 키 주입 실패", e)
        }
    }

    private fun setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocation()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissionAndRequest() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val lat = it.latitude
                val lng = it.longitude
                currentLat = lat
                currentLng = lng

                if (pageLoaded) {
                    webView.evaluateJavascript("initMap($lat, $lng);", null)
                } else {
                    pendingLat = lat
                    pendingLng = lng
                }

                viewModel.loadNearbyShelters(lat, lng)

                lifecycleScope.launch {
                    viewModel.shelters.collectLatest { shelters ->
                        for (shelter in shelters) {
                            shelter.ndmsLat?.let { sLat ->
                                shelter.ndmsLng?.let { sLng ->
                                    val js = "addMarker($sLat, $sLng, '${shelter.name.replace("'", "\\'")}');"
                                    webView.evaluateJavascript(js, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun performSearch(keyword: String) {
        lifecycleScope.launch {
            val results = viewModel.searchShelters(keyword)

            // 거리 계산은 하되, 정렬은 하지 않음
            val lat = currentLat
            val lng = currentLng
            val updated = results.map { shelter ->
                if (lat != null && lng != null && shelter.ndmsLat != null && shelter.ndmsLng != null) {
                    val results = FloatArray(1)
                    Location.distanceBetween(lat, lng, shelter.ndmsLat, shelter.ndmsLng, results)
                    shelter.distanceFromCurrent = results[0] / 1000.0
                }
                shelter
            }

            adapter.submitList(updated)

            // 마커 초기화
            webView.evaluateJavascript("clearMarkers();", null)

            updated.forEachIndexed { index, shelter ->
                val sLat = shelter.ndmsLat ?: return@forEachIndexed
                val sLng = shelter.ndmsLng ?: return@forEachIndexed
                val title = shelter.name.replace("'", "\\'")
                val js = "addMarker($sLat, $sLng, '$title');"
                webView.evaluateJavascript(js, null)

                if (index == 0) {
                    val moveJs = "moveTo($sLat, $sLng);"
                    webView.evaluateJavascript(moveJs, null)
                }
            }
        }
    }

    fun buildAddress(shelter: ShelterEntity): String {
        // 도로명 주소 구성
        val roadParts = listOfNotNull(
            shelter.ctprvnName,
            shelter.sggName,
            shelter.sggRoadName
        )

        val buildingMain = shelter.buildingMainNo?.toIntOrNull()
        val buildingSub = shelter.buildingSubNo?.toIntOrNull()

        val buildingNumber = when {
            buildingMain == null -> null
            buildingSub != null && buildingSub > 0 -> "$buildingMain-$buildingSub"
            else -> "$buildingMain"
        }

        val roadAddress = if (roadParts.isNotEmpty() && buildingNumber != null) {
            (roadParts + buildingNumber).joinToString(" ")
        } else null

        // 지번 주소 구성
        val lotMain = shelter.lotNoMain?.toIntOrNull()
        val lotSub = shelter.lotNoSub?.toIntOrNull()

        val lotNumber = when {
            lotMain == null -> null
            lotSub != null && lotSub > 0 -> "$lotMain-$lotSub"
            else -> "$lotMain"
        }

        val jibunParts = listOfNotNull(
            shelter.ctprvnName,
            shelter.sggName,
            shelter.emdName
        )

        val jibunAddress = if (jibunParts.isNotEmpty() && lotNumber != null) {
            (jibunParts + lotNumber).joinToString(" ")
        } else null

        // 최종 주소 결정
        return roadAddress ?: jibunAddress ?: "주소 정보 없음"
    }

    private fun bindDetailInfo(shelter: ShelterEntity) {
        txtDetailName.text = shelter.name
        txtDetailAddress.text = buildAddress(shelter)

        // 텍스트 정보 구성
        val info = buildString {
            appendLine("세부정보: ${shelter.detailInfo ?: "없음"}")
            appendLine("수용 가능 인원: ${shelter.capacity?.let { "${it}명" } ?: "정보 없음"}")
            appendLine("면적: ${shelter.areaSize?.let { "$it㎡" } ?: "정보 없음"}")
            appendLine("지하 여부: ${toOX(shelter.isUnderground)}")
            appendLine("지진 대피소: ${toAvailable(shelter.isEarthquakeShelter)}")
            appendLine("화장실: ${toAvailable(shelter.hasToilet)}")
            appendLine("급식 시설: ${toAvailable(shelter.hasMeal)}")
            appendLine("급수 시설: ${toAvailable(shelter.hasWater)}")
            appendLine("전화번호: ${shelter.phone ?: "없음"}")
            appendLine("관리자 연락처: ${shelter.managerPhone ?: "없음"}")
        }

        txtDetailInfo.text = info
    }

    private fun toOX(value: Boolean?): String = if (value == true) "O" else "X"
    private fun toAvailable(value: Boolean?): String = if (value == true) "있음" else "없음"
}