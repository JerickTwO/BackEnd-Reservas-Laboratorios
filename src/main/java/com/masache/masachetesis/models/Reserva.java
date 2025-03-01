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
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

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

    @Column(nullable = false)
    private LocalDate fechaReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaEnum dia;

    @Column(nullable = false)
    private String motivoReserva;

    @Column(nullable = false)
    @Min(1)
    @Max(35)
    private Integer cantidadParticipantes;

    @Column
    private String requerimientosTecnicos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    public enum EstadoReserva {
        PENDIENTE,
        APROBADA,
        RECHAZADA
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "VARCHAR(255) DEFAULT 'RESERVA'")
    private TipoEnum tipoEnum;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private Periodo periodo;

    @CreationTimestamp
    @Column(name = "creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "actualizacion")
    private LocalDateTime fechaActualizacion;

}
