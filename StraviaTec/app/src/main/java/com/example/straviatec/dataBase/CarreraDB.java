package com.example.straviatec.dataBase;

import android.provider.BaseColumns;

public class CarreraDB {
    public static abstract class CarreraEntry implements BaseColumns {
        public static final String TABLE_NAME ="carrera";
        public static final String ID_DEPORTISTA= "idDeportista";
        public static final String CATEGORIA = "categoria";
        public static final String ID_CARRERA = "idCarrera";
        public static final String NOMBRE_CARRERA = "nombreCarrera";
        public static final String TIPO = "tipo";
        public static final String FECHA = "fecha";
        public static final String KILOMETRAJE = "kilometraje";
        public static final String ALTURA = "altura";
        public static final String DURACION = "duracion";
        public static final String COMPLETITUD = "completitud";
        public static final String RECORRIDO = "recorrido";
    }
}
