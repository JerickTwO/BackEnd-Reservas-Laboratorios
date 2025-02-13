package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@NoArgsConstructor
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

}