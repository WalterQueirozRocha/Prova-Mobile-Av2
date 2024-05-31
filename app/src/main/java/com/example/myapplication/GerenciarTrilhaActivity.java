package com.example.myapplication;

import static com.example.myapplication.ConfiguracaoActivity.PREFS_NAME;

import androidx.fragment.app.FragmentActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class GerenciarTrilhaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TrilhasDB trilhasDB;
    private SharedPreferences sharedPreferences;
    private ListView listViewSummaries;
    private List<WayPoint> summaries;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_trilha);
        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trilhasDB = new TrilhasDB(this);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        listViewSummaries = findViewById(R.id.list_view_summaries);
        summaries = trilhasDB.getAllSummaries();
        List<String> summariesText = new ArrayList<>();
        for (WayPoint summary : summaries) {
            String avgSpeedFormatted = String.format("%.2f", summary.getAvgSpeed());
            String totalDistanceFormatted = String.format("%.2f", summary.getTotalDistance());
            summariesText.add("ID: " + summary.getId() + "\n" +
                    "Data: " + summary.getStartDate() + "\n" +
                    "Velocidade Média: " + avgSpeedFormatted + " km/h" + "\n" +
                    "Distância: " + totalDistanceFormatted + " m" + "\n" +
                    "Duração: " + summary.getDuration() / 1000 + " s");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, summariesText);
        listViewSummaries.setAdapter(adapter);

        listViewSummaries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int trilhaId = summaries.get(position).getId();
                trilhasDB.apagarTrilha(trilhaId);
                Toast.makeText(GerenciarTrilhaActivity.this, "Trilha apagada: ID=" + trilhaId, Toast.LENGTH_SHORT).show();
                summaries.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
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

        List<WayPoint> wayPoints = trilhasDB.getAllWayPoints();

        PolylineOptions polylineOptions = new PolylineOptions();
        for (WayPoint wayPoint : wayPoints) {
            LatLng latLng = new LatLng(wayPoint.getLatitude(), wayPoint.getLongitude());
            polylineOptions.add(latLng);
        }
        mMap.addPolyline(polylineOptions);
        if (!wayPoints.isEmpty()) {
            WayPoint firstWayPoint = wayPoints.get(0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstWayPoint.getLatitude(), firstWayPoint.getLongitude()), 19));
        }
    }
}
