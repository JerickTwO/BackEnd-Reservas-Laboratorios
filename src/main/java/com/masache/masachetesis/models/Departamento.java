package com.masache.masachetesis.models;

import lombok.Data;


import jakarta.persistence.*;


@Data
@Entity
@Table(name = "departamentos")  // Nombre explícito de la tabla
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento") // Nombre de la columna
    private Long idDepartamento;


    @Column(name = "nombre_departamento") // Nombre de la columna
    private String nombreDepartamento;

    @Column(name = "descripcion") // Nombre de la columna
    private String descripcion;

    // Getters y Setters - Generados automáticamente con Lombok
}
