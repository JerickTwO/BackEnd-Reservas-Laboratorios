package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {
    boolean existsByNombrePeriodo(String nombrePeriodo);

}
