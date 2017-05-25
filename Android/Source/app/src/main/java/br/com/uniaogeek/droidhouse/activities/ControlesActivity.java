package br.com.uniaogeek.droidhouse.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import br.com.uniaogeek.droidhouse.R;

public class ControlesActivity extends AppCompatActivity implements View.OnClickListener {

    /* SPP UUID service - Isso deve funcionar para a maioria dos dispositivos. */
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address; // String para endereço MAC

    /* Debugging para LOGCAT */
    private static String TAG = "LOG DeviceListActivity"; // String para verificar logs

    /* Utilizado para identificar mensagem manipulador */
    final int handlerState = 0;
    final int RECIEVE_MESSAGE = 1;
    private Handler bluetoothIn;
    private Handler h;

    /* Utilizado para criar e verificar comunicação com o Arduino via Bluetooth*/
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    /* Utilizado para tratar dados recebidos do Arduino pelo método getDataArduino() */
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;

    private Toolbar mToolbar; //Utilizado para criar uma Barra superior com título

    /* Configura o vibrador do aparelho */
    private Vibrator vibrator;
    private int vibClick = 50;

    /* Botões criados para interface de controle */
    private ToggleButton btnLiga;
    private Switch simpleSwitch1, simpleSwitch2, simpleSwitch3;
    private Button checkLamp; //Checa estados dos botões

    private TextView temperatureText; //Recebe os dados de temperatura a cada 1 minuto, do método getDataArduino()

    private boolean statusLampada = false; //Checa estados das lâmpadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controles);

        /* Inicializa todas as Views */
        initViews();

        /* Método para receber dados do Arduino */
        getDataArduino();

        /* Barra de Título da Activity */
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(getString(R.string.titleDH));
        mToolbar.setSubtitle(getString(R.string.lblStatus));
        setSupportActionBar(mToolbar);

        /* Vibrador para o dispositivo */
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        /* Manipulador da conexão com o Arduino, via Bluetooth */
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj; // msg.arg1 = bytes de conexão com a Thread principal
                    recDataString.append(readMessage); // tenta manter a conexão
                    int endOfLineIndex = recDataString.indexOf("~"); // determinar o final da linha

                    if (endOfLineIndex > 0) { //Verifica os dados que vieram antes de "~"
                        // extrai a String recebido
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        // recebe o tamanho dos dados recebidos
                        int dataLength = dataInPrint.length();

                        // se ele começa com # sabemos que \u00e9 o que estamos procurando
                        if (recDataString.charAt(0) == '#') {
                        }

                        //limpa todos os dados da String
                        recDataString.delete(0, recDataString.length());
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        /* Recebe o Adapter do Bluetooth */
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        /* Acessa o método que verifica a conexão com o Bluetooth */
        checkBTState();

        /* Inicializa todos os Listeners dos componentes da Interface */
        initListeners();
    }

    /**
     * Este método aguarda o Clique do Botão, quando clicado, realiza a verificação com o Banco
     *
     * @param v recebe a view atual
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLigaControles:
                if (statusLampada == false) {
                    //vibrator.vibrate(vibClick);
                    mConnectedThread.write("led_on_all" + "+" + "1" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = true; //Altera status da lâmpada para ligado!

                    //Toast.makeText(ControlesActivity.this, getString(R.string.msnLigado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho

                    simpleSwitch1.setChecked(true);
                    simpleSwitch2.setChecked(true);
                    simpleSwitch3.setChecked(true);

                } else {
                    //vibrator.vibrate(vibClick);
                    mConnectedThread.write("led_off_all" + "+" + "0" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = false; //Altera status da lâmpada para desligado!

                    //Toast.makeText(ControlesActivity.this, getString(R.string.msnDesligado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho

                    simpleSwitch1.setChecked(false);
                    simpleSwitch2.setChecked(false);
                    simpleSwitch3.setChecked(false);
                }
                break;
        }
    }

    /**
     * Método para criar uma via de comunicação com o dispositivo através de bluetooth,
     * utilizando o método BluetoothSocket
     *
     * @param device
     * @return
     * @throws IOException
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        // Cria conexão de saída segura com dispositivo BT usando UUID

        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().
                        getMethod("createInsecureRfcommSocketToServiceRecord",
                                new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
            } catch (Exception e) {
                Log.e(TAG, getString(R.string.errorRFComm), e);
            }
        }

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    /**
     * onResume
     *
     * Método criado automaticamente
     */
    @Override
    public void onResume() {
        super.onResume();

        //Recebe o MAC address da classe ListaDispositivosActivity via intent
        Intent intent = getIntent();

        // //Recebe o MAC address da classe ListaDispositivosActivity via EXTRA
        address = intent.getStringExtra(ListaDispositivosActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            /* Exibe erro de conexão caso não tenha identificado o dispositivo com BT ativo */
            Toast.makeText(getBaseContext(), getString(R.string.errorSocketBT), Toast.LENGTH_LONG).show();

        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                /* Exibe erro de conexão caso não tenha identificado o dispositivo com BT ativo */
                Toast.makeText(getBaseContext(), getString(R.string.errorSocketBT), Toast.LENGTH_LONG).show();

            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }


    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }


    /**
     * checkBTState - Checks that the Android device Bluetooth is available and prompts to be turned on if off
     */
    private void checkBTState() {
        if (btAdapter == null) { // Verifica se o aparelho possui bluetooth.
            Toast.makeText(getBaseContext(), getString(R.string.errorBTState), Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    /**
     *
     * Método utilizado para inicializar todas as Views de controles da Interface
     *
     * **/
    private void initViews() {

        temperatureText = (TextView) findViewById(R.id.tempCurrent);

        btnLiga = (ToggleButton) findViewById(R.id.btnLigaControles);

        simpleSwitch1 = (Switch) findViewById(R.id.simpleSwitch1);
        simpleSwitch2 = (Switch) findViewById(R.id.simpleSwitch2);
        simpleSwitch3 = (Switch) findViewById(R.id.simpleSwitch3);

        checkLamp = (Button) findViewById(R.id.checkLamp);

    }

    /**
     * O Método initListerners, é utilizado para inicializar todos os Listeners (escuta de ações)
     * que vierem da interface, tais como o clique em algum botão, tela, ou movimentação do scroll
     *
     * */
    private void initListeners() {

        btnLiga.setOnClickListener(this); //Aguarda o clique no Botão Ligar/Desligar todas as Lâmpadas



        /**
         *  Checa estados das Lâmpadas/Leds, se estão ligadas ou Desligadas
         *
         **/
        checkLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1, statusSwitch2, statusSwitch3;
                if (simpleSwitch1.isChecked()) {
                    statusSwitch1 = simpleSwitch1.getTextOn().toString();

                } else {
                    statusSwitch1 = simpleSwitch1.getTextOff().toString();

                }
                if (simpleSwitch2.isChecked()) {
                    statusSwitch2 = simpleSwitch2.getTextOn().toString();
                } else {
                    statusSwitch2 = simpleSwitch2.getTextOff().toString();
                }
                if (simpleSwitch3.isChecked()) {
                    statusSwitch3 = simpleSwitch3.getTextOn().toString();
                } else {
                    statusSwitch3 = simpleSwitch3.getTextOff().toString();
                }
                /* Exibe em tela, os estados de cada botão das lâmpadas/leds */
                Toast.makeText(getApplicationContext(),
                        getString(R.string.lblLamp1)   + ": " + statusSwitch1 + "\n"
                        + getString(R.string.lblLamp2) + ": " + statusSwitch2 + "\n"
                        + getString(R.string.lblLamp3) + ": " + statusSwitch3 + "\n",
                         Toast.LENGTH_LONG).show();
            } // Fim do método onClick()
        });

        /**
         *  Métodos para controle dos botões Switch, utilizados aqui
         *  para simular as lâmpadas/leds do sistema
         *
         **/

        // INICIO Lampada/Led 1
        simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //simpleSwitch1.setText("Lâmpada Ligada"); //Altera o texto ao lado do botão switch

                    vibrator.vibrate(vibClick); //Faz o dispositivo vibrar

                    mConnectedThread.write("led_on_1" + "+" + "1" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = true; //Altera status da lâmpada para ligado!

                    Toast.makeText(ControlesActivity.this, getString(R.string.msnLigado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho

                } else {
                    //simpleSwitch1.setText("Lâmpada Desligada"); //Altera o texto ao lado do botão switch

                    vibrator.vibrate(vibClick); //Faz o dispositivo vibrar

                    mConnectedThread.write("led_off_1" + "+" + "0" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = false; //Altera status da lâmpada para desligado!

                    Toast.makeText(ControlesActivity.this,  getString(R.string.msnDesligado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho
                }
            }
        }); //FIM Lampada/Led 1

        // INICIO Lampada/Led 2
        simpleSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //simpleSwitch2.setText("Lâmpada Ligada"); //Altera o texto ao lado do botão switch

                    vibrator.vibrate(vibClick); //Faz o dispositivo vibrar

                    mConnectedThread.write("led_on_2" + "+" + "1" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = true; //Altera status da lâmpada para ligado!

                    Toast.makeText(ControlesActivity.this, getString(R.string.msnLigado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho

                } else {
                    //simpleSwitch2.setText("Lâmpada Desligada"); //Altera o texto ao lado do botão switch

                    vibrator.vibrate(vibClick); //Faz o dispositivo vibrar

                    mConnectedThread.write("led_off_2" + "+" + "0" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = false; //Altera status da lâmpada para desligado!

                    Toast.makeText(ControlesActivity.this,  getString(R.string.msnDesligado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho
                }
            }
        }); //FIM Lampada/Led 2

        // INICIO Lampada/Led 3
        simpleSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //simpleSwitch3.setText("Lâmpada Ligada"); //Altera o texto ao lado do botão switch

                    vibrator.vibrate(vibClick); //Faz o dispositivo vibrar

                    mConnectedThread.write("led_on_3" + "+" + "1" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = true; //Altera status da lâmpada para ligado!

                    Toast.makeText(ControlesActivity.this, getString(R.string.msnLigado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho

                } else {
                    //simpleSwitch3.setText("Lâmpada Desligada"); //Altera o texto ao lado do botão switch

                    vibrator.vibrate(vibClick); //Faz o dispositivo vibrar

                    mConnectedThread.write("led_off_3" + "+" + "0" + "\n"); //Envia os dados para chamar o método no Arduino

                    statusLampada = false; //Altera status da lâmpada para desligado!

                    Toast.makeText(ControlesActivity.this,  getString(R.string.msnDesligado), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem do status na tela do aparelho
                }
            }
        }); //FIM Lampada/Led 3
    } // Fim do método initListeners()

    /**
     * O Método getDataArduino é utilizado para tratar as informações recebidas do Arduino
     * Utiliza manipulador de mensagens e estados (Handler)
     * Os dados recebidos são enviados para interface de Controle
     *
     * **/

    public void getDataArduino() {
//        temperatureText.clearComposingText();
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:

                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        recDataString.append(strIncom);
                        int endOfLineIndex = recDataString.indexOf("\n");
                        if (endOfLineIndex > 0) {

                            String sbprint = recDataString.substring(0, endOfLineIndex);
                            recDataString.delete(0, recDataString.length());
                            temperatureText.setText(sbprint + "ºC");
                        }
                        Log.d(TAG, "...String:" + recDataString.toString() + "Byte:" + msg.arg1 + "..."); //Verificação de Logs
                        break;
                }
            }
        };
    } // Fim do método getDataArduino()

    /**
     * Classe criada para manipular e controlar a comunicação com o Bluetooth, utilizando Threads para
     * possibilitar a realização de várias tarefas simultâneas
     *
     * **/

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            /*  Recebe os fluxos de entrada e saída, usando objetos temporários */
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /* Método criado automaticamente, utilizado para tratar os bytes enviados e recebidos*/
        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    // Get number of bytes and message in "buffer"
                    bytes = mmInStream.read(buffer);
                    // Send to message queue Handler
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        } // Fim do método run()

        /**
         * Chamado da tela de Controle e Dispositvos para possibilitar o envio de dados para o Arduino
         * Só é possível enviar, se a comunicação for realizada com sucesso, do contrário exibirá
         * uma mensagem de erro.
         *
         * **/
        public void write(String message) {
            // converts entered String into bytes
            byte[] msgBuffer = message.getBytes();
            try {
                // write bytes over BT connection via outstream
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                // if you cannot write, close the application
                //Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                Toast.makeText(getBaseContext(), getString(R.string.errorBTConnect), Toast.LENGTH_LONG).show();
                mToolbar.setSubtitle(getString(R.string.lblStatusError));
                //finish();
            }
        } // Fim do método write()
    } //Fim da Classe ConnectedThread

} //Fim da classe ControlesActivity
