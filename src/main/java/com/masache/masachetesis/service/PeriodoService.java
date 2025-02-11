package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.repositories.PeriodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodoService {

    private final PeriodoRepository periodoRepository;

    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    /**
     * Obtener todos los periodos.
     */
    public List<Periodo> obtenerTodos() {
        return periodoRepository.findAll();
    }

    /**
     * Obtener un periodo por ID.
     */
    public Optional<Periodo> obtenerPorId(Long id) {
        return periodoRepository.findById(id);
    }

    /**
     * Guardar un nuevo periodo.
     */
    public Periodo guardar(Periodo periodo) {
        if (periodoRepository.existsByNombrePeriodo(periodo.getNombrePeriodo())) {
            throw new IllegalArgumentException("El periodo ya existe.");
        }
        return periodoRepository.save(periodo);
    }

    /**
     * Actualizar un periodo existente.
     */
    public Periodo actualizar(Long id, Periodo periodoActualizado) {
        if (!periodoRepository.existsById(id)) {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
        periodoActualizado.setIdPeriodo(id);
        return periodoRepository.save(periodoActualizado);
    }

    /**
     * Eliminar un periodo por ID.
     */
    public void eliminar(Long id) {
        if (!periodoRepository.existsById(id)) {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
        periodoRepository.deleteById(id);
    }
}
