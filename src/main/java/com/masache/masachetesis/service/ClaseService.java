package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Clase;
import com.masache.masachetesis.models.Docente;
import com.masache.masachetesis.models.Materia;
import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.repositories.ClaseRepository;
import com.masache.masachetesis.repositories.DocenteRepository;
import com.masache.masachetesis.repositories.MateriaRepository;
import com.masache.masachetesis.repositories.PeriodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClaseService {

    private static final Logger logger = LoggerFactory.getLogger(ClaseService.class);

    private final ClaseRepository claseRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;
    private final PeriodoRepository periodoRepository;

    public ClaseService(ClaseRepository claseRepository, MateriaRepository materiaRepository,
                        DocenteRepository docenteRepository, PeriodoRepository periodoRepository) {
        this.claseRepository = claseRepository;
        this.materiaRepository = materiaRepository;
        this.docenteRepository = docenteRepository;
        this.periodoRepository = periodoRepository;
    }

    /**
     * Obtener todas las clases.
     */
    public List<Clase> obtenerTodas() {
        logger.info("Obteniendo todas las clases");
        return claseRepository.findAll();
    }

    /**
     * Obtener una clase por ID.
     */
    public Optional<Clase> obtenerPorId(Long id) {
        logger.info("Obteniendo clase con ID: {}", id);
        return claseRepository.findById(id);
    }

    /**
     * Obtener clases por materia.
     */
    public List<Clase> obtenerPorMateria(Long idMateria) {
        logger.info("Obteniendo clases por ID de Materia: {}", idMateria);
        return claseRepository.findByMateria_IdMateria(idMateria);
    }

    /**
     * Obtener clases por docente.
     */
    public List<Clase> obtenerPorDocente(Long idDocente) {
        logger.info("Obteniendo clases por ID de Docente: {}", idDocente);
        return claseRepository.findByDocente_IdDocente(idDocente);
    }

    /**
     * Obtener clases por periodo.
     */
    public List<Clase> obtenerPorPeriodo(Long idPeriodo) {
        logger.info("Obteniendo clases por ID de Periodo: {}", idPeriodo);
        return claseRepository.findByPeriodo_IdPeriodo(idPeriodo);
    }

    /**
     * Guardar una nueva clase, verificando si ya existe.
     */
    public Clase guardar(Clase clase) {
        try {
            logger.info("Intentando guardar una nueva clase con Materia ID: {}, Docente ID: {}, Periodo ID: {}",
                    clase.getMateria().getIdMateria(), clase.getDocente().getIdDocente(), clase.getPeriodo().getIdPeriodo());

            // Verificar existencia de Materia, Docente y Periodo
            Materia materia = materiaRepository.findById(clase.getMateria().getIdMateria())
                    .orElseThrow(() -> {
                        logger.error("La materia con ID {} no existe", clase.getMateria().getIdMateria());
                        return new IllegalArgumentException("La materia con el ID proporcionado no existe.");
                    });

            Docente docente = docenteRepository.findById(clase.getDocente().getIdDocente())
                    .orElseThrow(() -> {
                        logger.error("El docente con ID {} no existe", clase.getDocente().getIdDocente());
                        return new IllegalArgumentException("El docente con el ID proporcionado no existe.");
                    });

            Periodo periodo = periodoRepository.findById(clase.getPeriodo().getIdPeriodo())
                    .orElseThrow(() -> {
                        logger.error("El periodo con ID {} no existe", clase.getPeriodo().getIdPeriodo());
                        return new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
                    });

            clase.setMateria(materia);
            clase.setDocente(docente);
            clase.setPeriodo(periodo);

            // Verificar si la clase ya existe
            Optional<Clase> claseExistente = claseRepository.findByMateria_IdMateriaAndDocente_IdDocenteAndPeriodo_IdPeriodo(
                    materia.getIdMateria(), docente.getIdDocente(), periodo.getIdPeriodo());

            if (claseExistente.isPresent()) {
                logger.error("La clase ya existe en el sistema: Materia ID: {}, Docente ID: {}, Periodo ID: {}",
                        materia.getIdMateria(), docente.getIdDocente(), periodo.getIdPeriodo());
                throw new IllegalArgumentException("La clase ya existe en este periodo con este docente y materia.");
            }

            logger.info("Clase guardada exitosamente");
            return claseRepository.save(clase);
        }
        catch (Exception e) {
            logger.error("Error al guardar la clase: "+  e);
            throw new IllegalArgumentException("Error al guardar la clase.");
        }
    }


    /**
     * Actualizar una clase existente.
     */
    @Transactional
    public Clase actualizar(Long id, Clase claseActualizada) {
        logger.info("Intentando actualizar clase con ID: {}", id);

        // Verificar si la clase existe
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("La clase con ID {} no existe", id);
                    return new IllegalArgumentException("La clase con el ID proporcionado no existe.");
                });

        // Verificar existencia de Materia, Docente y Periodo
        Materia materia = materiaRepository.findById(claseActualizada.getMateria().getIdMateria())
                .orElseThrow(() -> {
                    logger.error("La materia con ID {} no existe", claseActualizada.getMateria().getIdMateria());
                    return new IllegalArgumentException("La materia con el ID proporcionado no existe.");
                });
        Docente docente = docenteRepository.findById(claseActualizada.getDocente().getIdDocente())
                .orElseThrow(() -> {
                    logger.error("El docente con ID {} no existe", claseActualizada.getDocente().getIdDocente());
                    return new IllegalArgumentException("El docente con el ID proporcionado no existe.");
                });
        Periodo periodo = periodoRepository.findById(claseActualizada.getPeriodo().getIdPeriodo())
                .orElseThrow(() -> {
                    logger.error("El periodo con ID {} no existe", claseActualizada.getPeriodo().getIdPeriodo());
                    return new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
                });

        // Actualizar los datos de la clase
        clase.setMateria(materia);
        clase.setDocente(docente);
        clase.setPeriodo(periodo);

        logger.info("Clase actualizada exitosamente");
        return claseRepository.save(clase);
    }

    /**
     * Eliminar una clase por ID.
     */
    @Transactional
    public void eliminar(Long id) {
        logger.info("Intentando eliminar clase con ID: {}", id);

        if (!claseRepository.existsById(id)) {
            logger.error("La clase con ID {} no existe", id);
            throw new IllegalArgumentException("La clase con el ID proporcionado no existe.");
        }
        claseRepository.deleteById(id);
        logger.info("Clase eliminada exitosamente");
    }
}
