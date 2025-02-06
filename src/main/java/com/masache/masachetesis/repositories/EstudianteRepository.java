package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Docente;
import com.masache.masachetesis.models.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    @Query("SELECT e FROM Estudiante e LEFT JOIN FETCH e.carrera")
    List<Estudiante> findAllWithCarrera();
}
