package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    // Consulta para obtener docentes y cargar el departamento asociado
    @Query("SELECT d FROM Docente d LEFT JOIN FETCH d.departamento")
    List<Docente> findAllWithDepartamento();

    boolean existsByCorreoDocente(String correoDocente);
    boolean existsByIdInstitucional(String idInstitucional);

}

