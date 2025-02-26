package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Clase;
import com.masache.masachetesis.service.ClaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clases")
@CrossOrigin(origins = "*") // Permite llamadas desde cualquier origen
public class ClaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClaseController.class);
    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    /**
     * Obtener todas las clases.
     */
    @GetMapping
    public ResponseEntity<List<Clase>> obtenerTodas() {
        List<Clase> clases = claseService.obtenerTodas();
        return ResponseEntity.ok(clases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clase> obtenerPorId(@PathVariable Long id) {
        Optional<Clase> clase = claseService.obtenerPorId(id);
        return clase.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/periodo-activo")
    public ResponseEntity<List<Clase>> getReservasByPeriodoActual() {
        List<Clase> clases = claseService.getClasesByPeriodoActivo();
        return ResponseEntity.ok(clases);
    }
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Clase clase) {
        try {
            logger.info("Intentando guardar nueva clase: Materia ID {}, Docente ID {}, Periodo ID {}",
                    clase.getMateria().getIdMateria(),
                    clase.getDocente().getIdDocente(),
                    clase.getPeriodo().getIdPeriodo());

            Clase nuevaClase = claseService.guardar(clase);
            return ResponseEntity.ok(nuevaClase);
        } catch (Exception e) {
            logger.error("Error al guardar la clase: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error al guardar la clase: " + e.getMessage());
        }
    }

    /**
     * Actualizar una clase existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Clase clase) {
        try {
            logger.info("Intentando actualizar clase ID: {}", id);
            Clase claseActualizada = claseService.actualizar(id, clase);
            return ResponseEntity.ok(claseActualizada);
        } catch (Exception e) {
            logger.error("Error al actualizar la clase: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error al actualizar la clase: " + e.getMessage());
        }
    }

    /**
     * Eliminar una clase por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            logger.info("Intentando eliminar clase ID: {}", id);
            claseService.eliminar(id);
            return ResponseEntity.ok("Clase eliminada correctamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar la clase: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error al eliminar la clase: " + e.getMessage());
        }
    }
}
