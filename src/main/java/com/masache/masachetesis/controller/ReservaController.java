package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.service.ReservaService;
import com.masache.masachetesis.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private EmailService emailService; // Servicio de correo electrónico

    // Obtener todas las reservas
    @GetMapping
    public List<Reserva> getAllReservas() {
        return reservaService.getAllReservas();
    }

    // Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long id) {
        return reservaService.getReservaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear una nueva reserva
    @PostMapping
    public ResponseEntity<Reserva> createReserva(@RequestBody Reserva reserva) {
        Reserva nuevaReserva = reservaService.createReserva(reserva);

        // Enviar correo al administrador
        String correoAdmin = "fjmasache@espe.edu.ec";
        String asunto = "Nueva Reserva Creada";
        String mensaje = String.format(
                "Una nueva reserva ha sido registrada:\n\n" +
                        "Nombre: %s\nCorreo: %s\nTeléfono: %s\nLaboratorio: %s\nHora Inicio: %s\nHora Fin: %s\nMotivo: %s",
                nuevaReserva.getNombreCompleto(),
                nuevaReserva.getCorreo(),
                nuevaReserva.getTelefono(),
                nuevaReserva.getLaboratorio().getNombreLaboratorio(),
                nuevaReserva.getHoraInicio(),
                nuevaReserva.getHoraFin(),
                nuevaReserva.getMotivoReserva()
        );

        emailService.enviarCorreo(correoAdmin, asunto, mensaje);

        return ResponseEntity.ok(nuevaReserva);
    }

    // Actualizar una reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(@PathVariable Long id, @RequestBody Reserva updatedReserva) {
        try {
            return ResponseEntity.ok(reservaService.updateReserva(id, updatedReserva));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cambiar el estado de una reserva
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Reserva> updateEstadoReserva(@PathVariable Long id, @RequestBody Reserva.EstadoReserva estado) {
        try {
            Reserva reserva = reservaService.getReservaById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
            reserva.setEstado(estado);
            return ResponseEntity.ok(reservaService.createReserva(reserva));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una reserva por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        try {
            reservaService.deleteReserva(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
