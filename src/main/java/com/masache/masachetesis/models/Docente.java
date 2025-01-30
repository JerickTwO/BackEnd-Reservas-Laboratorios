package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_docente")
    private Long idDocente;

    @NotBlank(message = "El nombre del docente es obligatorio.")
    @Column(name = "nombre_docente")
    private String nombreDocente;

    @NotBlank(message = "El apellido del docente es obligatorio.")
    @Column(name = "apellido_docente")
    private String apellidoDocente;

    @NotBlank(message = "El correo del docente es obligatorio.")
    @Column(name = "correo_docente", unique = true)
    private String correoDocente;

    @NotBlank(message = "El ID institucional es obligatorio.")
    @Column(name = "id_institucional", unique = true)
    private String idInstitucional;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
}
