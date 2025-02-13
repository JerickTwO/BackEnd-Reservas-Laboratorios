package com.masache.masachetesis.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaEnum dia; // Ahora usa el enum directamente

    @Column(nullable = false)
    private LocalTime horaInicio; // Maneja horas correctamente con LocalTime

    @Column(nullable = false)
    private LocalTime horaFin;

    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;
}
