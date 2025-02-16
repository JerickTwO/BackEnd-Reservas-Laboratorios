package com.masache.masachetesis.service;

import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.models.Materia;
import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.repositories.HorarioRepository;
import com.masache.masachetesis.repositories.MateriaRepository;
import com.masache.masachetesis.repositories.ReservaRepository;
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
    private final ReservaRepository reservaRepository;
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
     * Validar si un horario coincide con las franjas permitidas.
     */
    private boolean validarFranjaPermitida(LocalTime horaInicio, LocalTime horaFin) {
        return FRANJAS_HORARIAS_PERMITIDAS.stream()
                .anyMatch(franja -> horaInicio.equals(franja[0]) && horaFin.equals(franja[1]));
    }

    /**
     * Validar si el horario a agregar tiene conflictos con otros horarios en el mismo laboratorio y día.
     */






    public List<Horario> obtenerHorariosConReservaAprobada() {
        return horarioRepository.findHorariosConReservaAprobada();
    }

    public void eliminar(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El horario con el ID proporcionado no existe.");
        }
        horarioRepository.deleteById(id);
    }
}
