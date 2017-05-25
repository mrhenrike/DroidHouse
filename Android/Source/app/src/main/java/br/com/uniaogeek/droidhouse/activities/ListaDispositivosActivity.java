package br.com.uniaogeek.droidhouse.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import br.com.uniaogeek.droidhouse.R;
import me.drakeet.materialdialog.MaterialDialog;


public class ListaDispositivosActivity extends AppCompatActivity {
    private static final boolean D = true;

    /* Envia endereço para ControlesActivity */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /* Debugging para LOGCAT */
    private static String TAG = "LOG DeviceListActivity"; // String para verificar logs

    private AlertDialog.Builder alerta;
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private Toolbar mToolbar;  //Utilizado para criar uma Barra superior com título

    private MaterialDialog mMaterialDialog;  //Utilizado para exibir as mensagens com controle na tela

    /**
     * Método automático para Material Dialog
     * Set up on-click listener for the list (nicked this - unsure)
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        /**
         * onItemClick
         * @param av
         * @param v
         * @param arg2
         * @param arg3
         */
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(ListaDispositivosActivity.this, R.style.CardView))
                    .setTitle(getString(R.string.lblBluetooth))
                    .setMessage(getString(R.string.msgBluetooth))
                    .setPositiveButton(getString(R.string.btnOK), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //mMaterialDialog.dismiss();
                            Toast.makeText(ListaDispositivosActivity.this, getString(R.string.msnAguarde), Toast.LENGTH_SHORT).show(); //Exibe uma mensagem de espera na tela do aparelho
                        }
                    })
                    .setNegativeButton(getString(R.string.btnCancelar), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
            mMaterialDialog.show();


            // Obtem o endereço MAC do dispositivo, que você nos algoritimos 17 caracteres na View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Faza a intent começar a próxima activity, passando pelo extra o endereço MAC.
            Intent i = new Intent(ListaDispositivosActivity.this, ControlesActivity.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_dispositivos);

        /* Inicializar uma Barra Superior */
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setSubtitle(R.string.selecione_dispositivo);
        setSupportActionBar(mToolbar);

    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();

        checkBTState();

        /* Inicializa adaptador do tipo Array para dispositivos pareados anteriormente */
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_device_name);

        // Encontra e lista no ListView os dispositivos pareados
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Recebe o adaptador local do Bluetooth
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Obtem um conjunto de dispositivos e a 'paredDevices', outros itens abaixo, conforme forem surgindo.
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Adicione dispositivos previamente pareados no Array
        if (pairedDevices.size() > 0) {
            //findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE); // make title viewable

            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

        } else {
            /* Exibe mensagem de que nenhum dispositivo foi pareado */
            String noDevices = getResources().getText(R.string.nao_pareado).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    } // FIM do método checkBTState()


    /**
     * checkBTState - Verifica Status de conectividade bluetooth no dispositivo.
     */
    private void checkBTState() {
        /* Verifica se o dispositivo possui Bluetooth, se está ligado ou sendo usado por outra app.. */
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) { /* Verifica a existencia da tecnologia bluetooth no dispositivo.. */
            Toast.makeText(getBaseContext(), getResources().getText(R.string.msnDeviceNotSupported), Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) { // Verifica se o bluetooth está ativo..
                Toast.makeText(getBaseContext(), getResources().getText(R.string.msnBluetoothActive), Toast.LENGTH_SHORT).show();
            } else { /* Se não estiver ativado, soliciata ao usuário a ativação.. */

            /* Exibe uma mensagem na tela do usuario solicitando que habilite o Bluetooth */
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    } // FIM do método checkBTState()

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        /* Inflate the menu; this adds items to the action bar if it is present. */
//        getMenuInflater().inflate(R.menu.menu_device_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        /**
//         * Handle action bar item clicks here.
//         * The action bar will automatically handle clicks on the Home/Up button,
//         * so long as you specify a parent activity in AndroidManifest.xml.
//         */
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_sobre) {
//            startActivity(new Intent(this, SobreActivity.class));
//        } else if (id == R.id.action_ajuda) {
//            startActivity(new Intent(this, SobreActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }


} // FIM da Classe ListaDispositivosActivity
