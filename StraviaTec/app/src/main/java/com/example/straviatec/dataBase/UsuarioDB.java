package com.example.straviatec.dataBase;

import android.provider.BaseColumns;

import java.util.Date;

import kotlin.UByteArray;

public class UsuarioDB {

    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME ="usuario";

        public static final String ID_USUARIO = "idUsuario";
        public static final String NOMBRE_USUARIO = "nombreUsuario";
        public static final String NOMBRE = "nombre";
        public static final String APELLIDOS = "apellidos";
        public static final String FECHA_NACIMIENTO = "fechaNacimiento";
        public static final String NACIONALIDAD = "nacionalidad";
        public static final String PASSWORD = "password";
        //public static final UByteArray FOTO = new UByteArray();
    }
}