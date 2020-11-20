package com.example.straviatec.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarreraDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Carrera.db";

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public CarreraDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + CarreraDB.CarreraEntry.TABLE_NAME + " ("
                + CarreraDB.CarreraEntry.ID_DEPORTISTA + " INTEGER,"
                + CarreraDB.CarreraEntry.CATEGORIA + " TEXT NOT NULL,"
                + CarreraDB.CarreraEntry.ID_CARRERA + " INTEGER PRIMARY KEY,"
                + CarreraDB.CarreraEntry.NOMBRE_CARRERA + " TEXT NOT NULL,"
                + CarreraDB.CarreraEntry.TIPO + " TEXT NOT NULL,"
                + CarreraDB.CarreraEntry.FECHA + " TEXT NOT NULL,"
                + CarreraDB.CarreraEntry.KILOMETRAJE + " TEXT,"
                + CarreraDB.CarreraEntry.ALTURA + " TEXT,"
                + CarreraDB.CarreraEntry.DURACION + " TEXT,"
                + CarreraDB.CarreraEntry.COMPLETITUD + " BOOLEAN NOT NULL,"
                + CarreraDB.CarreraEntry.RECORRIDO + " TEXT,"
                + "FOREIGN KEY(idCarrera) REFERENCES "+ UsuarioDB.UserEntry.TABLE_NAME+"(idUsuario),"
                + "UNIQUE (" + CarreraDB.CarreraEntry.ID_CARRERA+ "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long createCarrera(SQLiteDatabase db, Carrera carrera) {
        return db.insert(
                CarreraDB.CarreraEntry.TABLE_NAME,
                null,
                carrera.toContentValues());
    }

    public int updateCarrera(Carrera carrera, Integer idCarrera) {
        return getWritableDatabase().update(
                CarreraDB.CarreraEntry.TABLE_NAME,
                carrera.toContentValues(),
                CarreraDB.CarreraEntry.ID_CARRERA + " LIKE ?",
                new String[]{idCarrera.toString()}
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Carrera> getListaCarreras(Integer id) {
        List<Carrera> lista = new ArrayList<Carrera> ();
        Cursor cursor =  getReadableDatabase()
                .rawQuery("SELECT * FROM carrera WHERE idDeportista == ?", new String[]{id.toString()});
        if (cursor.moveToFirst()) {
            do {
                Carrera carrera = new Carrera(cursor.getInt(cursor.getColumnIndex("idDeportista")),
                        cursor.getString(cursor.getColumnIndex("categoria")),
                        cursor.getInt(cursor.getColumnIndex("idCarrera")),
                        cursor.getString(cursor.getColumnIndex("nombreCarrera")),
                        cursor.getString(cursor.getColumnIndex("tipo")),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fecha"))),
                        cursor.getString(cursor.getColumnIndex("kilometraje")),
                        cursor.getString(cursor.getColumnIndex("altura")),
                        cursor.getString(cursor.getColumnIndex("duracion")),
                        Boolean.valueOf(cursor.getString(cursor.getColumnIndex("completitud"))),
                        cursor.getString(cursor.getColumnIndex("recorrido"))
                );
                lista.add(carrera);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Carrera getCarrera(Integer id) {
        Carrera carrera = null;
        Cursor cursor =  getReadableDatabase()
                .rawQuery("SELECT * FROM carrera WHERE idCarrera == ?", new String[]{id.toString()});
        if (cursor.moveToFirst()) {
            do {
               carrera = new Carrera(cursor.getInt(cursor.getColumnIndex("idDeportista")),
                        cursor.getString(cursor.getColumnIndex("categoria")),
                        cursor.getInt(cursor.getColumnIndex("idCarrera")),
                        cursor.getString(cursor.getColumnIndex("nombreCarrera")),
                        cursor.getString(cursor.getColumnIndex("tipo")),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("fecha"))),
                        cursor.getString(cursor.getColumnIndex("kilometraje")),
                        cursor.getString(cursor.getColumnIndex("altura")),
                        cursor.getString(cursor.getColumnIndex("duracion")),
                        Boolean.getBoolean(cursor.getString(cursor.getColumnIndex("completitud"))),
                        cursor.getString(cursor.getColumnIndex("recorrido"))
                );
            } while (cursor.moveToNext());
        }

        return carrera;
    }
}
