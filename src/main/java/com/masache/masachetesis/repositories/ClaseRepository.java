package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Clase;
import com.masache.masachetesis.models.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    // Verificar si una clase ya existe
    Optional<Clase> findByMateria_IdMateriaAndDocente_IdDocenteAndPeriodo_IdPeriodo(Long idMateria, Long idDocente, Long idPeriodo);

    // Obtener clases por materia
    List<Clase> findByMateria_IdMateria(Long idMateria);

    // Obtener clases por docente
    List<Clase> findByDocente_IdDocente(Long idDocente);

    // Obtener clases por periodo
    List<Clase> findByPeriodo_IdPeriodo(Long idPeriodo);

    List<Clase> findByPeriodo(Periodo periodo);

}
