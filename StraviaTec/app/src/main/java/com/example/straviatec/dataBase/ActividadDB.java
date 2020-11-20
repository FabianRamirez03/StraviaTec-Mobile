package com.example.straviatec.dataBase;

import android.provider.BaseColumns;

public class ActividadDB
    {
        public static abstract class ActividadEntry implements BaseColumns {
            public static final String TABLE_NAME ="actividad";
            public static final String ID_DEPORTISTA= "idDeportista";
            public static final String ID_ACTIVIDAD= "idActividad";
            public static final String NOMBRE_ACTIVIDAD = "nombreActividad";
            public static final String KM = "km";
            public static final String ALTURA = "altura";
            public static final String RECORRIDO = "recorrido";
            public static final String TIPO = "tipo";
            public static final String DURACION = "duracion";
            public static final String FECHA = "fecha";
            public static final String SINCRONIZADO = "sincronizado";
        }
}
