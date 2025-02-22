package com.masache.masachetesis.service;

import com.masache.masachetesis.models.*;
import com.masache.masachetesis.repositories.AdministradorRepository;
import com.masache.masachetesis.repositories.HorarioRepository;
import com.masache.masachetesis.repositories.LaboratorioRepository;
import com.masache.masachetesis.repositories.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
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

    @Autowired
    private HorarioRepository horarioRepository;

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
        if (!"admin".equals(usuario.getRol().getNombre()) &&
                (reserva.getEstado() == Reserva.EstadoReserva.APROBADA || reserva.getEstado() == Reserva.EstadoReserva.RECHAZADA)) {
            throw new RuntimeException("No tienes permiso para aprobar o rechazar reservas.");
        }
        if (reserva.getLaboratorio() != null && reserva.getLaboratorio().getIdLaboratorio() != null) {
            Laboratorio laboratorio = laboratorioRepository
                    .findById(reserva.getLaboratorio().getIdLaboratorio())
                    .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));

            // VALIDACIÓN: más participantes que capacidad
            if (reserva.getCantidadParticipantes() > laboratorio.getCapacidad()) {
                throw new RuntimeException("Hay demasiados participantes, no se puede realizar la reserva.");
            }
            reserva.setLaboratorio(laboratorio);
        }
        long diffHours = ChronoUnit.HOURS.between(reserva.getHoraInicio(), reserva.getHoraFin());
        if (diffHours != 1) {
            throw new RuntimeException("La reserva debe durar exactamente 1 hora.");
        }

        if ("docente".equals(usuario.getRol().getNombre())) {
            reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        }
        log.info("El usuario logeado es: {}", usuario);

        reserva.setTipoEnum(TipoEnum.RESERVA);
        reserva.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellido());
        log.info("El nombre completo del usuario es: {}", reserva.getNombreCompleto());
        reserva.setCorreo(usuario.getCorreo());
        log.info("El correo del usuario es: {}", reserva.getCorreo());


        // Verificar si el laboratorio existe y asignarlo
        if (reserva.getLaboratorio() != null && reserva.getLaboratorio().getIdLaboratorio() != null) {
            Laboratorio laboratorio = laboratorioRepository.findById(reserva.getLaboratorio().getIdLaboratorio())
                    .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
            // Verificar capacidad del laboratorio antes de aceptar la reserva
            if (reserva.getCantidadParticipantes() > laboratorio.getCapacidad()) {
                throw new RuntimeException("Hay demasiados estudiantes, no se puede realizar la reserva");
            }
            reserva.setLaboratorio(laboratorio);
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

            if (updatedReserva.getLaboratorio() != null && updatedReserva.getLaboratorio().getIdLaboratorio() != null) {
                Laboratorio laboratorio = laboratorioRepository.findById(updatedReserva.getLaboratorio().getIdLaboratorio())
                        .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
                reserva.setLaboratorio(laboratorio);
            }
            log.info("Estado de la reserva: {}", updatedReserva.getEstado());
            if (updatedReserva.getLaboratorio() != null
                    && updatedReserva.getLaboratorio().getIdLaboratorio() != null) {
                Laboratorio laboratorio = laboratorioRepository
                        .findById(updatedReserva.getLaboratorio().getIdLaboratorio())
                        .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));

                if (updatedReserva.getCantidadParticipantes() > laboratorio.getCapacidad()) {
                    throw new RuntimeException("Hay demasiados participantes, no se puede actualizar la reserva.");
                }
                reserva.setLaboratorio(laboratorio);
            }
            if (Reserva.EstadoReserva.APROBADA.equals(updatedReserva.getEstado())) {
                // Crear un nuevo horario y asignarle la reserva aprobada
                Horario horario = new Horario();
                horario.setReserva(reserva);

                // Guardar el horario en la base de datos
                horarioRepository.save(horario);
                log.info("Horario creado para la reserva con ID: {}", updatedReserva.getIdReserva());
            } else {
                log.warn("La reserva no está aprobada, no se creará el horario.{}",updatedReserva.getEstado());
            }
            long diffHours = ChronoUnit.HOURS.between(updatedReserva.getHoraInicio(), updatedReserva.getHoraFin());
            if (diffHours != 1) {
                throw new RuntimeException("La reserva debe durar exactamente 1 hora.");
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
