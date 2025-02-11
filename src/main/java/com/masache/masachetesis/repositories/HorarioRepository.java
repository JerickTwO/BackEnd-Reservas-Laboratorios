package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByLaboratorio_IdLaboratorioAndDia(Long idLaboratorio, DiaEnum dia);
    List<Horario> findByClase_IdClase(Long idClase);
    boolean existsByFechaAndHoraInicioAndHoraFinAndLaboratorio_IdLaboratorio(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long laboratorioId
    );
}
