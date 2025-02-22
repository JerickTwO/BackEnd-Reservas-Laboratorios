package com.masache.masachetesis.controller;

import com.masache.masachetesis.dto.HorarioReservasDto;
import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.service.HorarioClaseReservaServiceImpl;
import com.masache.masachetesis.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;
    private final HorarioClaseReservaServiceImpl horarioClaseReservaService;

    @GetMapping("/clases-reservas")
    public ResponseEntity<List<HorarioReservasDto>> getHorarioClaseReserva() {
        List<HorarioReservasDto> horarios = horarioClaseReservaService.getHorarioClaseReserva();
        return ResponseEntity.ok(horarios);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @GetMapping
    public ResponseEntity<List<Horario>> obtenerTodos() {
        List<Horario> horarios = horarioService.obtenerTodos();
        return ResponseEntity.ok(horarios);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Horario> obtenerPorId(@PathVariable Long id) {
        Optional<Horario> horario = horarioService.obtenerPorId(id);
        return horario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/aprobadas")
    public ResponseEntity<List<Horario>> obtenerHorariosConReservaAprobada() {
        List<Horario> horarios = horarioService.obtenerHorariosConReservaAprobada();
        return ResponseEntity.ok(horarios);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHorario(@PathVariable Long id) {
        try {
            horarioService.eliminar(id);
            return ResponseEntity.ok("Horario eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el horario.");
        }
    }
}
