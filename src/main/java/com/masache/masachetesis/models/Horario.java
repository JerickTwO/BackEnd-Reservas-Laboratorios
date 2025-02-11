package com.masache.masachetesis.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "horarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fecha", "hora_inicio", "hora_fin", "laboratorio_id"})
})
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha; // Fecha específica del horario

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaEnum dia; // Día de la semana

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio; // Hora de inicio

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin; // Hora de fin

    @ManyToOne
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase; // Relación con la clase

    @ManyToOne
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio; // Relación con el laboratorio
}
