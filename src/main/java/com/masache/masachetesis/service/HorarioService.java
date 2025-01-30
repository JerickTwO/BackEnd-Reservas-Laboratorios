package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.repositories.HorarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {
    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

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
     * Guardar un nuevo horario.
     * Valida que el laboratorio esté presente.
     */
    public Horario guardar(Horario horario) {
        if (horario.getLaboratorio() == null || horario.getLaboratorio().getIdLaboratorio() == null) {
            throw new IllegalArgumentException("El laboratorio es obligatorio.");
        }
        return horarioRepository.save(horario);
    }

    /**
     * Actualizar un horario existente.
     * Valida que el laboratorio esté presente.
     */
    public Horario actualizar(Long id, Horario horarioActualizado) {
        Optional<Horario> horarioExistente = horarioRepository.findById(id);

        if (!horarioExistente.isPresent()) {
            throw new IllegalArgumentException("El horario con el ID proporcionado no existe.");
        }

        if (horarioActualizado.getLaboratorio() == null || horarioActualizado.getLaboratorio().getIdLaboratorio() == null) {
            throw new IllegalArgumentException("El laboratorio es obligatorio.");
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
        horarioRepository.deleteById(id);
    }
}
