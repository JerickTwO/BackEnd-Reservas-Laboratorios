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

    @ElementCollection
    @CollectionTable(name = "franjas_horario", joinColumns = @JoinColumn(name = "id_horario"))
    @Column(name = "franja")
    private List<String> franjasHorario;

    @ElementCollection
    @CollectionTable(name = "dias_horario", joinColumns = @JoinColumn(name = "id_horario"))
    @Column(name = "dia")
    private List<String> diasHorario;
}
