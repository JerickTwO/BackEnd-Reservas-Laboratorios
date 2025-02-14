package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.repositories.PeriodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Periodo guardar(Periodo periodo) {
        // Verifica si el periodo con el mismo nombre ya existe
        if (periodoRepository.existsByNombrePeriodo(periodo.getNombrePeriodo())) {
            throw new IllegalArgumentException("El periodo ya existe.");
        }
        // Guarda el nuevo periodo
        return periodoRepository.save(periodo);
    }

    /**
     * Actualizar un periodo existente.
     */
    @Transactional
    public Periodo actualizar(Long id, Periodo periodoActualizado) {
        // Verifica si el periodo existe
        if (!periodoRepository.existsById(id)) {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
        // Establece el ID del periodo actualizado
        periodoActualizado.setIdPeriodo(id);
        // Actualiza el periodo
        return periodoRepository.save(periodoActualizado);
    }

    /**
     * Eliminar un periodo por ID.
     */
    @Transactional
    public void eliminar(Long id) {
        // Verifica si el periodo existe
        if (!periodoRepository.existsById(id)) {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
        // Elimina el periodo
        periodoRepository.deleteById(id);
    }
}
