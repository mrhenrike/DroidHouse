package br.com.uniaogeek.droidhouse.activities;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.uniaogeek.droidhouse.R;

public class SobreActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaração de variáveis utilizadas para o layout
    private NestedScrollView nestedScrollViewSobre;
    private AppCompatButton btnSobreVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        //Método para retirar a Barra Superior desta Activity
        getSupportActionBar().hide();

//        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
//        AppCompatTextView dataAtual = (AppCompatTextView) findViewById(R.id.lblData);
//        dataAtual.setText(date);


        //Inicializando métodos
        initViews();
        initListeners();
    }

    private void initViews() {

        nestedScrollViewSobre = (NestedScrollView) findViewById(R.id.nestedScrollView);
        btnSobreVoltar = (AppCompatButton) findViewById(R.id.btnSobreVoltar);

    }

    private void initListeners() {
        btnSobreVoltar.setOnClickListener(this);

    }

    /**
     * Método utilizado para voltar para tela anterior
     * */
    @Override
    public void onClick(View v) {
        onBackPressed();
    }


}

