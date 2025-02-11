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

    /**
     * Obtener horarios por laboratorio y día.
     */
    @GetMapping("/laboratorio/{idLaboratorio}/{dia}")
    public ResponseEntity<List<Horario>> obtenerPorLaboratorioYDia(
            @PathVariable Long idLaboratorio,
            @PathVariable String dia) {

        try {
            // Convertir el String a Enum
            DiaEnum diaEnum = DiaEnum.valueOf(dia.toUpperCase());

            // Obtener horarios filtrados por laboratorio y día
            List<Horario> horarios = horarioService.obtenerPorLaboratorioYDia(idLaboratorio, diaEnum);
            return ResponseEntity.ok(horarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Devuelve error si el día no es válido
        }
    }



    /**
     * Crear un nuevo horario.
     */
    @PostMapping
    public ResponseEntity<String> crearHorario(@RequestBody Horario horario) {
        try {
            horarioService.guardar(horario);
            return ResponseEntity.ok("Horario registrado exitosamente."); // Estado 200 si es correcto
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Estado 400 si hay error
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarHorario(@PathVariable Long id, @RequestBody Horario horario) {
        try {
            horarioService.actualizar(id, horario);
            return ResponseEntity.ok("Horario actualizado correctamente."); // Estado 200 si es correcto
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Estado 400 si hay error
        }
    }


    /**
     * Eliminar un horario por ID.
     */
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
