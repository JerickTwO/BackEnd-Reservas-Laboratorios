package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Laboratorio;
import com.masache.masachetesis.repositories.LaboratorioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    // Constructor para inyección de dependencias
    public LaboratorioService(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    /**
     * Obtener todos los laboratorios.
     */
    public List<Laboratorio> getAllLaboratorios() {
        return laboratorioRepository.findAll();
    }

    /**
     * Guardar o actualizar un laboratorio.
     * Si el ID es null o menor o igual a 0, se crea un nuevo laboratorio.
     */
    public Laboratorio saveOrUpdateLaboratorio(Laboratorio laboratorio) {
        validarLaboratorio(laboratorio);

        if (laboratorio.getIdLaboratorio() == null || laboratorio.getIdLaboratorio() <= 0) {
            // Creación de un nuevo laboratorio
            laboratorio.setIdLaboratorio(null);
            return laboratorioRepository.save(laboratorio);
        } else {
            // Actualización de un laboratorio existente
            if (!laboratorioRepository.existsById(laboratorio.getIdLaboratorio())) {
                throw new IllegalStateException("El laboratorio con ID " + laboratorio.getIdLaboratorio() + " no existe.");
            }
            return laboratorioRepository.save(laboratorio);
        }
    }

    /**
     * Obtener un laboratorio por su ID.
     */
    public Optional<Laboratorio> getLaboratorioById(Long id) {
        validarId(id);
        return laboratorioRepository.findById(id);
    }

    /**
     * Buscar laboratorios por nombre (contiene).
     */
    public List<Laboratorio> findLaboratoriosByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del laboratorio no puede estar vacío.");
        }
        return laboratorioRepository.findByNombreLaboratorioContainingIgnoreCase(nombre);
    }

    /**
     * Eliminar un laboratorio por su ID.
     */
    public void deleteLaboratorio(Long id) {
        validarId(id);

        if (!laboratorioRepository.existsById(id)) {
            throw new IllegalStateException("El laboratorio con ID " + id + " no existe.");
        }

        laboratorioRepository.deleteById(id);
    }

    /**
     * Validar que el ID sea válido.
     */
    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del laboratorio debe ser un número positivo.");
        }
    }

    /**
     * Validar que el laboratorio tenga datos válidos.
     */
    private void validarLaboratorio(Laboratorio laboratorio) {
        if (laboratorio == null) {
            throw new IllegalArgumentException("El objeto Laboratorio no puede ser nulo.");
        }
        if (laboratorio.getNombreLaboratorio() == null || laboratorio.getNombreLaboratorio().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del laboratorio es obligatorio.");
        }
        if (laboratorio.getUbicacion() == null || laboratorio.getUbicacion().trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación del laboratorio es obligatoria.");
        }
        if (laboratorio.getCapacidad() == null || laboratorio.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad del laboratorio debe ser un número positivo.");
        }
    }
}