package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    // Método para buscar laboratorios por nombre ignorando mayúsculas/minúsculas
    List<Laboratorio> findByNombreLaboratorioContainingIgnoreCase(String nombreLaboratorio);
}
