package com.example.straviatec.dataBase;

import android.provider.BaseColumns;

public class RetoDb {
    public static abstract class RetoEntry implements BaseColumns {
        public static final String TABLE_NAME ="reto";
        public static final String ID_DEPORTISTA= "idDeportista";
        public static final String ID_RETO = "idReto";
        public static final String NOMBRE_RETO = "nombreReto";
        public static final String OBJETIVO = "objetivo";
        public static final String TIPO_ACTIVIDAD = "tipoActividad";
        public static final String TIPO_RETO = "tipoReto";
        public static final String FECHA_INICIO = "fechaInicio";
        public static final String FECHA_FINAL = "fechaFinal";
        public static final String KILOMETRAJE = "kilometraje";
        public static final String ALTURA = "altura";
        public static final String DURACION = "duracion";
        public static final String COMPLETITUD = "completitud";
        public static final String RECORRIDO = "recorrido";
    }
}
