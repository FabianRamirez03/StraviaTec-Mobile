package com.example.straviatec.dataBase

import android.content.ContentValues
import java.time.LocalDateTime

class Reto(var idDeportista:Int,var idReto:Int, var nombreReto:String,var objetivo: String ,var tipoActividad:String, var tipoReto:String,var fechaInicio: LocalDateTime,
           var fechaFinal: LocalDateTime, var kilometraje: String, var altura: String, var duracion: String, var completitud: Boolean, var recorrido: String) {
    fun toContentValues(): ContentValues? {
        val values = ContentValues()
        values.put(RetoDb.RetoEntry.ID_DEPORTISTA, idDeportista)
        values.put(RetoDb.RetoEntry.ID_RETO, idReto)
        values.put(RetoDb.RetoEntry.NOMBRE_RETO, nombreReto)
        values.put(RetoDb.RetoEntry.OBJETIVO, objetivo)
        values.put(RetoDb.RetoEntry.TIPO_ACTIVIDAD, tipoActividad)
        values.put(RetoDb.RetoEntry.TIPO_RETO, tipoReto)
        values.put(RetoDb.RetoEntry.FECHA_INICIO, fechaInicio.toString())
        values.put(RetoDb.RetoEntry.FECHA_FINAL, fechaFinal.toString())
        values.put(RetoDb.RetoEntry.KILOMETRAJE, kilometraje)
        values.put(RetoDb.RetoEntry.ALTURA, altura)
        values.put(RetoDb.RetoEntry.DURACION, duracion)
        values.put(RetoDb.RetoEntry.COMPLETITUD, completitud)
        values.put(RetoDb.RetoEntry.RECORRIDO, recorrido)
        return values
    }
}