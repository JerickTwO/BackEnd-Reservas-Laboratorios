package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Estudiante;

import com.masache.masachetesis.repositories.EstudianteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstudianteService {
    private final EstudianteRepository estudianteRepository;
    
    // Obtener todos los docentes con el departamento (JOIN FETCH)
    public List<Estudiante> getAllDocentesWithDepartamento() {
        return estudianteRepository.findAllWithCarrera();
    }

    // Obtener un docente por su ID
    public Optional<Estudiante> getEstudianteById(Long id) {
        return estudianteRepository.findById(id);
    }

    // Crear o actualizar un docente

    public Estudiante saveOrUpdateEstudiante(@Valid Estudiante estudiante) {

        // todo: VALIDAR LA SHROAS DE DOCENTE HORIARIO

        // Si el ID es null o 0, tratarlo como un nuevo registro
        if (estudiante.getIdEstudiante() == null || estudiante.getIdEstudiante() == 0) {
            estudiante.setIdEstudiante(null); // Hibernate considera null como un nuevo registro
            return estudianteRepository.save(estudiante);
        } else {
            // Validar si el docente existe antes de intentar actualizar
            if (!estudianteRepository.existsById(estudiante.getIdEstudiante())) {
                throw new IllegalStateException("El docente con ID " + estudiante.getIdEstudiante() + " no existe.");
            }
            return estudianteRepository.save(estudiante); // Actualizar entidad existente
        }
    }


    // Eliminar un docente por su ID
    public void deleteDocente(Long id) {
        if (!estudianteRepository.existsById(id)) {
            throw new IllegalStateException("El docente con ID " + id + " no existe.");
        }
        estudianteRepository.deleteById(id); // Eliminar el docente por ID
    }    

}
