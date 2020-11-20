package com.example.straviatec.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RetoDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reto.db";

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public RetoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + RetoDb.RetoEntry.TABLE_NAME + " ("
                + RetoDb.RetoEntry.ID_DEPORTISTA + " INTEGER,"
                + RetoDb.RetoEntry.ID_RETO + " INTEGER PRIMARY KEY,"
                + RetoDb.RetoEntry.NOMBRE_RETO + " Text NOT NULL,"
                + RetoDb.RetoEntry.OBJETIVO + " TEXT,"
                + RetoDb.RetoEntry.TIPO_ACTIVIDAD + " TEXT,"
                + RetoDb.RetoEntry.TIPO_RETO + " TEXT,"
                + RetoDb.RetoEntry.FECHA_INICIO + " TEXT,"
                + RetoDb.RetoEntry.FECHA_FINAL + " TEXT,"
                + RetoDb.RetoEntry.KILOMETRAJE + " TEXT,"
                + RetoDb.RetoEntry.ALTURA + " TEXT,"
                + RetoDb.RetoEntry.DURACION + " TEXT,"
                + RetoDb.RetoEntry.COMPLETITUD + " BOOLEAN NOT NULL,"
                + RetoDb.RetoEntry.RECORRIDO + " TEXT,"
                + "FOREIGN KEY(idReto) REFERENCES "+ UsuarioDB.UserEntry.TABLE_NAME+"(idDeportista),"
                + "UNIQUE (" + RetoDb.RetoEntry.ID_RETO+ "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public int updateReto(Reto reto, Integer idReto) {
        return getWritableDatabase().update(
                RetoDb.RetoEntry.TABLE_NAME,
                reto.toContentValues(),
                RetoDb.RetoEntry.ID_RETO + " LIKE ?",
                new String[]{idReto.toString()}
        );
    }

    public long createReto(SQLiteDatabase db, Reto reto) {
        return db.insert(
                RetoDb.RetoEntry.TABLE_NAME,
                null,
                reto.toContentValues());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Reto> getListaRetos(Integer id) {
        List<Reto> lista = new ArrayList<Reto>();
        Cursor cursor =  getReadableDatabase()
                .rawQuery("SELECT * FROM reto WHERE idDeportista == ?", new String[]{id.toString()});
        if (cursor.moveToFirst()) {
            do {
                Reto reto = new Reto(cursor.getInt(cursor.getColumnIndex("idDeportista")),
                        cursor.getInt(cursor.getColumnIndex("idReto")),
                        cursor.getString(cursor.getColumnIndex("nombreReto")),
                        cursor.getString(cursor.getColumnIndex("objetivo")),
                        cursor.getString(cursor.getColumnIndex("tipoActividad")),
                        cursor.getString(cursor.getColumnIndex("tipoReto")),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fechaInicio"))),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fechaFinal"))),
                        cursor.getString(cursor.getColumnIndex("kilometraje")),
                        cursor.getString(cursor.getColumnIndex("altura")),
                        cursor.getString(cursor.getColumnIndex("duracion")),
                        Boolean.valueOf(cursor.getString(cursor.getColumnIndex("completitud"))),
                        cursor.getString(cursor.getColumnIndex("recorrido"))
                );
                lista.add(reto);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Reto getReto(Integer id) {
        Cursor cursor =  getReadableDatabase()
                .rawQuery("SELECT * FROM reto WHERE idReto == ?", new String[]{id.toString()});
        Reto reto = null;
        if (cursor.moveToFirst()) {
            do {
                reto = new Reto(cursor.getInt(cursor.getColumnIndex("idDeportista")),
                        cursor.getInt(cursor.getColumnIndex("idReto")),
                        cursor.getString(cursor.getColumnIndex("nombreReto")),
                        cursor.getString(cursor.getColumnIndex("objetivo")),
                        cursor.getString(cursor.getColumnIndex("tipoActividad")),
                        cursor.getString(cursor.getColumnIndex("tipoReto")),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fechaInicio"))),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fechaFinal"))),
                        cursor.getString(cursor.getColumnIndex("kilometraje")),
                        cursor.getString(cursor.getColumnIndex("altura")),
                        cursor.getString(cursor.getColumnIndex("duracion")),
                        Boolean.getBoolean(cursor.getString(cursor.getColumnIndex("completitud"))),
                        cursor.getString(cursor.getColumnIndex("recorrido"))
                );
            } while (cursor.moveToNext());
        }
        return reto;
    }
}
