package com.example.straviatec.dataBase

import android.content.ContentValues
import java.time.LocalDateTime
import java.time.LocalTime

class Actividad(var idDeportista: Int, var nombreActividad:String, var km:String, var altura:String, var recorrido: String,
                var tipo:String, var duracion:String, var fecha: LocalDateTime) {

    fun toContentValues(): ContentValues? {
        val values = ContentValues()
        values.put(ActividadDB.ActividadEntry.ID_DEPORTISTA, idDeportista)
        values.put(ActividadDB.ActividadEntry.NOMBRE_ACTIVIDAD, nombreActividad)
        values.put(ActividadDB.ActividadEntry.KM, km)
        values.put(ActividadDB.ActividadEntry.ALTURA, altura)
        values.put(ActividadDB.ActividadEntry.RECORRIDO, tipo)
        values.put(ActividadDB.ActividadEntry.TIPO,tipo )
        values.put(ActividadDB.ActividadEntry.DURACION, duracion)
        values.put(ActividadDB.ActividadEntry.FECHA, fecha.toString())
        return values
    }

}