package com.example.straviatec.dataBase;

import java.util.Date;

import kotlin.UByteArray;
import android.content.ContentValues;
public class Usuario {
    private String idUsuario;
    private String nombreUsuario;
    private String nombre;
    private String apellidos;
    private Date fechaNacimiento;
    private String nacionalidad;
    private String password;
    private UByteArray foto;

    public Usuario(String idUsuario,
                  String nombreUsuario, String nombre,
                  String apellidos, Date fechaNacimiento, String nacionalidad, String password) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.nacionalidad = nacionalidad;
        this.password = password;
        //this.foto = foto;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(UsuarioDB.UserEntry.ID_USUARIO, this.idUsuario);
        values.put(UsuarioDB.UserEntry.NOMBRE_USUARIO, nombreUsuario);
        values.put(UsuarioDB.UserEntry.NOMBRE, nombre);
        values.put(UsuarioDB.UserEntry.APELLIDOS, apellidos);
        //values.put(UsuarioDB.UserEntry.FECHA_NACIMIENTO, fechaNacimiento);
        values.put(UsuarioDB.UserEntry.NACIONALIDAD, nacionalidad);
        values.put(UsuarioDB.UserEntry.PASSWORD, password);
        //values.put(UsuarioDB.UserEntry.FOTO, foto);
        return values;
    }

    public String getId() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }
    public String getPassword() {
        return password;
    }
    public UByteArray getFoto() {
        return foto;
    }

}
