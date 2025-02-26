
package com.masache.masachetesis.repositories;


import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByCorreo(String correo);
    List<Reserva> findByEstado(Reserva.EstadoReserva estado);
    List<Reserva> findByEstadoAndPeriodo(Reserva.EstadoReserva estado, Periodo periodo);
    List<Reserva> findByPeriodo(Periodo periodo);

}
