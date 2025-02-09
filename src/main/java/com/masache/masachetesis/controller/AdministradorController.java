package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Administrador;
import com.masache.masachetesis.models.Departamento;
import com.masache.masachetesis.models.Administrador;
import com.masache.masachetesis.service.AdministradorService;
import com.masache.masachetesis.service.DepartamentoService;
import com.masache.masachetesis.service.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    // Obtener todos los docentes con su departamento
    @GetMapping
    public ResponseEntity<List<Administrador>> getAllAdministradors() {
        List<Administrador> administradores = administradorService.getAllAdministradores();
        return new ResponseEntity<>(administradores, HttpStatus.OK);
    }

    // Obtener un docente por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Administrador> getAdministradorById(@PathVariable("id") Long id) {
        Optional<Administrador> docente = administradorService.getAdministradorById(id);
        return docente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo docente
    @PostMapping
    public ResponseEntity<Administrador> createAdministrador(@Validated @RequestBody Administrador docente) {
        try {
            Administrador savedAdministrador = administradorService.saveOrUpdateAdministrador(docente);
            return new ResponseEntity<>(savedAdministrador, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrador(@PathVariable("id") Long id) {
        Optional<Administrador> docente = administradorService.getAdministradorById(id);
        if (docente.isPresent()) {
            administradorService.deleteAdministrador(id); // Llamar al servicio para eliminar el docente
            return ResponseEntity.noContent().build(); // Retornar 204 No Content si se elimina correctamente
        } else {
            return ResponseEntity.notFound().build(); // Retornar 404 Not Found si el docente no existe
        }
    }

    // Actualizar un docente existente

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAdministrador(@PathVariable("id") Long id, @Validated @RequestBody Administrador docente) {
        Optional<Administrador> existingAdministrador = administradorService.getAdministradorById(id);
        if (existingAdministrador.isPresent()) {
            try {

                docente.setIdAdministrador(id);
                Administrador updatedAdministrador = administradorService.saveOrUpdateAdministrador(docente);
                return new ResponseEntity<>(updatedAdministrador, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
