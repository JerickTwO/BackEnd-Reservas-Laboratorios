package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Long idEstudiante;

    @NotBlank(message = "El nombre del estudiante es obligatorio.")
    @Column(name = "nombre_estudiante")
    private String nombreEstudiante;

    @NotBlank(message = "El apellido del estudiante es obligatorio.")
    @Column(name = "apellido_estudiante")
    private String apellidoEstudiante;

    @NotBlank(message = "El correo del estudiante es obligatorio.")
    @Column(name = "correo_estudiante", unique = true)
    private String correoEstudiante;

    @NotBlank(message = "El ID institucional es obligatorio.")
    @Column(name = "id_institucional", unique = true)
    private String idInstitucional;

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;
}
