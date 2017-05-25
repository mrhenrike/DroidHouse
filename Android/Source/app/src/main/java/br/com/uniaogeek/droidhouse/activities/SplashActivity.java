package br.com.uniaogeek.droidhouse.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.uniaogeek.droidhouse.R;

public class SplashActivity extends AppCompatActivity {
    private static final int MAXIMO = 100;
    private int progresso = 0;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    //private TextView titulo_app;
    //private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //font = Typeface.createFromAsset(getAssets(), "font/2BeesChampagne.ttf");
        //titulo_app = (TextView) findViewById(R.id.titlulo_app);
        //titulo_app.setTypeface(font);

        //Método para retirar a Barra Superior desta Activity
        getSupportActionBar().hide();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /* Inícia Thread */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progresso < MAXIMO) {
                    progresso = onSleep();

                    /* Atualiza barra de progresso */
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progresso);
                        }
                    });
                }
                //Intent mainActivity = new Intent("br.com.andruinobluetooth.DEVICELISTACTIVITY");
                Intent mainActivity = new Intent("br.com.uniaogeek.droidhouse.activities.MainActivity");
                startActivity(mainActivity);
            }
        }).start();
    }

    /**
     * onSleep
     *
     * @return
     */
    private int onSleep() {
        progresso++;
        try {
            Thread.sleep(25);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progresso;
    }

    /**
     * onPause
     */
    protected void onPause() {
        super.onPause();
        finish();
    }

}
