package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
    @Size(max = 5, message = "El campo NRC debe tener un máximo de 5 caracteres.")
    private String nrc;

    private Integer creditos;

}