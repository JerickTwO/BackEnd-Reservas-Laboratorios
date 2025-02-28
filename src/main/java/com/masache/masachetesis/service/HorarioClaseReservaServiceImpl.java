package com.masache.masachetesis.service;

import com.masache.masachetesis.dto.HorarioReservasDto;
import com.masache.masachetesis.models.Clase;
import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.repositories.ClaseRepository;
import com.masache.masachetesis.repositories.PeriodoRepository;
import com.masache.masachetesis.repositories.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class HorarioClaseReservaServiceImpl {

    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final PeriodoRepository periodoRepository;

    public List<HorarioReservasDto> getHorarioClaseReserva() {
        Periodo periodoActivo = periodoRepository.findByEstadoTrue()
                .orElseThrow(() -> new NoSuchElementException("No hay un período activo."));

        List<Clase> clases = claseRepository.findByPeriodo(periodoActivo);
        if (clases.isEmpty()) {
            throw new NoSuchElementException("No se encontraron clases para el período activo.");
        }

        List<Reserva> reservas = reservaRepository.findByEstadoAndPeriodo(
                Reserva.EstadoReserva.APROBADA,
                periodoActivo
        );

        List<HorarioReservasDto> horarios = new ArrayList<>();
        List<HorarioReservasDto> reservasDto = reservas.stream().map(reserva ->
                HorarioReservasDto.builder()
                        .id(reserva.getIdReserva())
                        .nombreDocente(reserva.getNombreCompleto())
                        .correoDocente(reserva.getCorreo())
                        .laboratorio(reserva.getLaboratorio())
                        .nombreMateria(null)
                        .horaInicio(reserva.getHoraInicio())
                        .horaFin(reserva.getHoraFin())
                        .dia(reserva.getDia())
                        .motivo(reserva.getMotivoReserva())
                        .tipo(reserva.getTipoEnum())
                        .build()
        ).collect(Collectors.toList());
        List<HorarioReservasDto> clasesDto = clases.stream().map(clase ->
                HorarioReservasDto.builder()
                        .id(clase.getIdClase())
                        .nombreDocente(clase.getDocente().getNombreDocente() + " " + clase.getDocente().getApellidoDocente())
                        .correoDocente(clase.getDocente().getCorreoDocente())
                        .laboratorio(clase.getLaboratorio())
                        .nombreMateria(clase.getMateria().getNombreMateria())
                        .horaInicio(clase.getHoraInicio())
                        .horaFin(clase.getHoraFin())
                        .dia(clase.getDia())
                        .tipo(clase.getTipoEnum())
                        .build()
        ).collect(Collectors.toList());

        horarios.addAll(reservasDto);
        horarios.addAll(clasesDto);

        return horarios;
    }
}