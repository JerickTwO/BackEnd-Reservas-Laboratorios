package com.masache.masachetesis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    @Size(min = 10, max = 10)
    private String telefono;

    @Column(nullable = false)
    private String ocupacionLaboral;

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
    @Enumerated(EnumType.STRING) // Se almacena como texto en la BD
    @Column(name = "tipo", columnDefinition = "VARCHAR(255) DEFAULT 'RESERVA'")
    private TipoEnum tipoEnum;

    // Fechas de creación y actualización
    @CreationTimestamp
    @Column(name = "creacion", updatable = false)
    private LocalDateTime fechaCreacion; // Fecha de creación

    @UpdateTimestamp
    @Column(name = "actualizacion")
    private LocalDateTime fechaActualizacion; // Fecha de última actualización

}
