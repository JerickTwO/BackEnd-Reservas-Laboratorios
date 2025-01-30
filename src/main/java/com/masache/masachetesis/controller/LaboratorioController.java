package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Laboratorio;
import com.masache.masachetesis.service.LaboratorioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/laboratorios")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    // Constructor para inyección de dependencias
    public LaboratorioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    /**
     * Obtener todos los laboratorios.
     */
    @GetMapping
    public ResponseEntity<List<Laboratorio>> getAllLaboratorios() {
        List<Laboratorio> laboratorios = laboratorioService.getAllLaboratorios();
        return ResponseEntity.ok(laboratorios);
    }

    /**
     * Obtener un laboratorio por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Laboratorio> getLaboratorioById(@PathVariable Long id) {
        Optional<Laboratorio> laboratorio = laboratorioService.getLaboratorioById(id);
        return laboratorio.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Crear un nuevo laboratorio.
     */
    @PostMapping
    public ResponseEntity<Laboratorio> createLaboratorio(@RequestBody Laboratorio laboratorio) {
        try {
            Laboratorio nuevoLaboratorio = laboratorioService.saveOrUpdateLaboratorio(laboratorio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLaboratorio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Actualizar un laboratorio existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Laboratorio> updateLaboratorio(@PathVariable Long id, @RequestBody Laboratorio laboratorio) {
        try {
            laboratorio.setIdLaboratorio(id);
            Laboratorio laboratorioActualizado = laboratorioService.saveOrUpdateLaboratorio(laboratorio);
            return ResponseEntity.ok(laboratorioActualizado);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Eliminar un laboratorio por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaboratorio(@PathVariable Long id) {
        try {
            laboratorioService.deleteLaboratorio(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Buscar laboratorios por nombre.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Laboratorio>> findLaboratoriosByNombre(@RequestParam String nombre) {
        try {
            List<Laboratorio> laboratorios = laboratorioService.findLaboratoriosByNombre(nombre);
            return ResponseEntity.ok(laboratorios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
