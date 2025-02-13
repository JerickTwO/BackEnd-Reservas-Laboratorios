package com.masache.masachetesis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "username", nullable = false, length = 150, unique = true)
    private String usuario;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido_usuario", nullable = false, length = 50)
    private String apellido;

    @Column(name = "correo_usuario", nullable = false, length = 50, unique = true)
    private String correo;

    @Column(name = "contrasena_usuario", nullable = false, length = 150)
    private String contrasena;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false) // Define la columna de unión en la tabla "usuarios"
    private Roles rol;

    @Column(name = "primer_login", nullable = false, columnDefinition = "boolean default true")
    private boolean primerLogin; // 1: primer login, 0: no es el primer login

    // 1: activo, 0: inactivo
    @Column(name = "estado", nullable = false, columnDefinition = "boolean default true")
    private Boolean estado;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Roles roles = (Roles) o;
        return Objects.equals(nombre, roles.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }


}
