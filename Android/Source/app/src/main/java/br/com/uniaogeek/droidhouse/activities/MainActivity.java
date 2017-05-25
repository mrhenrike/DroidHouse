package br.com.uniaogeek.droidhouse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import br.com.uniaogeek.droidhouse.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaração de variáveis utilizadas para o layout
    private NestedScrollView nestedScrollView;
    private AppCompatButton btnHomeLogin;
    private AppCompatButton btnHomeSobre;
    private AppCompatButton btnHomeSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Método para retirar a Barra Superior desta Activity
        getSupportActionBar().hide();

        //Inicializando métodos
        initViews();
        initListeners();
    }

    @Override
    protected void onResume() {
        if (getIntent().getBooleanExtra(getString(R.string.msnSair), false)) {
            finish();
        }
        super.onResume();
    }

    /**
     * Método para inicializar todas as views
     */
    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        btnHomeLogin = (AppCompatButton) findViewById(R.id.btnHomeLogin);
        btnHomeSobre = (AppCompatButton) findViewById(R.id.btnHomeSobre);
        btnHomeSair = (AppCompatButton) findViewById(R.id.btnHomeSair);

    }

    /**
     * Método para inicializar todos os listeners para ação dos botões criados
     */
    private void initListeners() {
        btnHomeLogin.setOnClickListener(this);
        btnHomeSobre.setOnClickListener(this);
        btnHomeSair.setOnClickListener(this);

    }

    /**
     * Método que sobrescreve o onClick, com ações dos botões da Activity Main
     *
     * @param v recebe a view que irá tratar das ações dos botões
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHomeLogin:
                //Abre a tela de login
                Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.btnHomeSobre:
                //Abre a tela contendo algumas informações gerais do sistema
                Intent intentSobre = new Intent(getApplicationContext(), SobreActivity.class);
                startActivity(intentSobre);
                break;
            case R.id.btnHomeSair:
                //Fecha o aplicativo
                finish();
                break;

        }
    }

}
