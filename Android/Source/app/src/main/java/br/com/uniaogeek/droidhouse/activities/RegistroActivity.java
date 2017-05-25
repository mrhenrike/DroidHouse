package br.com.uniaogeek.droidhouse.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import br.com.uniaogeek.droidhouse.model.Usuario;
import br.com.uniaogeek.droidhouse.sql.DatabaseHelper;

/**
 * Created by lalit on 8/27/2016.
 */
public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegistroActivity.this;
    private int cont = 0;
    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView txtMensagemCadastro;
    private AppCompatTextView txtMensagemCadastroClique;

    private ValidaEntradaDados validaEntradaDados;
    private DatabaseHelper databaseHelper;
    private Usuario usuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        txtMensagemCadastro = (AppCompatTextView) findViewById(R.id.txtMensagemCadastro);
        txtMensagemCadastroClique = (AppCompatTextView) findViewById(R.id.txtMensagemCadastro);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        txtMensagemCadastro.setOnClickListener(this);
        txtMensagemCadastroClique.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        validaEntradaDados = new ValidaEntradaDados(activity);
        databaseHelper = new DatabaseHelper(activity);
        usuario = new Usuario();

    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                postDataToSQLite();
                break;

            case R.id.txtMensagemCadastro:
                finish();
                break;
            case R.id.txtMensagemCadastroClique:
                finish();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void postDataToSQLite() {
        if (!validaEntradaDados.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!validaEntradaDados.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!validaEntradaDados.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!validaEntradaDados.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!validaEntradaDados.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }

        if (!databaseHelper.verificaUsuario(textInputEditTextEmail.getText().toString().trim())) {

            usuario.setNome(textInputEditTextName.getText().toString().trim());
            usuario.setEmail(textInputEditTextEmail.getText().toString().trim());
            usuario.setSenha(textInputEditTextPassword.getText().toString().trim());
            usuario.setPerfil("perfilUSER");

            databaseHelper.adicionarUsuario(usuario);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();

        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }


    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
    }
}
