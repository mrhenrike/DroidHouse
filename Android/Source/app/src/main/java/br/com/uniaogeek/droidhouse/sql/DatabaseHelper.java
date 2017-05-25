package br.com.uniaogeek.droidhouse.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.uniaogeek.droidhouse.activities.LoginActivity;
import br.com.uniaogeek.droidhouse.model.Usuario;

/**
 * Created by lalit on 9/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Versão do Banco de Dados
    private static final int DATABASE_VERSION = 1;

    // Nome do Banco de Dados
    private static final String DATABASE_NAME = "DroidHouseApp.db";

    // Nome da Tabela para Usuarios
    private static final String TABLE_USER = "usuarios";

    // Título das colunas da Tabela Usuarios
    private static final String COLUMN_USER_ID = "usu_id";
    private static final String COLUMN_USER_NAME = "usu_nome";
    private static final String COLUMN_USER_EMAIL = "usu_email";
    private static final String COLUMN_USER_PASSWORD = "usu_senha";
    private static final String COLUMN_USER_PERFIL = "usu_perfil";

    // Comando SQL para criar a tabela usuarios e suas colunas
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " VARCHAR(60),"
            + COLUMN_USER_EMAIL + " VARCHAR(80)," + COLUMN_USER_PASSWORD + " VARCHAR(16)," + COLUMN_USER_PERFIL + " VARCHAR(10))";

    //private String INSERT_USER_TABLE = "INSERT INTO `DroidHouse`.`usuarios` (`usu_id`, `usu_nome`, `usu_email`, `usu_senha`, `usu_perfil`) VALUES ('001', 'Administrador', 'dh@ug.com', 'qwerty', 'perfilADM');";

    // Comando SQL para dar um DROP na tabela caso ela exista
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    /**
     * Método Construtor da Classe
     *
     * @param context recebe dados do Banco
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop tabela usuario se existir
        db.execSQL(DROP_USER_TABLE);

        // Cria a Tabela novamente
        onCreate(db);

    }


    /**
     * Método para Adicionar usuarios ao Banco de Dados
     *
     * @param usuario recebe dados para preencher as colunas da tabela usuarios
     */
    public void adicionarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, usuario.getNome());
        values.put(COLUMN_USER_EMAIL, usuario.getEmail());
        values.put(COLUMN_USER_PASSWORD, usuario.getSenha());
        values.put(COLUMN_USER_PERFIL, usuario.getPerfil());

        // Inseri linhas
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * Este metodo busca todos os usuarios cadastrados e retorna os dados em forma de lista
     *
     * @return list retorna lista de usuarios
     */
    public List<Usuario> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_PERFIL
        };

        // Ordena os dados pela coluna Nome
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<Usuario> usuarioList = new ArrayList<Usuario>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,    //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();
                usuario.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                usuario.setNome(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                usuario.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                usuario.setSenha(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                usuario.setPerfil(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PERFIL)));

                // Adding usuario record to list
                usuarioList.add(usuario);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return usuarioList;
    }

    /**
     * This method to update usuario record
     *
     * @param usuario
     */
    public void atualizaUsuarios(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, usuario.getNome());
        values.put(COLUMN_USER_EMAIL, usuario.getEmail());
        values.put(COLUMN_USER_PASSWORD, usuario.getSenha());
        values.put(COLUMN_USER_PERFIL, usuario.getPerfil());

        // Atualiza a linha
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(usuario.getId())});
        db.close();
    }

    /**
     * This method is to delete usuario record
     *
     * @param usuario
     */
    public void deletarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete usuario record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(usuario.getId())});
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean verificaUsuario(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean verificaUsuario(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }
}
