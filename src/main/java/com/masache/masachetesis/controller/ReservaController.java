package com.masache.masachetesis.controller;

import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.UsuariosRepository;
import com.masache.masachetesis.security.jwt.JwtProvider;
import com.masache.masachetesis.service.ReservaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/periodo-activo")
    public ResponseEntity<List<Reserva>> getReservasByPeriodoActivo() {
        List<Reserva> reservas = reservaService.getReservasByPeriodoActivo();
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity<JsonResponseDto> createReserva(@Valid @RequestBody Reserva reserva, @RequestHeader("Authorization") String token) {
        log.info("Entro a crear reserva");
        String jwt = token.replace("Bearer ", "");
        String username = jwtProvider.getNombreUsuarioFromToken(jwt);
        Usuario usuario = usuariosRepository.findUsuarioByUsuarioAndEstadoTrue(username);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Usuario no encontrado", null, null));
        }
        try {
            log.info("Usuario autenticado: " + usuario.getUsuario());
            Reserva nuevaReserva = reservaService.createReserva(reserva, usuario);
            log.info("Reserva creada: " + nuevaReserva);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new JsonResponseDto(true, HttpStatus.CREATED.value(), "Reserva creada exitosamente", nuevaReserva, null));
        } catch (RuntimeException e) {
            log.error("Error al crear la reserva: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Cambio aquí para devolver 400 en errores de validación
                    .body(new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Error al crear la reserva", null, e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<JsonResponseDto> updateReserva(@Valid @PathVariable Long id, @RequestBody Reserva updatedReserva, @RequestHeader("Authorization") String token) {
        log.info("Entro a actualizar reserva");

        String jwt = token.replace("Bearer ", "");
        String username = jwtProvider.getNombreUsuarioFromToken(jwt);
        Usuario usuario = usuariosRepository.findUsuarioByUsuarioAndEstadoTrue(username);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Usuario no encontrado", null, null));
        }

        try {
            log.info("Usuario autenticado: " + usuario.getUsuario());
            Reserva reservaActualizada = reservaService.updateReserva(id, updatedReserva, usuario);
            log.info("Reserva actualizada: " + reservaActualizada);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new JsonResponseDto(true, HttpStatus.OK.value(), "Reserva actualizada exitosamente", reservaActualizada, null));
        } catch (RuntimeException e) {
            log.error("Error al actualizar la reserva: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Error al actualizar la reserva", null, e.getMessage()));
        }
    }


    // Cambiar el estado de una reserva
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Reserva> updateEstadoReserva(Long id,@RequestBody Reserva.EstadoReserva estado, @RequestHeader("Authorization") String token) {
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
    public ResponseEntity<JsonResponseDto> deleteReserva(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtProvider.getNombreUsuarioFromToken(jwt);
        Usuario usuario = usuariosRepository.findUsuarioByUsuarioAndEstadoTrue(username);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Usuario no encontrado", null, null));
        }
        try {
            reservaService.deleteReserva(id, usuario);
            return ResponseEntity.ok()
                    .body(new JsonResponseDto(true, HttpStatus.OK.value(), "Reserva eliminada exitosamente", null, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "Error al eliminar la reserva", null, e.getMessage()));
        }
    }
}
