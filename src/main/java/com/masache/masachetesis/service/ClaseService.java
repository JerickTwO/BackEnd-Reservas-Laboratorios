package com.masache.masachetesis.service;

import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.models.*;
import com.masache.masachetesis.repositories.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClaseService {

    private static final Logger logger = LoggerFactory.getLogger(ClaseService.class);

    private final ClaseRepository claseRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;
    private final PeriodoRepository periodoRepository;
    private final LaboratorioRepository laboratorioRepository;

    public List<Clase> obtenerTodas() {
        logger.info("Obteniendo todas las clases");
        return claseRepository.findAll();
    }

    public Optional<Clase> obtenerPorId(Long id) {
        logger.info("Obteniendo clase con ID: {}", id);
        return claseRepository.findById(id);
    }

    public List<Clase> getClasesByPeriodoActivo() {
        Periodo periodoActivo = periodoRepository.findByEstadoTrue().orElseThrow(() -> new RuntimeException("No hay un período activo"));
        return claseRepository.findByPeriodo(periodoActivo);
    }

    public List<Clase> obtenerPorMateria(Long idMateria) {
        logger.info("Obteniendo clases por ID de Materia: {}", idMateria);
        return claseRepository.findByMateria_IdMateria(idMateria);
    }

    public List<Clase> obtenerPorDocente(Long idDocente) {
        logger.info("Obteniendo clases por ID de Docente: {}", idDocente);
        return claseRepository.findByDocente_IdDocente(idDocente);
    }

    public List<Clase> obtenerPorPeriodo(Long idPeriodo) {
        logger.info("Obteniendo clases por ID de Periodo: {}", idPeriodo);
        return claseRepository.findByPeriodo_IdPeriodo(idPeriodo);
    }

    public Clase guardar(Clase clase) {
        try {
            logger.info("Intentando guardar una nueva clase con Materia ID: {}, Docente ID: {}", clase.getMateria().getIdMateria(), clase.getDocente().getIdDocente());
            Materia materia = materiaRepository.findById(clase.getMateria().getIdMateria()).orElseThrow(() -> {
                logger.error("La materia con ID {} no existe", clase.getMateria().getIdMateria());
                return new IllegalArgumentException("La materia con el ID proporcionado no existe.");
            });

            Docente docente = docenteRepository.findById(clase.getDocente().getIdDocente()).orElseThrow(() -> {
                logger.error("El docente con ID {} no existe", clase.getDocente().getIdDocente());
                return new IllegalArgumentException("El docente con el ID proporcionado no existe.");
            });

            // Buscar el periodo activo
            List<Periodo> periodosActivos = periodoRepository.findByEstado(true);
            if (periodosActivos.isEmpty()) {
                logger.error("No hay periodos activos en el sistema");
                throw new IllegalArgumentException("No hay periodos activos.");
            }

            Periodo periodoActivo = periodosActivos.get(0); // Suponiendo que solo hay un periodo activo a la vez
            clase.setPeriodo(periodoActivo);
            clase.setTipoEnum(TipoEnum.CLASE);
            // Verificar si la clase ya existe
            Optional<Clase> claseExistente = claseRepository.findByMateria_IdMateriaAndDocente_IdDocenteAndPeriodo_IdPeriodo(materia.getIdMateria(), docente.getIdDocente(), periodoActivo.getIdPeriodo());

            if (claseExistente.isPresent()) {
                logger.error("La clase ya existe en el sistema: Materia ID: {}, Docente ID: {}, Periodo ID: {}", materia.getIdMateria(), docente.getIdDocente(), periodoActivo.getIdPeriodo());
                throw new IllegalArgumentException("La clase ya existe en este periodo con este docente y materia.");
            }

            logger.info("Clase guardada exitosamente");
            return claseRepository.save(clase);
        } catch (Exception e) {
            logger.error("Error al guardar la clase: " + e);
            throw new IllegalArgumentException("Error al guardar la clase.");
        }
    }

    @Transactional
    public JsonResponseDto actualizar(Long id, Clase claseActualizada) {
        logger.info("Intentando actualizar clase con ID: {}", id);

        try {
            // Verificar si la clase existe
            Clase clase = claseRepository.findById(id).orElseThrow(() -> {
                logger.error("La clase con ID {} no existe", id);
                return new IllegalArgumentException("La clase con el ID proporcionado no existe.");
            });

            // Verificar la existencia de Materia, Docente y Laboratorio
            Materia materia = materiaRepository.findById(claseActualizada.getMateria().getIdMateria()).orElseThrow(() -> new IllegalArgumentException("La materia con el ID proporcionado no existe."));
            Docente docente = docenteRepository.findById(claseActualizada.getDocente().getIdDocente()).orElseThrow(() -> new IllegalArgumentException("El docente con el ID proporcionado no existe."));
            Laboratorio laboratorio = laboratorioRepository.findById(claseActualizada.getLaboratorio().getIdLaboratorio()).orElseThrow(() -> new IllegalArgumentException("El laboratorio con el ID proporcionado no existe."));

            // Buscar el periodo activo
            List<Periodo> periodosActivos = periodoRepository.findByEstado(true);
            if (periodosActivos.isEmpty()) {
                return new JsonResponseDto(false, 400, "No hay periodos activos.", null, null);
            }
            Periodo periodoActivo = periodosActivos.get(0);

            // Validar si la clase ya existe en el mismo periodo con la misma materia y docente
            Optional<Clase> claseExistente = claseRepository.findByMateria_IdMateriaAndDocente_IdDocenteAndPeriodo_IdPeriodo(materia.getIdMateria(), docente.getIdDocente(), periodoActivo.getIdPeriodo());

            if (claseExistente.isPresent() && !claseExistente.get().getIdClase().equals(id)) {
                return new JsonResponseDto(false, 400, "Ya existe una clase con esta materia y docente en este periodo.", null, null);
            }

            // Validar que la hora de inicio y fin sean válidas
            if (claseActualizada.getHoraInicio().isAfter(claseActualizada.getHoraFin())) {
                return new JsonResponseDto(false, 400, "La hora de inicio no puede ser después de la hora de fin.", null, null);
            }

            // Validar el día de la semana
            if (claseActualizada.getDia() == null) {
                return new JsonResponseDto(false, 400, "Debe especificar un día válido.", null, null);
            }

            // Actualizar la clase
            clase.setMateria(materia);
            clase.setDocente(docente);
            clase.setLaboratorio(laboratorio);
            clase.setPeriodo(periodoActivo);
            clase.setHoraInicio(claseActualizada.getHoraInicio());
            clase.setHoraFin(claseActualizada.getHoraFin());
            clase.setDia(claseActualizada.getDia());
            clase.setTipoEnum(TipoEnum.CLASE);

            Clase claseGuardada = claseRepository.save(clase);

            logger.info("Clase actualizada exitosamente con ID: {}", id);
            return new JsonResponseDto(true, 200, "Clase actualizada correctamente", claseGuardada, null);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al actualizar clase: {}", e.getMessage());
            return new JsonResponseDto(false, 400, "Error de validación", null, e.getMessage());

        } catch (Exception e) {
            logger.error("Error inesperado al actualizar clase: {}", e.getMessage(), e);
            return new JsonResponseDto(false, 500, "Error inesperado al actualizar la clase.", null, e.getMessage());
        }
    }

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
