package com.masache.masachetesis.service;

import com.masache.masachetesis.models.*;
import com.masache.masachetesis.repositories.HorarioRepository;
import com.masache.masachetesis.repositories.LaboratorioRepository;
import com.masache.masachetesis.repositories.ClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HorarioService {
    private final HorarioRepository horarioRepository;
    private final ClaseRepository claseRepository;
    private final LaboratorioRepository laboratorioRepository;

    private static final List<LocalTime[]> FRANJAS_HORARIAS_PERMITIDAS = List.of(
            new LocalTime[]{LocalTime.of(7, 0), LocalTime.of(9, 0)},
            new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(11, 0)},
            new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(13, 0)},
            new LocalTime[]{LocalTime.of(13, 30), LocalTime.of(15, 30)}
    );

    public List<Horario> obtenerTodos() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> obtenerPorId(Long id) {
        return horarioRepository.findById(id);
    }

    public List<Horario> obtenerPorLaboratorioYDia(Long idLaboratorio, DiaEnum dia) {
        return horarioRepository.findByLaboratorio_IdLaboratorioAndDia(idLaboratorio, dia);
    }

    public Horario guardar(Horario horario) {
        validarHorario(horario);

        Optional<Clase> claseOpt = claseRepository.findById(horario.getClase().getIdClase());
        Optional<Laboratorio> labOpt = laboratorioRepository.findById(horario.getLaboratorio().getIdLaboratorio());

        if (claseOpt.isEmpty() || labOpt.isEmpty()) {
            throw new IllegalArgumentException("Clase o laboratorio no encontrados.");
        }

        if (!validarFranjaPermitida(horario.getHoraInicio(), horario.getHoraFin())) {
            throw new IllegalArgumentException("El horario no coincide con las franjas permitidas.");
        }

        return horarioRepository.save(horario);
    }

    public Horario actualizar(Long id, Horario horarioActualizado) {
        Horario horarioExistente = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado."));

        // Permitir actualizar solo la materia o cambios válidos dentro de la franja horaria
        if (!validarFranjaPermitida(horarioActualizado.getHoraInicio(), horarioActualizado.getHoraFin())) {
            throw new IllegalArgumentException("El horario no coincide con las franjas permitidas.");
        }

        // Actualizar los datos del horario
        horarioExistente.setFecha(horarioActualizado.getFecha());
        horarioExistente.setDia(horarioActualizado.getDia());
        horarioExistente.setHoraInicio(horarioActualizado.getHoraInicio());
        horarioExistente.setHoraFin(horarioActualizado.getHoraFin());
        horarioExistente.setClase(horarioActualizado.getClase());
        horarioExistente.setLaboratorio(horarioActualizado.getLaboratorio());

        return horarioRepository.save(horarioExistente);
    }

    private void validarHorario(Horario horario) {
        // Si estamos actualizando, no aplicar la validación de existencia previa
        if (horario.getId() != null) {
            return;
        }

        boolean existeHorario = horarioRepository.existsByFechaAndHoraInicioAndHoraFinAndLaboratorio_IdLaboratorio(
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getLaboratorio().getIdLaboratorio()
        );

        if (existeHorario) {
            throw new IllegalArgumentException("Ya existe un horario para esta fecha, día y franja horaria en el laboratorio seleccionado.");
        }
    }

    private boolean validarFranjaPermitida(LocalTime horaInicio, LocalTime horaFin) {
        return FRANJAS_HORARIAS_PERMITIDAS.stream()
                .anyMatch(franja -> horaInicio.equals(franja[0]) && horaFin.equals(franja[1]));
    }

    public void eliminar(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Horario no encontrado.");
        }
        horarioRepository.deleteById(id);
    }
}
