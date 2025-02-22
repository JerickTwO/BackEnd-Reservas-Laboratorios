package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.service.PeriodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/periodos")
public class PeriodoController {

    private final PeriodoService periodoService;

    public PeriodoController(PeriodoService periodoService) {
        this.periodoService = periodoService;
    }

    /**
     * Obtener todos los periodos.
     */
    @GetMapping
    public ResponseEntity<List<Periodo>> obtenerTodos() {
        return ResponseEntity.ok(periodoService.obtenerTodos());
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<Periodo> cambiarEstado(@PathVariable Long id, @RequestParam boolean estado) {
        try {
            Periodo periodoActualizado = periodoService.cambiarEstado(id, estado);
            return ResponseEntity.ok(periodoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * Obtener un periodo por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Periodo> obtenerPorId(@PathVariable Long id) {
        Optional<Periodo> periodo = periodoService.obtenerPorId(id);
        return periodo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Guardar un nuevo periodo.
     */
    @PostMapping
    public ResponseEntity<Periodo> guardar(@RequestBody Periodo periodo) {
        try {
            return ResponseEntity.ok(periodoService.guardar(periodo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Actualizar un periodo existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Periodo> actualizar(@PathVariable Long id, @RequestBody Periodo periodoActualizado) {
        try {
            return ResponseEntity.ok(periodoService.actualizar(id, periodoActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Eliminar un periodo por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            periodoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
