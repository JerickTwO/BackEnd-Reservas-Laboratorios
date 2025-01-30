package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    // Puedes agregar consultas personalizadas si es necesario
}
