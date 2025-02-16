package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.DiaEnum;
import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;

    /**
     * Obtener todos los horarios.
     */
    @GetMapping
    public ResponseEntity<List<Horario>> obtenerTodos() {
        List<Horario> horarios = horarioService.obtenerTodos();
        return ResponseEntity.ok(horarios);
    }

    /**
     * Obtener un horario por ID.
     */
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
