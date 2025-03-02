package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    @Query("SELECT COUNT(h) > 0 FROM Horario h WHERE :franja MEMBER OF h.franjasHorario AND :dia MEMBER OF h.diasHorario")
    boolean existsByFranjaAndDia(@Param("franja") String franja, @Param("dia") String dia);

}
