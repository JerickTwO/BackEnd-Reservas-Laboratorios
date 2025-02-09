package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Long idMateria;

    @Column(name = "nombre_materia", nullable = false)
    private String nombreMateria;

    @Column(name = "nrc", nullable = false, unique = true)
    private String nrc;

    private Integer creditos;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente; // Relación con la tabla de docentes
}