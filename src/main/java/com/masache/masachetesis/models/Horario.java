package com.masache.masachetesis.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario", unique = true, nullable = false)
    private Long idHorario;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

}
