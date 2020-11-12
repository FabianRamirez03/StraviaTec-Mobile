package com.example.straviatec.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

import kotlin.UByteArray;

public class UsersDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lawyers.db";

    public UsersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + UsuarioDB.UserEntry.TABLE_NAME + " ("
                + UsuarioDB.UserEntry.ID_USUARIO + " INTEGER PRIMARY KEY,"
                + UsuarioDB.UserEntry.NOMBRE_USUARIO + " TEXT NOT NULL,"
                + UsuarioDB.UserEntry.NOMBRE + " TEXT NOT NULL,"
                + UsuarioDB.UserEntry.APELLIDOS + " TEXT NOT NULL,"
                + UsuarioDB.UserEntry.FECHA_NACIMIENTO + " TEXT,"
                + UsuarioDB.UserEntry.NACIONALIDAD + " TEXT NOT NULL,"
                + UsuarioDB.UserEntry.PASSWORD + " TEXT NOT NULL,"
               // + UsuarioDB.UserEntry.FOTO + " ByteA,"
                + "UNIQUE (" + UsuarioDB.UserEntry.ID_USUARIO+ "))");

        createUser(sqLiteDatabase, new Usuario("2", "wajo10",
                "Wajib", "Zaglul", new Date(), "Costarricense", "wajib123"));
    }

    public long createUser(SQLiteDatabase db, Usuario usuario) {
        return db.insert(
                UsuarioDB.UserEntry.TABLE_NAME,
                null,
                usuario.toContentValues());
    }

    public long saveUser(Usuario usuario) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                UsuarioDB.UserEntry.TABLE_NAME,
                null,
                usuario.toContentValues());
    }

    public Cursor getUser() {
        return getReadableDatabase()
                .query(
                        UsuarioDB.UserEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
