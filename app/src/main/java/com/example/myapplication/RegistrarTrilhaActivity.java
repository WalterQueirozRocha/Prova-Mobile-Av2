package com.example.myapplication;

import static com.example.myapplication.ConfiguracaoActivity.PREFS_NAME;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RegistrarTrilhaActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mLastLocation = null;
    private TrilhasDB trilhasDB;
    private SharedPreferences sharedPreferences;
    private TextView mTextViewTimer;
    private TextView mTextViewDistance;
    private TextView mTextViewSpeed;
    private long mStartTime = 0L;
    private Handler mHandler = new Handler();
    private long mElapsedTime = 0L;
    private double totalDistance = 0;
    private double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_trilha);
        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());


        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        mTextViewTimer = findViewById(R.id.text_view_timer);
        mTextViewDistance = findViewById(R.id.text_view_distance);
        mTextViewSpeed = findViewById(R.id.text_view_speed);

        startTimer();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        trilhasDB = new TrilhasDB(this);

        mLocationRequest = new LocationRequest.Builder(1000).build();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    speed = location.getSpeed();
                    double distance = 0;
                    if (mLastLocation != null){
                        distance = mLastLocation.distanceTo(location);
                        totalDistance += distance;
                    }
                    mLastLocation = location;


                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Você está aqui"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19));

                    trilhasDB.addWayPoint(new WayPoint(location.getLatitude(), location.getLongitude()));
                }
            }
        };
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long currentTime = System.currentTimeMillis();
            mElapsedTime = currentTime - mStartTime;
            updateText(mElapsedTime, totalDistance, speed);
            mHandler.postDelayed(this, 1000); // Atualiza a cada segundo
        }
    };

    private void startTimer() {
        mStartTime = System.currentTimeMillis();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void updateText(long elapsedTime, double totalDistance, double speed) {
        int seconds = (int) (elapsedTime / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;
        mTextViewTimer.setText(String.format("Tempo: %02d:%02d:%02d",hours, minutes, seconds));
        mTextViewDistance.setText(String.format("Distância: %.2f Km", totalDistance/1000));
        mTextViewSpeed.setText(String.format("Velocidade: %.2f km/h", speed));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String tipoMapa;
        tipoMapa = sharedPreferences.getString("tipo_mapa","Vetorial");
        if (tipoMapa == "Vetorial"){
            mMap.setMapType(1);
        } else {
            mMap.setMapType(2);
        }

        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setCompassEnabled(true);
        mapUI.setAllGesturesEnabled(true);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Ative o GPS para usar esta função", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        mHandler.removeCallbacks(mUpdateTimeTask);
        double avgSpeed = totalDistance / mElapsedTime;
        String startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(mStartTime));

        trilhasDB.addTrilhaSummary(startDate, avgSpeed, totalDistance, mElapsedTime);
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }
}


