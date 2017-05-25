package br.com.uniaogeek.droidhouse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import br.com.uniaogeek.droidhouse.R;
import br.com.uniaogeek.droidhouse.helpers.ValidaEntradaDados;
import br.com.uniaogeek.droidhouse.sql.DatabaseHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /*Inicio do Escopo de Variáveis da Activity*/
    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout txtLayoutEmail;
    private TextInputLayout txtLayoutSenha;

    private TextInputEditText txtEntradaEmail;
    private TextInputEditText txtEntradaSenha;

    private AppCompatButton btnLogin;

    private AppCompatTextView txtMensagemRegistroCliqueAqui;
    private AppCompatTextView txtMensagemRegistro;

    private ValidaEntradaDados validaEntradaDados;
    private DatabaseHelper databaseHelper;
    /*Fim do Escopo de Variáveis da Activity*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Método para retirar a Barra Superior desta Activity
        getSupportActionBar().hide();

        //Inicializando métodos
        initViews();
        initListeners();
        initObjects();
    }

    /**
     * Método para inicializar todas as views
     */
    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        txtLayoutEmail = (TextInputLayout) findViewById(R.id.txtLayoutEmail);
        txtLayoutSenha = (TextInputLayout) findViewById(R.id.txtLayoutSenha);

        txtEntradaEmail = (TextInputEditText) findViewById(R.id.txtEntradaEmail);
        txtEntradaSenha = (TextInputEditText) findViewById(R.id.txtEntradaSenha);

        btnLogin = (AppCompatButton) findViewById(R.id.btnLogin);

        txtMensagemRegistroCliqueAqui = (AppCompatTextView) findViewById(R.id.txtMensagemRegistroCliqueAqui);
        txtMensagemRegistro = (AppCompatTextView) findViewById(R.id.txtMensagemRegistro);
    }

    /**
     * Método para inicializar todos os listeners
     */
    private void initListeners() {
        btnLogin.setOnClickListener(this);
        txtMensagemRegistroCliqueAqui.setOnClickListener(this);
        txtMensagemRegistro.setOnClickListener(this);
    }

    /**
     * Método para inicializar os objetos
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        validaEntradaDados = new ValidaEntradaDados(activity);
    }

    /**
     * Este método aguarda o Clique do Botão, quando clicado, realiza a verificação com o Banco
     *
     * @param v recebe a view atual
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                //Verifica se o email e senha são válidos
                verificaSQLite();
                break;
            case R.id.txtMensagemRegistro:
                //Acessa a tela de Registro de Usuário
                Intent iReg1 = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(iReg1);
                break;
            case R.id.txtMensagemRegistroCliqueAqui:
                //Acessa a tela de Registro de Usuário
                Intent iReg2 = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(iReg2);
                break;
        }
    }

    /**
     * Método para validar os dados digitados e verificar se existe no SQLite
     */
    private void verificaSQLite() {
        if (!validaEntradaDados.isInputEditTextFilled(txtEntradaEmail, txtLayoutEmail, getString(R.string.error_message_email))) {
            Snackbar.make(nestedScrollView, getString(R.string.error_message_email), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!validaEntradaDados.isInputEditTextEmail(txtEntradaEmail, txtLayoutEmail, getString(R.string.error_message_email))) {
            Snackbar.make(nestedScrollView, getString(R.string.error_message_email), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!validaEntradaDados.isInputEditTextFilled(txtEntradaSenha, txtLayoutSenha, getString(R.string.error_message_password))) {
            Snackbar.make(nestedScrollView, getString(R.string.error_message_password), Snackbar.LENGTH_LONG).show();
            return;
        }

        if (databaseHelper.verificaUsuario(txtEntradaEmail.getText().toString().trim()
                , txtEntradaSenha.getText().toString().trim())) {

            //Intent accountsIntent = new Intent(activity, ListaUsuariosActivity.class);
            //accountsIntent.putExtra("EMAIL", txtEntradaEmail.getText().toString().trim());
            //emptyInputEditText();
            //startActivity(accountsIntent);

            //Abre o Dashboard principal caso o usuário esteja cadastrado e validado
            Intent dashboardIntent = new Intent(activity, ListaDispositivosActivity.class);
            dashboardIntent.putExtra("EMAIL", txtEntradaEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(dashboardIntent);

        } else {
            // Mensagem exibida quando Email ou Senha diferentes do cadastrado
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Método para limpar todos os campos digitáveis da tela de login
     */
    private void emptyInputEditText() {
        txtEntradaEmail.setText(null);
        txtEntradaSenha.setText(null);
    }

}
