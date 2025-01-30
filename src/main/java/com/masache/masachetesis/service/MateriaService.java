package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Materia;
import com.masache.masachetesis.repositories.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    /**
     * Obtener todas las materias.
     * @return Lista de materias.
     */
    public List<Materia> getAllMaterias() {
        return materiaRepository.findAll();
    }

    /**
     * Crear o actualizar una materia.
     * @param materia Objeto Materia a guardar o actualizar.
     * @return Materia guardada.
     */
    public Materia saveOrUpdateMateria(Materia materia) {
        validarMateria(materia);

        // Si el ID es null o 0, tratarlo como una nueva entidad
        if (materia.getIdMateria() == null || materia.getIdMateria() == 0) {
            materia.setIdMateria(null); // Hibernate reconoce null como nuevo registro
            return materiaRepository.save(materia);
        } else {
            // Validar si la materia existe antes de intentar actualizar
            if (!materiaRepository.existsById(materia.getIdMateria())) {
                throw new IllegalStateException("La materia con ID " + materia.getIdMateria() + " no existe.");
            }
            return materiaRepository.save(materia); // Actualizar entidad existente
        }
    }

    /**
     * Obtener una materia por su ID.
     * @param id ID de la materia.
     * @return Optional con la materia si existe.
     */
    public Optional<Materia> getMateriaById(Long id) {
        validarId(id);
        return materiaRepository.findById(id);
    }

    /**
     * Eliminar una materia por su ID.
     * @param id ID de la materia.
     */
    public void deleteMateria(Long id) {
        validarId(id);

        if (!materiaRepository.existsById(id)) {
            throw new IllegalStateException("La materia con ID " + id + " no existe.");
        }

        materiaRepository.deleteById(id);
    }

    /**
     * Validar que el ID sea válido.
     * @param id ID de la materia.
     */
    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de la materia debe ser un número positivo.");
        }
    }

    /**
     * Validar que el objeto Materia sea válido.
     * @param materia Objeto Materia a validar.
     */
    private void validarMateria(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("El objeto Materia no puede ser nulo.");
        }
        if (materia.getNombreMateria() == null || materia.getNombreMateria().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio.");
        }
        if (materia.getNrc() == null || materia.getNrc().trim().isEmpty()) {
            throw new IllegalArgumentException("El NRC de la materia es obligatorio.");
        }
    }
}
