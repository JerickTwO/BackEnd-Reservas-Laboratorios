package com.masache.masachetesis.service;

import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.models.Materia;
import com.masache.masachetesis.repositories.HorarioRepository;
import com.masache.masachetesis.repositories.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class HorarioService {
    private final HorarioRepository horarioRepository;
    private final MateriaRepository materiaRepository;


    // Franjas horarias permitidas (7-9, 9-11, 11-13, 13:30-15:30)
    private static final List<LocalTime[]> FRANJAS_HORARIAS_PERMITIDAS = List.of(
            new LocalTime[]{LocalTime.of(7, 0), LocalTime.of(9, 0)},
            new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(11, 0)},
            new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(13, 0)},
            new LocalTime[]{LocalTime.of(13, 30), LocalTime.of(15, 30)}
    );

    /**
     * Obtener todos los horarios.
     */
    public List<Horario> obtenerTodos() {
        return horarioRepository.findAll();
    }

    /**
     * Obtener un horario por su ID.
     */
    public Optional<Horario> obtenerPorId(Long id) {
        return horarioRepository.findById(id);
    }

    /**
     * Obtener horarios por laboratorio y día.
     */
    public List<Horario> obtenerPorLaboratorioYDia(Long idLaboratorio, DiaEnum dia) {
        return horarioRepository.findByLaboratorio_IdLaboratorioAndDia(idLaboratorio, dia);
    }

    /**
     * Obtener horarios por docente.
     */
    public List<Horario> obtenerPorDocente(Long idDocente) {
        return horarioRepository.findByDocente_IdDocente(idDocente);
    }

    /**
     * Obtener horarios por materia.
     */
    public List<Horario> obtenerPorMateria(Long idMateria) {
        return horarioRepository.findByMateria_IdMateria(idMateria);
    }

    /**
     * Validar si un horario coincide con las franjas permitidas.
     */
    private boolean validarFranjaPermitida(LocalTime horaInicio, LocalTime horaFin) {
        return FRANJAS_HORARIAS_PERMITIDAS.stream()
                .anyMatch(franja -> horaInicio.equals(franja[0]) && horaFin.equals(franja[1]));
    }

    /**
     * Validar si el horario a agregar tiene conflictos con otros horarios en el mismo laboratorio y día.
     */
    private boolean validarConflictosDeHorario(Horario nuevoHorario) {
        List<Horario> horariosExistentes = horarioRepository.findByLaboratorio_IdLaboratorioAndDia(
                nuevoHorario.getLaboratorio().getIdLaboratorio(),
                nuevoHorario.getDia()
        );

        for (Horario horario : horariosExistentes) {
            if ((nuevoHorario.getHoraInicio().isBefore(horario.getHoraFin()) && nuevoHorario.getHoraFin().isAfter(horario.getHoraInicio())) ||
                    nuevoHorario.getHoraInicio().equals(horario.getHoraInicio()) ||
                    nuevoHorario.getHoraFin().equals(horario.getHoraFin())) {
                return false; // Conflicto detectado
            }
        }
        return true; // No hay conflictos
    }

    /**
     * Validar si el docente excede las 20 horas semanales de carga horaria.
     */
    private boolean validarCargaHorariaDocente(Horario nuevoHorario) {
        List<Horario> horariosDocente = horarioRepository.findByDocente_IdDocente(nuevoHorario.getDocente().getIdDocente());

        int horasActuales = horariosDocente.stream()
                .mapToInt(h -> (int) Duration.between(h.getHoraInicio(), h.getHoraFin()).toHours())
                .sum();

        int horasNuevoHorario = (int) Duration.between(nuevoHorario.getHoraInicio(), nuevoHorario.getHoraFin()).toHours();

        return horasActuales + horasNuevoHorario <= 20; // Máximo 20 horas semanales
    }

    /**
     * Validar si una materia respeta sus horas semanales según los créditos.
     */
    private boolean validarHorasPorMateria(Horario nuevoHorario) {
        if (nuevoHorario.getMateria() == null || nuevoHorario.getMateria().getIdMateria() == null) {
            throw new IllegalArgumentException("La materia no puede ser nula.");
        }

        // Buscar la materia en la base de datos para obtener los créditos
        Optional<Materia> materiaOpt = materiaRepository.findById(nuevoHorario.getMateria().getIdMateria());

        if (materiaOpt.isEmpty()) {
            throw new IllegalArgumentException("La materia con ID " + nuevoHorario.getMateria().getIdMateria() + " no existe.");
        }

        Materia materia = materiaOpt.get();

        // Obtener horarios actuales de la materia
        List<Horario> horariosMateria = horarioRepository.findByMateria_IdMateria(materia.getIdMateria());

        int horasActuales = horariosMateria.stream()
                .mapToInt(h -> (int) Duration.between(h.getHoraInicio(), h.getHoraFin()).toHours())
                .sum();

        int maxHorasPermitidas = (materia.getCreditos() == 9) ? 6 : (materia.getCreditos() == 6) ? 4 : 0;
        int horasNuevoHorario = (int) Duration.between(nuevoHorario.getHoraInicio(), nuevoHorario.getHoraFin()).toHours();

        return horasActuales + horasNuevoHorario <= maxHorasPermitidas;
    }


    /**
     * Guardar un nuevo horario asegurando que cumple todas las validaciones.
     */
    public Horario guardar(Horario horario) {
        // Validar que la materia no sea nula
        if (horario.getMateria() == null || horario.getMateria().getIdMateria() == null) {
            throw new IllegalArgumentException("La materia es obligatoria.");
        }

        // Cargar la materia desde la base de datos
        Optional<Materia> materiaOpt = materiaRepository.findById(horario.getMateria().getIdMateria());
        if (materiaOpt.isEmpty()) {
            throw new IllegalArgumentException("La materia con ID " + horario.getMateria().getIdMateria() + " no existe.");
        }
        horario.setMateria(materiaOpt.get());

        // Validaciones
        if (!validarFranjaPermitida(horario.getHoraInicio(), horario.getHoraFin())) {
            throw new IllegalArgumentException("El horario no coincide con las franjas horarias permitidas.");
        }
        if (!validarConflictosDeHorario(horario)) {
            throw new IllegalArgumentException("Conflicto de horario detectado.");
        }
        if (!validarCargaHorariaDocente(horario)) {
            throw new IllegalArgumentException("El docente excede las 20 horas semanales.");
        }
        if (!validarHorasPorMateria(horario)) {
            throw new IllegalArgumentException("La materia excede las horas semanales permitidas.");
        }

        return horarioRepository.save(horario);
    }


    /**
     * Actualizar un horario existente con validaciones.
     */
    public Horario actualizar(Long id, Horario horarioActualizado) {
        Optional<Horario> horarioExistente = horarioRepository.findById(id);
        if (horarioExistente.isEmpty()) {
            throw new IllegalArgumentException("El horario con el ID proporcionado no existe.");
        }

        if (!validarFranjaPermitida(horarioActualizado.getHoraInicio(), horarioActualizado.getHoraFin())) {
            throw new IllegalArgumentException("El horario no coincide con las franjas horarias permitidas.");
        }
        if (!validarConflictosDeHorario(horarioActualizado)) {
            throw new IllegalArgumentException("Conflicto de horario detectado.");
        }
        if (!validarCargaHorariaDocente(horarioActualizado)) {
            throw new IllegalArgumentException("El docente excede las 20 horas semanales.");
        }
        if (!validarHorasPorMateria(horarioActualizado)) {
            throw new IllegalArgumentException("La materia excede las horas semanales permitidas.");
        }

        Horario horario = horarioExistente.get();
        horario.setDia(horarioActualizado.getDia());
        horario.setHoraInicio(horarioActualizado.getHoraInicio());
        horario.setHoraFin(horarioActualizado.getHoraFin());
        horario.setMateria(horarioActualizado.getMateria());
        horario.setDocente(horarioActualizado.getDocente());
        horario.setLaboratorio(horarioActualizado.getLaboratorio());

        return horarioRepository.save(horario);
    }

    /**
     * Eliminar un horario por su ID.
     */
    public void eliminar(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El horario con el ID proporcionado no existe.");
        }
        horarioRepository.deleteById(id);
    }
}
