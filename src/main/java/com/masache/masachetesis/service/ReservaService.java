
package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    // Obtener todas las reservas
    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    // Obtener una reserva por ID
    public Optional<Reserva> getReservaById(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

    // Crear una nueva reserva
    public Reserva createReserva(Reserva reserva) {
        // Establecer estado inicial como PENDIENTE
        if (reserva.getEstado() == null) {
            reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        }
        return reservaRepository.save(reserva);
    }

    // Actualizar una reserva existente
    public Reserva updateReserva(Long idReserva, Reserva updatedReserva) {
        return reservaRepository.findById(idReserva).map(reserva -> {
            reserva.setNombreCompleto(updatedReserva.getNombreCompleto());
            reserva.setCorreo(updatedReserva.getCorreo());
            reserva.setTelefono(updatedReserva.getTelefono());
            reserva.setOcupacionLaboral(updatedReserva.getOcupacionLaboral());
            reserva.setLaboratorio(updatedReserva.getLaboratorio());
            reserva.setHoraInicio(updatedReserva.getHoraInicio());
            reserva.setHoraFin(updatedReserva.getHoraFin());
            reserva.setMotivoReserva(updatedReserva.getMotivoReserva());
            reserva.setCantidadParticipantes(updatedReserva.getCantidadParticipantes());
            reserva.setRequerimientosTecnicos(updatedReserva.getRequerimientosTecnicos());
            reserva.setEstado(updatedReserva.getEstado()); // Actualizar el estado
            return reservaRepository.save(reserva);
        }).orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));
    }

    // Eliminar una reserva por ID
    public void deleteReserva(Long idReserva) {
        if (!reservaRepository.existsById(idReserva)) {
            throw new RuntimeException("Reserva no encontrada con ID: " + idReserva);
        }
        reservaRepository.deleteById(idReserva);
    }
}
