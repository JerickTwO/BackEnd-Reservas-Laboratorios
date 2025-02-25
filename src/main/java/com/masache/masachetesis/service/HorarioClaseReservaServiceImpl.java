package com.masache.masachetesis.service;

import com.masache.masachetesis.dto.HorarioReservasDto;
import com.masache.masachetesis.models.Clase;
import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.repositories.ClaseRepository;
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
    private final PeriodoService periodoService;


    public List<HorarioReservasDto> getHorarioClaseReserva() {
        var periodoActivoOpt = periodoService.obtenerPeriodoActivo();
        if (periodoActivoOpt.isEmpty()) {
            throw new NoSuchElementException("No hay un periodo activo.");
        }
        var periodoActivo = periodoActivoOpt.get();
        List<Clase> clases = claseRepository.findByPeriodo(periodoActivo);
        if (clases.isEmpty()) {
            throw new NoSuchElementException("No se encontraron clases para el periodo activo.");
        }
        List<Reserva> reservas = reservaRepository.findByEstadoAndPeriodo(
                Reserva.EstadoReserva.APROBADA, periodoActivo);
        if (reservas.isEmpty()) {
            throw new NoSuchElementException("No se encontraron reservas aprobadas para el periodo activo.");
        }

        List<HorarioReservasDto> horarios = new ArrayList<>();

        // Convertir Reservas a DTO
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
                        .periodo(reserva.getPeriodo())
                        .tipo(reserva.getTipo())
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
                        .periodo(clase.getPeriodo())
                        .tipo(clase.getTipo())
                        .build()
        ).collect(Collectors.toList());

        horarios.addAll(reservasDto);
        horarios.addAll(clasesDto);

        return horarios;
    }
}
