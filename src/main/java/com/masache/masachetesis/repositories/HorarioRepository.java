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

    /**
     * Obtener todos los horarios de un laboratorio en un día específico.
     */
    List<Horario> findByLaboratorio_IdLaboratorioAndDia(Long idLaboratorio, DiaEnum dia);

    /**
     * Obtener todos los horarios asignados a un docente.
     */
    List<Horario> findByDocente_IdDocente(Long idDocente);

    /**
     * Obtener todos los horarios asignados a una materia.
     */
    List<Horario> findByMateria_IdMateria(Long idMateria);

    /**
     * Obtener horarios en un rango de horas específico en un laboratorio y día.
     */
    @Query("SELECT h FROM Horario h WHERE h.laboratorio.idLaboratorio = :idLaboratorio " +
            "AND h.dia = :dia " +
            "AND h.horaInicio BETWEEN :horaInicio AND :horaFin")
    List<Horario> findByLaboratorio_IdLaboratorioAndDiaAndHoraInicioBetween(
            @Param("idLaboratorio") Long idLaboratorio,
            @Param("dia") DiaEnum dia,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}
