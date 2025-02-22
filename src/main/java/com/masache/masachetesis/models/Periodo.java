    package com.masache.masachetesis.models;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import jakarta.persistence.*;

    import java.time.LocalDate;
    import java.time.LocalTime;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Entity
    @Table(name = "periodos")
    public class Periodo {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_periodo")
        private Long idPeriodo;

        @Column(name = "nombre_periodo", nullable = false)
        private String nombrePeriodo;

        @Column(nullable = false)
        private LocalDate fechaInicio;

        @Column(nullable = false)
        private LocalDate fechaFin;

        @Column(name = "estado")
        private boolean estado;

        @Column(name = "descripcion")
        private String descripcion;

    }
