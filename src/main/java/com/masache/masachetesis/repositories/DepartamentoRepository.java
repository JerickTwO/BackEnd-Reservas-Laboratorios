package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    // Método para verificar si existe un departamento por nombre
    boolean existsByNombreDepartamento(String nombreDepartamento);

    // Método para buscar un departamento por su nombre
    Optional<Departamento> findByNombreDepartamento(String nombreDepartamento);

    // Método para buscar un departamento por su ID institucional (si lo necesitas)
    Optional<Departamento> findByIdDepartamento(Long idDepartamento);}
