package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    /**
     * Busca materias cuyo nombre contiene una palabra clave (búsqueda no sensible a mayúsculas).
     *
     * @param keyword Palabra clave para buscar.
     * @return Lista de materias que coinciden con la búsqueda.
     */
    List<Materia> findByNombreMateriaContainingIgnoreCase(String keyword);

    /**
     * Obtiene todas las materias con sus docentes asociados.
     *
     * @return Lista de materias con sus docentes.
     */

    boolean existsByNrc(String nrc);

}