package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    boolean existsByNombreCarrera(String nombreCarrera);
}