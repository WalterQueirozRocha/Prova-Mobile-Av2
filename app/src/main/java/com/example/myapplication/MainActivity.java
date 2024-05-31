package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Registrar Trilha
        Button btnRegistrarTrilha = findViewById(R.id.btn_registrar_trilha);
        btnRegistrarTrilha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarTrilhaActivity.class);
                startActivity(intent);
            }
        });

        //Gerenciar Trilha
        Button btnGerenciarTrilha = findViewById(R.id.btn_gerenciar_trilha);
        btnGerenciarTrilha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GerenciarTrilhaActivity.class);
                startActivity(intent);
            }
        });

        //Compartilhar Trilha
        Button btnCompartilharTrilha = findViewById(R.id.btn_compartilhar_trilha);
        btnCompartilharTrilha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CompartilharTrilhaActivity.class);
                startActivity(intent);
            }
        });

        //Configurações
        Button btnConfiguracao = findViewById(R.id.btn_configuracao);
        btnConfiguracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
                startActivity(intent);
            }
        });

        //Sobre
        Button btnCreditos = findViewById(R.id.btn_sobre);
        btnCreditos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SobreActivity.class);
                startActivity(intent);
            }
        });
    }
}
