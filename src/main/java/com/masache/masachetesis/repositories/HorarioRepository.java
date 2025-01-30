package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    // Cambiar "Id" por "IdLaboratorio"
    List<Horario> findByLaboratorio_IdLaboratorio(Long idLaboratorio);

}
