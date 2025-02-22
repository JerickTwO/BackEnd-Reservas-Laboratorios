package com.masache.masachetesis.dto;

import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Laboratorio;
import com.masache.masachetesis.models.TipoEnum;
import lombok.*;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HorarioReservasDto {
    private Long id;
    private String nombreDocente;
    private String correoDocente;
    private String motivo;
    private Laboratorio laboratorio;
    private String nombreMateria;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private DiaEnum dia;
    private TipoEnum tipo;
}
