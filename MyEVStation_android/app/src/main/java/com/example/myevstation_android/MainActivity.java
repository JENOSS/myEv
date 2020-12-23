package com.example.myevstation_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myevstation_android.dialog.CustomPopUpDialog;
import com.example.myevstation_android.dialog.FavoriteBottomSheetDialog;
import com.example.myevstation_android.dialog.FilterBottomSheetDialog;
import com.example.myevstation_android.model.Station;
import com.example.myevstation_android.remote.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Context context = this;
    private ApiService service;

    private static final String TAG = "MainActivity";
    private NaverMap mNaverMap;
    private Location currentLocation;
    private List<Marker> markerList = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //base location 강남역
        currentLocation = new Location("dummyprovider");
        currentLocation.setLatitude(37.497876);
        currentLocation.setLongitude(127.027591);

        service = ((GlobalApplication) getApplication()).getApiService();

        // 지도 객체 생성성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        // getMapAsync를 호출하여 비동기로 OnMapReady 콜백메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile_item:
                        break;

                    case R.id.filter_item:
                        FilterBottomSheetDialog filterBottomSheetDialog = new FilterBottomSheetDialog();
                        filterBottomSheetDialog.show(getSupportFragmentManager(), "filterBottomSheetDialog");
                        break;

                    case R.id.favorite_item:
                        FavoriteBottomSheetDialog favoriteBottomSheetDialog = new FavoriteBottomSheetDialog();
                        favoriteBottomSheetDialog.show(getSupportFragmentManager(), "favoriteBottomSheetDialog");
                        break;
                }
                return true;
            }
        });

        checkLocationPermission();
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG, "onMapReady");

        //NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;

        CameraUpdate cameraUpdate =
                CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15.5));
        mNaverMap.setMinZoom(6.5);

        mNaverMap.moveCamera(cameraUpdate);

        // 카메라가 이동 되면 호출되는 이벤트트
        mNaverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                markerList.clear();
                Location camLocation = new Location("dummyprovider");
                camLocation.setLatitude(naverMap.getCameraPosition().target.latitude);
                camLocation.setLongitude(naverMap.getCameraPosition().target.longitude);
                getStations(camLocation);
            }
        });
    }

    private void addMarker(List<Station> stations){
        if(stations != null && !stations.isEmpty()){
            for(Station station : stations){
                Marker marker = new Marker();
                marker.setPosition(new LatLng(Double.parseDouble(station.getLat()),Double.parseDouble(station.getLng())));
                markerList.add(marker);

                marker.setMap(mNaverMap);
            }
        }
    }

    private void getStations(Location location){
        service.getStation(location.getLatitude(),location.getLongitude()).enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().size() > 0){
                        addMarker(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {

            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) // 권환 o
                == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else { // 권한 x
            CustomPopUpDialog dialog = new CustomPopUpDialog(context, new CustomPopUpDialog.ButtonListener() {
                @Override
                public void clickButton(AlertDialog alertDialog) {
                    alertDialog.dismiss();
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_CODE);
                }
            });
            dialog.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // request code와 권환획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //허용
                //mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
                getMyLocation();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) { //거부
                checkLocationPermission();
            } else { // 다시 묻지 않기
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(i, PERMISSION_REQUEST_CODE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PERMISSION_REQUEST_CODE){
            checkLocationPermission();
        }
    }

    private void getMyLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                List<Location> locationList = locationResult.getLocations();

                if (locationList.size() > 0) {
                    //여기서 호출
                    currentLocation = locationList.get(0);

                    CameraUpdate cameraUpdate =
                            CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15.5));

                    mNaverMap.moveCamera(cameraUpdate);
                }
            }
        };

        if (locationManager.isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "내 위치 가져오기 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }else{
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }else{
            Toast.makeText(context, "내 위치 가져오기 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }



    }


}