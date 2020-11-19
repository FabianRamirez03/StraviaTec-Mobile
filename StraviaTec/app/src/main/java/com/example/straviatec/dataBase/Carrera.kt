package com.example.straviatec.dataBase

import android.content.ContentValues
import java.time.Duration
import java.time.LocalDateTime

class Carrera(var idDeportista:Int, var categoria:String, var idCarrera:Int, var nombreCarrera:String, var tipo:String, var fecha: LocalDateTime,
var kilometraje: String, var altura: String, var duracion: String, var completitud: Boolean, var recorrido: String) {

    fun toContentValues(): ContentValues? {
        val values = ContentValues()
        values.put(CarreraDB.CarreraEntry.ID_DEPORTISTA, idDeportista)
        values.put(CarreraDB.CarreraEntry.CATEGORIA, categoria)
        values.put(CarreraDB.CarreraEntry.ID_CARRERA, idCarrera)
        values.put(CarreraDB.CarreraEntry.NOMBRE_CARRERA, nombreCarrera)
        values.put(CarreraDB.CarreraEntry.TIPO, tipo)
        values.put(CarreraDB.CarreraEntry.FECHA, fecha.toString())
        values.put(CarreraDB.CarreraEntry.KILOMETRAJE, kilometraje)
        values.put(CarreraDB.CarreraEntry.ALTURA, altura)
        values.put(CarreraDB.CarreraEntry.DURACION, duracion)
        values.put(CarreraDB.CarreraEntry.COMPLETITUD, completitud)
        values.put(CarreraDB.CarreraEntry.RECORRIDO, recorrido)
        return values
    }

}