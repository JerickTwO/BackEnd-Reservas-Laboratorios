package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva; // ID único para cada reserva

    @Column(nullable = false)
    private String nombreCompleto; // Nombre completo de quien reserva

    @Column(nullable = false)
    private String correo; // Correo electrónico del reservante

    @Column(nullable = false)
    @Size(min = 10, max = 10)
    private String telefono; // Teléfono del reservante

    @Column(nullable = false)
    private String ocupacionLaboral; // Ocupación laboral del reservante

    @ManyToOne
    @JoinColumn(name = "id_laboratorio", nullable = false) // Relación con la tabla laboratorios
    private Laboratorio laboratorio; // Entidad Laboratorio

    @Column(nullable = false)
    private LocalTime horaInicio; // Hora de inicio de la reserva

    @Column(nullable = false)
    private LocalTime horaFin; // Hora de fin de la reserva


    @Column(nullable = false)
    private String motivoReserva; // Motivo de la reserva

    @Column(nullable = false)
    @Min(1)
    @Max(35)
    private Integer cantidadParticipantes; // Cantidad de participantes

    @Column
    private String requerimientosTecnicos; // Requerimientos técnicos (opcional)

    @Enumerated(EnumType.STRING) // Almacenar como texto en la base de datos
    @Column(nullable = false)
    private EstadoReserva estado; // Estado de la reserva

    public enum EstadoReserva {
        PENDIENTE, // La reserva está pendiente de aprobación
        APROBADA,  // La reserva ha sido aceptada
        RECHAZADA  // La reserva ha sido rechazada
    }
}
