package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "administradores")
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_administrador")
    private Long idAdministrador;

    @NotBlank(message = "El nombre del administrador es obligatorio.")
    @Column(name = "nombre_administrador")
    private String nombreAdministrador;

    @NotBlank(message = "El apellido del administrador es obligatorio.")
    @Column(name = "apellido_administrador")
    private String apellidoAdministrador;

    @NotBlank(message = "El correo del administrador es obligatorio.")
    @Column(name = "correo_administrador", unique = true)
    private String correoAdministrador;

    @NotBlank(message = "El ID institucional es obligatorio.")
    @Column(name = "id_institucional", unique = true)
    private String idInstitucional;

}
