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

public class ActividadDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Actividad.db";
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public ActividadDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + ActividadDB.ActividadEntry.TABLE_NAME + " ("
                + ActividadDB.ActividadEntry.ID_DEPORTISTA + " INTEGER NOT NULL,"
                + ActividadDB.ActividadEntry.ID_ACTIVIDAD + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ActividadDB.ActividadEntry.NOMBRE_ACTIVIDAD + " TEXT NOT NULL,"
                + ActividadDB.ActividadEntry.KM+ " TEXT ,"
                + ActividadDB.ActividadEntry.ALTURA + " TEXT,"
                + ActividadDB.ActividadEntry.RECORRIDO + " TEXT,"
                + ActividadDB.ActividadEntry.TIPO + " TEXT,"
                + ActividadDB.ActividadEntry.DURACION + " TEXT,"
                + ActividadDB.ActividadEntry.FECHA + " TEXT,"
                + "UNIQUE (" + CarreraDB.CarreraEntry.ID_CARRERA+ "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long createActividad(SQLiteDatabase db, Actividad actividad) {
        return db.insert(
                RetoDb.RetoEntry.TABLE_NAME,
                null,
                actividad.toContentValues());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Actividad> getListaActividades(Integer id) {
        List<Actividad> lista = new ArrayList<Actividad>();
        Cursor cursor =  getReadableDatabase()
                .rawQuery("SELECT * FROM actividad WHERE idDeportista == ?", new String[]{id.toString()});
        if (cursor.moveToFirst()) {
            do {
                Actividad actividad = new Actividad(cursor.getInt(cursor.getColumnIndex("idDeportista")),
                        cursor.getString(cursor.getColumnIndex("nombreActividad")),
                        cursor.getString(cursor.getColumnIndex("km")),
                        cursor.getString(cursor.getColumnIndex("altura")),
                        cursor.getString(cursor.getColumnIndex("recorrido")),
                        cursor.getString(cursor.getColumnIndex("tipo")),
                        cursor.getString(cursor.getColumnIndex("duracion")),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fecha")))
                );
                lista.add(actividad);
            } while (cursor.moveToNext());
        }
        return lista;
    }
}
