package com.masache.masachetesis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clase")
    private Long idClase;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private Periodo periodo;

    @ManyToOne
    @JoinColumn(name = "id_laboratorio", nullable = false)
    private Laboratorio laboratorio;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaEnum dia;

    @Enumerated(EnumType.STRING) // Se almacena como texto en la BD
    @Column(name = "tipo", columnDefinition = "VARCHAR(255) DEFAULT 'CLASE'")
    private TipoEnum tipoEnum;

    @CreationTimestamp
    @Column(name = "creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "actualizacion")
    private LocalDateTime fechaActualizacion;
}
