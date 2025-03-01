package com.masache.masachetesis.service;

import com.masache.masachetesis.models.*;
import com.masache.masachetesis.repositories.*;
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
    private PeriodoRepository periodoRepository;

    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> getReservaById(Long idReserva) {
        return reservaRepository.findById(idReserva);
    }

    public List<Reserva> getReservasByPeriodoActivo() {
        Periodo periodoActivo = periodoRepository.findByEstadoTrue().orElseThrow(() -> new RuntimeException("No hay un período activo."));
        return reservaRepository.findByPeriodo(periodoActivo);
    }

    public Reserva createReserva(Reserva reserva, Usuario usuario) {
        if (!"admin".equals(usuario.getRol().getNombre()) &&
                (reserva.getEstado() == Reserva.EstadoReserva.APROBADA || reserva.getEstado() == Reserva.EstadoReserva.RECHAZADA)) {
            throw new RuntimeException("No tienes permiso para aprobar o rechazar reservas.");
        }

        // Validar que la fecha de reserva existe
        if (reserva.getFechaReserva() == null) {
            throw new RuntimeException("La fecha de reserva es obligatoria.");
        }

        try {
            // Obtener el periodo activo
            Periodo periodoActivo = periodoRepository.findByEstadoTrue()
                    .orElseThrow(() -> new RuntimeException("No hay un período activo."));

            // Validar que la fecha de reserva está dentro del periodo
            if (reserva.getFechaReserva().isAfter(periodoActivo.getFechaFin())) {
                throw new RuntimeException("La fecha de reserva no puede ser posterior a la fecha de fin del periodo (" + periodoActivo.getFechaFin() + ")");
            }

            if (reserva.getFechaReserva().isBefore(periodoActivo.getFechaInicio())) {
                throw new RuntimeException("La fecha de reserva no puede ser anterior a la fecha de inicio del periodo (" + periodoActivo.getFechaInicio() + ")");
            }

            // Establecer el día automáticamente basado en la fecha de reserva
            try {
                String diaEnIngles = reserva.getFechaReserva().getDayOfWeek().name();
                String diaEnEspanol;

                switch (diaEnIngles) {
                    case "MONDAY":
                        diaEnEspanol = "LUNES";
                        break;
                    case "TUESDAY":
                        diaEnEspanol = "MARTES";
                        break;
                    case "WEDNESDAY":
                        diaEnEspanol = "MIERCOLES";
                        break;
                    case "THURSDAY":
                        diaEnEspanol = "JUEVES";
                        break;
                    case "FRIDAY":
                        diaEnEspanol = "VIERNES";
                        break;
                    case "SATURDAY":
                        diaEnEspanol = "SABADO";
                        break;
                    case "SUNDAY":
                        diaEnEspanol = "DOMINGO";
                        break;
                    default:
                        throw new RuntimeException("Día de la semana desconocido: " + diaEnIngles);
                }

                reserva.setDia(DiaEnum.valueOf(diaEnEspanol));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error al determinar el día de la semana para la fecha proporcionada.");
            }

            // Validar cantidad de participantes
            if (reserva.getCantidadParticipantes() < 1) {
                throw new RuntimeException("La cantidad de participantes no puede ser menor que 1.");
            }

            if (reserva.getLaboratorio() != null && reserva.getLaboratorio().getIdLaboratorio() != null) {
                Laboratorio laboratorio = laboratorioRepository.findById(reserva.getLaboratorio().getIdLaboratorio())
                        .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));

                // Validar que la cantidad de participantes no sea mayor que la capacidad del laboratorio
                if (reserva.getCantidadParticipantes() > laboratorio.getCapacidad()) {
                    throw new RuntimeException("La cantidad de participantes no puede ser mayor que la capacidad del laboratorio (" + laboratorio.getCapacidad() + ").");
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

            reserva.setTipoEnum(TipoEnum.RESERVA);
            reserva.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellido());
            reserva.setCorreo(usuario.getCorreo());
            reserva.setPeriodo(periodoActivo);

            Reserva nuevaReserva = reservaRepository.save(reserva);

            if (administradorRepository.count() > 0) {
                mailService.enviarCorreoReserva(nuevaReserva, usuario);
            } else {
                log.warn("No se envió el correo de reserva porque no hay administradores registrados.");
            }

            return nuevaReserva;

        } catch (Exception e) {
            log.error("Error al crear la reserva: {}", e.getMessage());
            throw new RuntimeException("Error al crear la reserva: " + e.getMessage());
        }
    }



    // Actualizar una reserva existente
    public Reserva updateReserva(Long idReserva, Reserva updatedReserva, Usuario usuario) {
        if (!"admin".equals(usuario.getRol().getNombre()) &&
                (updatedReserva.getEstado() == Reserva.EstadoReserva.APROBADA || updatedReserva.getEstado() == Reserva.EstadoReserva.RECHAZADA)) {
            throw new RuntimeException("No tienes permiso para aprobar o rechazar reservas.");
        }

        return reservaRepository.findById(idReserva).map(reserva -> {
            // Validar que la fecha de reserva existe
            if (updatedReserva.getFechaReserva() == null) {
                throw new RuntimeException("La fecha de reserva es obligatoria.");
            }

            // Obtener el periodo activo
            Periodo periodoActivo = periodoRepository.findByEstadoTrue()
                    .orElseThrow(() -> new RuntimeException("No hay un período activo."));

            // Validar que la fecha de reserva está dentro del periodo
            if (updatedReserva.getFechaReserva().isAfter(periodoActivo.getFechaFin())) {
                throw new RuntimeException("La fecha de reserva no puede ser posterior a la fecha de fin del periodo (" +
                        periodoActivo.getFechaFin() + ")");
            }

            if (updatedReserva.getFechaReserva().isBefore(periodoActivo.getFechaInicio())) {
                throw new RuntimeException("La fecha de reserva no puede ser anterior a la fecha de inicio del periodo (" +
                        periodoActivo.getFechaInicio() + ")");
            }

            // Establecer el día automáticamente basado en la fecha de reserva
            try {
                String diaEnIngles = updatedReserva.getFechaReserva().getDayOfWeek().name();
                String diaEnEspanol;

                switch (diaEnIngles) {
                    case "MONDAY":
                        diaEnEspanol = "LUNES";
                        break;
                    case "TUESDAY":
                        diaEnEspanol = "MARTES";
                        break;
                    case "WEDNESDAY":
                        diaEnEspanol = "MIERCOLES";
                        break;
                    case "THURSDAY":
                        diaEnEspanol = "JUEVES";
                        break;
                    case "FRIDAY":
                        diaEnEspanol = "VIERNES";
                        break;
                    case "SATURDAY":
                        diaEnEspanol = "SABADO";
                        break;
                    case "SUNDAY":
                        diaEnEspanol = "DOMINGO";
                        break;
                    default:
                        throw new RuntimeException("Día de la semana desconocido: " + diaEnIngles);
                }

                updatedReserva.setDia(DiaEnum.valueOf(diaEnEspanol));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error al determinar el día de la semana para la fecha proporcionada.");
            }

            // Validar cantidad de participantes
            if (updatedReserva.getCantidadParticipantes() < 1) {
                throw new RuntimeException("La cantidad de participantes no puede ser menor que 1.");
            }

            if (updatedReserva.getLaboratorio() != null && updatedReserva.getLaboratorio().getIdLaboratorio() != null) {
                Laboratorio laboratorio = laboratorioRepository.findById(updatedReserva.getLaboratorio().getIdLaboratorio())
                        .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));

                // Validar que la cantidad de participantes no sea mayor que la capacidad del laboratorio
                if (updatedReserva.getCantidadParticipantes() > laboratorio.getCapacidad()) {
                    throw new RuntimeException("La cantidad de participantes no puede ser mayor que la capacidad del laboratorio (" + laboratorio.getCapacidad() + ").");
                }
                updatedReserva.setLaboratorio(laboratorio);
            }

            long diffHours = ChronoUnit.HOURS.between(updatedReserva.getHoraInicio(), updatedReserva.getHoraFin());
            if (diffHours != 1) {
                throw new RuntimeException("La reserva debe durar exactamente 1 hora.");
            }

            if ("docente".equals(usuario.getRol().getNombre())) {
                updatedReserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
            }

            updatedReserva.setTipoEnum(TipoEnum.RESERVA);
            updatedReserva.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellido());
            updatedReserva.setCorreo(usuario.getCorreo());
            updatedReserva.setPeriodo(periodoActivo);

            Reserva reservaActualizada = reservaRepository.save(updatedReserva);

            if (administradorRepository.count() > 0) {
                mailService.enviarCorreoReserva(reservaActualizada, usuario);
            } else {
                log.warn("No se envió el correo de reserva porque no hay administradores registrados.");
            }

            return reservaActualizada;

        }).orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));
    }


    // Eliminar una reserva por ID
    public void deleteReserva(Long idReserva, Usuario usuario) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));

        // Validar si el usuario es admin o el dueño de la reserva
        boolean esAdmin = "admin".equals(usuario.getRol().getNombre());
        boolean esDueño = reserva.getCorreo().equals(usuario.getCorreo());

        if (!esAdmin && !esDueño) {
            throw new RuntimeException("No tienes permiso para eliminar esta reserva.");
        }

        // Solo los administradores pueden eliminar reservas aprobadas
        if (!esAdmin && reserva.getEstado() == Reserva.EstadoReserva.APROBADA) {
            throw new RuntimeException("No se puede eliminar una reserva que ya ha sido aprobada.");
        }

        // Validar que la reserva pertenezca al periodo activo
        Periodo periodoActivo = periodoRepository.findByEstadoTrue()
                .orElseThrow(() -> new RuntimeException("No hay un período activo."));

        if (!reserva.getPeriodo().getIdPeriodo().equals(periodoActivo.getIdPeriodo())) {
            throw new RuntimeException("Solo se pueden eliminar reservas del periodo activo.");
        }

        reservaRepository.deleteById(idReserva);
    }
}
