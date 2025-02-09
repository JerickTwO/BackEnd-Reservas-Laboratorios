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
@RequestMapping("/api/horarios")
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
     * Obtener horarios por docente.
     */
    @GetMapping("/docente/{idDocente}")
    public ResponseEntity<List<Horario>> obtenerPorDocente(@PathVariable Long idDocente) {
        List<Horario> horarios = horarioService.obtenerPorDocente(idDocente);
        return ResponseEntity.ok(horarios);
    }

    /**
     * Obtener horarios por materia.
     */
    @GetMapping("/materia/{idMateria}")
    public ResponseEntity<List<Horario>> obtenerPorMateria(@PathVariable Long idMateria) {
        List<Horario> horarios = horarioService.obtenerPorMateria(idMateria);
        return ResponseEntity.ok(horarios);
    }

    /**
     * Crear un nuevo horario.
     */
    @PostMapping
    public ResponseEntity<String> crearHorario(@RequestBody Horario horario) {
        try {
            horarioService.guardar(horario);
            return ResponseEntity.ok("Horario registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualizar un horario existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarHorario(@PathVariable Long id, @RequestBody Horario horarioActualizado) {
        try {
            horarioService.actualizar(id, horarioActualizado);
            return ResponseEntity.ok("Horario actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
