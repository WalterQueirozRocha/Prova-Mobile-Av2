package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.UiSettings;

public class ConfiguracaoActivity extends AppCompatActivity {

    static final String PREFS_NAME = "Configuracoes";
    private static final String UNIDADE_VELOCIDADE = "unidade_velocidade";
    private static final String FORMATO_COORDENADAS = "formato_coordenadas";
    private static final String ORIENTACAO_MAPA = "orientacao_mapa";
    private static final String TIPO_MAPA = "tipo_mapa";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        setupUnidadeVelocidade();
        setupFormatoCoordenadas();
        setupOrientacaoMapa();
        setupTipoMapa();
        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());
    }

    private void setupUnidadeVelocidade() {
        RadioGroup radioGroupUnidadeVelocidade = findViewById(R.id.radioGroupUnidadeVelocidade);
        String unidadeVelocidade = sharedPreferences.getString(UNIDADE_VELOCIDADE, "km/h");

        switch (unidadeVelocidade) {
            case "km/h":
                radioGroupUnidadeVelocidade.check(R.id.radioKmh);
                break;
            case "m/s":
                radioGroupUnidadeVelocidade.check(R.id.radioMs);
                break;
        }

        radioGroupUnidadeVelocidade.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (checkedId) {
                case R.id.radioKmh:
                    editor.putString(UNIDADE_VELOCIDADE, "km/h");
                    break;
                case R.id.radioMs:
                    editor.putString(UNIDADE_VELOCIDADE, "m/s");
                    break;
            }
            editor.apply();
            Toast.makeText(ConfiguracaoActivity.this, "Unidade de Velocidade alterada", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFormatoCoordenadas() {
        RadioGroup radioGroupFormatoCoordenadas = findViewById(R.id.radioGroupFormatoCoordenadas);
        String formatoCoordenadas = sharedPreferences.getString(FORMATO_COORDENADAS, "Graus");

        switch (formatoCoordenadas) {
            case "Graus":
                radioGroupFormatoCoordenadas.check(R.id.radioGraus);
                break;
            case "Graus-Minutos":
                radioGroupFormatoCoordenadas.check(R.id.radioGrausMinutos);
                break;
            case "Graus-Minutos-Segundos":
                radioGroupFormatoCoordenadas.check(R.id.radioGrausMinutosSegundos);
                break;
        }

        radioGroupFormatoCoordenadas.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (checkedId) {
                case R.id.radioGraus:
                    editor.putString(FORMATO_COORDENADAS, "Graus");
                    break;
                case R.id.radioGrausMinutos:
                    editor.putString(FORMATO_COORDENADAS, "Graus-Minutos");
                    break;
                case R.id.radioGrausMinutosSegundos:
                    editor.putString(FORMATO_COORDENADAS, "Graus-Minutos-Segundos");
                    break;
            }
            editor.apply();
            Toast.makeText(ConfiguracaoActivity.this, "Formato de Coordenadas alterado", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupOrientacaoMapa() {
        RadioGroup radioGroupOrientacaoMapa = findViewById(R.id.radioGroupOrientacaoMapa);
        String orientacaoMapa = sharedPreferences.getString(ORIENTACAO_MAPA, "Nenhuma");

        switch (orientacaoMapa) {
            case "Nenhuma":
                radioGroupOrientacaoMapa.check(R.id.radioNenhuma);
                break;
            case "North Up":
                radioGroupOrientacaoMapa.check(R.id.radioNorthUp);
                break;
            case "Course Up":
                radioGroupOrientacaoMapa.check(R.id.radioCourseUp);
                break;
        }

        radioGroupOrientacaoMapa.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (checkedId) {
                case R.id.radioNenhuma:
                    editor.putString(ORIENTACAO_MAPA, "Nenhuma");
                    break;
                case R.id.radioNorthUp:
                    editor.putString(ORIENTACAO_MAPA, "North Up");
                    break;
                case R.id.radioCourseUp:
                    editor.putString(ORIENTACAO_MAPA, "Course Up");
                    break;
            }
            editor.apply();
            Toast.makeText(ConfiguracaoActivity.this, "Orientação do Mapa alterada", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupTipoMapa() {
        RadioGroup radioGroupTipoMapa = findViewById(R.id.radioGroupTipoMapa);
        String tipoMapa = sharedPreferences.getString(TIPO_MAPA, "Vetorial");

        switch (tipoMapa) {
            case "Vetorial":
                radioGroupTipoMapa.check(R.id.radioVetorial);
                break;
            case "Satélite":
                radioGroupTipoMapa.check(R.id.radioSatelite);
                break;
        }

        radioGroupTipoMapa.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (checkedId) {
                case R.id.radioVetorial:
                    editor.putString(TIPO_MAPA, "Vetorial");
                    break;
                case R.id.radioSatelite:
                    editor.putString(TIPO_MAPA, "Satélite");
                    break;
            }
            editor.apply();
            Toast.makeText(ConfiguracaoActivity.this, "Tipo de Mapa alterado", Toast.LENGTH_SHORT).show();
        });
    }
}
