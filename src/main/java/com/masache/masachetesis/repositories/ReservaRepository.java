
package com.masache.masachetesis.repositories;


import com.masache.masachetesis.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Aquí puedes agregar métodos personalizados si los necesitas
}
