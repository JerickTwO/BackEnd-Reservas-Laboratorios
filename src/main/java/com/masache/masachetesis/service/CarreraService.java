package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Carrera;
import com.masache.masachetesis.repositories.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    /**
     * Obtener todas las carreras.
     *
     * @return Lista de carreras.
     */
    public List<Carrera> getAllCarreras() {
        return carreraRepository.findAll();
    }

    /**
     * Obtener una carrera por su ID.
     *
     * @param id ID de la carrera.
     * @return Optional con la carrera si existe.
     */
    public Optional<Carrera> getCarreraById(Long id) {
        validarId(id);
        return carreraRepository.findById(id);
    }

    /**
     * Crear o actualizar una carrera.
     *
     * @param carrera Objeto Carrera a guardar o actualizar.
     * @return Objeto Carrera guardado/actualizado.
     */
    public Carrera saveOrUpdateCarrera(Carrera carrera) {
        validarCarrera(carrera);

        System.out.println("Datos recibidos para guardar/actualizar: " + carrera);

        // Si el ID es null o 0, tratarlo como una nueva entidad
        if (carrera.getIdCarrera() == null || carrera.getIdCarrera() == 0) {
            carrera.setIdCarrera(null); // Hibernate espera null para nuevos registros
            return carreraRepository.save(carrera); // Crear nueva carrera
        } else {
            // Validar si la carrera existe antes de actualizar
            if (!carreraRepository.existsById(carrera.getIdCarrera())) {
                throw new IllegalStateException("La carrera con ID " + carrera.getIdCarrera() + " no existe.");
            }
            return carreraRepository.save(carrera); // Actualizar carrera existente
        }
    }

    /**
     * Eliminar una carrera por su ID.
     *
     * @param id ID de la carrera a eliminar.
     */
    public void deleteCarrera(Long id) {
        validarId(id);

        if (!carreraRepository.existsById(id)) {
            throw new IllegalStateException("La carrera con ID " + id + " no existe.");
        }

        carreraRepository.deleteById(id);
    }

    /**
     * Validar que el ID sea válido.
     *
     * @param id ID a validar.
     */
    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
    }

    /**
     * Validar que el objeto Carrera sea válido.
     *
     * @param carrera Objeto Carrera a validar.
     */
    private void validarCarrera(Carrera carrera) {
        if (carrera == null) {
            throw new IllegalArgumentException("El objeto Carrera no puede ser nulo.");
        }
        if (carrera.getNombreCarrera() == null || carrera.getNombreCarrera().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la carrera es obligatorio.");
        }
        if (carrera.getNombreCarrera().length() < 3 || carrera.getNombreCarrera().length() > 100) {
            throw new IllegalArgumentException("El nombre de la carrera debe tener entre 3 y 100 caracteres.");
        }
    }
}
