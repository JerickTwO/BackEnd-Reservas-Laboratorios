package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Dia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaRepository  extends JpaRepository<Dia, Long> {
    Optional<Dia> findByNombre(String nombre);
}
