package com.masache.masachetesis.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva; // ID único para cada reserva

    @Column(nullable = false)
    private String nombreCompleto; // Nombre completo de quien reserva

    @Column(nullable = false, unique = true)
    private String correo; // Correo electrónico del reservante

    @Column(nullable = false)
    private String telefono; // Teléfono del reservante

    @Column(nullable = false)
    private String ocupacionLaboral; // Ocupación laboral del reservante

    @ManyToOne
    @JoinColumn(name = "id_laboratorio", nullable = false) // Relación con la tabla laboratorios
    private Laboratorio laboratorio; // Entidad Laboratorio

    @Column(nullable = false)
    private LocalDateTime horaInicio; // Hora de inicio de la reserva

    @Column(nullable = false)
    private LocalDateTime horaFin; // Hora de fin de la reserva

    @Column(nullable = false)
    private String motivoReserva; // Motivo de la reserva

    @Column(nullable = false)
    private Integer cantidadParticipantes; // Cantidad de participantes

    @Column
    private String requerimientosTecnicos; // Requerimientos técnicos (opcional)

    @Enumerated(EnumType.STRING) // Almacenar como texto en la base de datos
    @Column(nullable = false)
    private EstadoReserva estado; // Estado de la reserva

    public enum EstadoReserva {
        PENDIENTE, // La reserva está pendiente de aprobación
        ACEPTADA,  // La reserva ha sido aceptada
        RECHAZADA  // La reserva ha sido rechazada
    }
}
