package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name = "laboratorios")
@AllArgsConstructor
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLaboratorio;

    @NotBlank(message = "El nombre del laboratorio es obligatorio.")
    @Column(nullable = false, unique = true)
    private String nombreLaboratorio;

    @NotBlank(message = "La ubicación del laboratorio es obligatoria.")
    @Column(nullable = false)
    private String ubicacion;

    @NotNull(message = "La capacidad del laboratorio es obligatoria.")
    @Min(value = 1, message = "La capacidad del laboratorio debe ser un número positivo.")
    @Column(nullable = false)
    private Integer capacidad;

    public Laboratorio() {

    }
}
