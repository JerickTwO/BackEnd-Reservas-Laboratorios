package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.UsuariosRepository;
import com.masache.masachetesis.security.jwt.JwtProvider;
import com.masache.masachetesis.service.MailService;
import com.masache.masachetesis.service.ReservaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("api/v1/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsuariosRepository usuariosRepository;

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
    public ResponseEntity<Reserva> createReserva(@Valid @RequestBody Reserva reserva, @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtProvider.getNombreUsuarioFromToken(jwt);
        Usuario usuario = usuariosRepository.findUsuarioByUsuarioAndEstadoTrue(username);
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }

       log.info("Usuario autenticado: " + usuario.getUsuario());
        Reserva nuevaReserva = reservaService.createReserva(reserva, usuario);

        return ResponseEntity.ok(nuevaReserva);
    }

    // Actualizar una reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(@Valid @PathVariable Long id, @RequestBody Reserva updatedReserva) {
        try {
            return ResponseEntity.ok(reservaService.updateReserva(id, updatedReserva));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cambiar el estado de una reserva
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Reserva> updateEstadoReserva( Long id,
                                                       @RequestBody Reserva.EstadoReserva estado,
                                                       @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtProvider.getNombreUsuarioFromToken(jwt);
        Usuario usuario = usuariosRepository.findUsuarioByUsuarioAndEstadoTrue(username);
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Reserva reserva = reservaService.getReservaById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
            reserva.setEstado(estado);
            return ResponseEntity.ok(reservaService.createReserva(reserva, usuario));
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
