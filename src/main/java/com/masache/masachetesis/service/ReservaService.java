package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Laboratorio;
import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.AdministradorRepository;
import com.masache.masachetesis.repositories.LaboratorioRepository;
import com.masache.masachetesis.repositories.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReservaService {

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private MailService mailService;

    // Obtener todas las reservas
    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    // Obtener una reserva por ID
    public Optional<Reserva> getReservaById(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

    // Crear una nueva reserva
    public Reserva createReserva(Reserva reserva, Usuario usuario) {

        // Asignar automáticamente el nombre y apellido del usuario autenticado
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellido());
        reserva.setCorreo(usuario.getCorreo());

        // Establecer estado inicial como PENDIENTE si no está definido
        if (reserva.getEstado() == null) {
            reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        }

        // Verificar si el laboratorio existe y asignarlo
        if (reserva.getLaboratorio() != null && reserva.getLaboratorio().getIdLaboratorio() != null) {
            reserva.setLaboratorio(laboratorioRepository.findById(reserva.getLaboratorio().getIdLaboratorio())
                    .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado")));
        }

        Reserva nuevaReserva = reservaRepository.save(reserva);

        // Enviar correo solo si hay administradores registrados
        if (administradorRepository.count() > 0) {
            mailService.enviarCorreoReserva(nuevaReserva, usuario);
        } else {
            log.warn("No se envió el correo de reserva porque no hay administradores registrados.");
        }

        return nuevaReserva;
    }

    // Actualizar una reserva existente
    public Reserva updateReserva(Long idReserva, Reserva updatedReserva) {
        return reservaRepository.findById(idReserva).map(reserva -> {

            reserva.setNombreCompleto(updatedReserva.getNombreCompleto());
            reserva.setCorreo(updatedReserva.getCorreo());
            reserva.setTelefono(updatedReserva.getTelefono());
            reserva.setOcupacionLaboral(updatedReserva.getOcupacionLaboral());

            // Verificar si el laboratorio existe antes de asignarlo
            if (updatedReserva.getLaboratorio() != null && updatedReserva.getLaboratorio().getIdLaboratorio() != null) {
                Laboratorio laboratorio = laboratorioRepository.findById(updatedReserva.getLaboratorio().getIdLaboratorio())
                        .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
                reserva.setLaboratorio(laboratorio);
            }

            reserva.setHoraInicio(updatedReserva.getHoraInicio());
            reserva.setHoraFin(updatedReserva.getHoraFin());
            reserva.setMotivoReserva(updatedReserva.getMotivoReserva());
            reserva.setCantidadParticipantes(updatedReserva.getCantidadParticipantes());
            reserva.setRequerimientosTecnicos(updatedReserva.getRequerimientosTecnicos());
            reserva.setEstado(updatedReserva.getEstado());

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
