package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Docente;
import com.masache.masachetesis.repositories.DocenteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;

    // Obtener todos los docentes con el departamento (JOIN FETCH)
    public List<Docente> getAllDocentesWithDepartamento() {
        return docenteRepository.findAllWithDepartamento();
    }

    // Obtener un docente por su ID
    public Optional<Docente> getDocenteById(Long id) {
        return docenteRepository.findById(id);
    }

    // Crear o actualizar un docente

    public Docente saveOrUpdateDocente(@Valid Docente docente) {

        // todo: VALIDAR LA SHROAS DE DOCENTE HORIARIO

        // Si el ID es null o 0, tratarlo como un nuevo registro
        if (docente.getIdDocente() == null || docente.getIdDocente() == 0) {
            docente.setIdDocente(null); // Hibernate considera null como un nuevo registro
            return docenteRepository.save(docente);
        } else {
            // Validar si el docente existe antes de intentar actualizar
            if (!docenteRepository.existsById(docente.getIdDocente())) {
                throw new IllegalStateException("El docente con ID " + docente.getIdDocente() + " no existe.");
            }
            return docenteRepository.save(docente); // Actualizar entidad existente
        }
    }


    // Eliminar un docente por su ID
    public void deleteDocente(Long id) {
        if (!docenteRepository.existsById(id)) {
            throw new IllegalStateException("El docente con ID " + id + " no existe.");
        }
        docenteRepository.deleteById(id); // Eliminar el docente por ID
    }

}
