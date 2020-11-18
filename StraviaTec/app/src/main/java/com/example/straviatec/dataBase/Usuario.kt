package com.example.straviatec.dataBase

import android.content.ContentValues
import java.time.LocalDateTime


class Usuario(var idUsuario: Int,
              var nombreUsuario: String, var nombre:String,
              var apellidos: String, var fechaNacimiento: LocalDateTime, var nacionalidad: String, var password: String, var foto:UByteArray?, var edad: Int?, var categoria:String? ) {

    fun toContentValues(): ContentValues? {
        val values = ContentValues()
        values.put(UsuarioDB.UserEntry.ID_USUARIO, idUsuario)
        values.put(UsuarioDB.UserEntry.NOMBRE_USUARIO, nombreUsuario)
        values.put(UsuarioDB.UserEntry.NOMBRE, nombre)
        values.put(UsuarioDB.UserEntry.APELLIDOS, apellidos)
        values.put(UsuarioDB.UserEntry.FECHA_NACIMIENTO, fechaNacimiento.toString())
        values.put(UsuarioDB.UserEntry.NACIONALIDAD, nacionalidad)
        values.put(UsuarioDB.UserEntry.PASSWORD, password)
        values.put(UsuarioDB.UserEntry.FOTO, foto.toString())
        values.put(UsuarioDB.UserEntry.EDAD, edad)
        values.put(UsuarioDB.UserEntry.CATEGORIA, categoria)
        return values
    }

}