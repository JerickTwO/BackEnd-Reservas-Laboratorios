package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Departamento;
import com.masache.masachetesis.repositories.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {


    @Autowired
    private DepartamentoRepository departamentoRepository;

    /**
     * Obtener todos los departamentos.
     * @return Lista de departamentos.
     */
    public List<Departamento> getAllDepartamentos() {
        return departamentoRepository.findAll();
    }

    /**
     * Crear o actualizar un departamento.
     * @param departamento Objeto Departamento a guardar o actualizar.
     * @return Departamento guardado.
     */
    public Departamento saveOrUpdateDepartamento(Departamento departamento) {
        validarDepartamento(departamento);

        // Si el ID es null o 0, tratarlo como una nueva entidad
        if (departamento.getIdDepartamento() == null || departamento.getIdDepartamento() == 0) {
            departamento.setIdDepartamento(null); // Hibernate solo reconoce null como nuevo registro
            return departamentoRepository.save(departamento);
        } else {
            // Validar si el departamento existe antes de intentar actualizar
            if (!departamentoRepository.existsById(departamento.getIdDepartamento())) {
                throw new IllegalStateException("El departamento con ID " + departamento.getIdDepartamento() + " no existe.");
            }
            return departamentoRepository.save(departamento); // Actualizar entidad existente
        }
    }


    /**
     * Obtener un departamento por su ID.
     * @param id ID del departamento.
     * @return Optional con el departamento si existe.
     */
    public Optional<Departamento> getDepartamentoById(Long id) {
        validarId(id);
        return departamentoRepository.findById(id);
    }

    /**
     * Eliminar un departamento por su ID.
     * @param id ID del departamento.
     */
    public void deleteDepartamento(Long id) {
        validarId(id);

        if (!departamentoRepository.existsById(id)) {
            throw new IllegalStateException("El departamento con ID " + id + " no existe.");
        }

        departamentoRepository.deleteById(id);
    }

    /**
     * Validar que el ID sea válido.
     * @param id ID del departamento.
     */
    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del departamento debe ser un número positivo.");
        }
    }

    /**
     * Validar que el objeto Departamento sea válido.
     * @param departamento Objeto Departamento a validar.
     */
    private void validarDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El objeto Departamento no puede ser nulo.");
        }
        if (departamento.getNombreDepartamento() == null || departamento.getNombreDepartamento().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
    }
}
