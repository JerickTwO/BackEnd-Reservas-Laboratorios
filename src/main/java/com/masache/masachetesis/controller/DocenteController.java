package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Departamento;
import com.masache.masachetesis.models.Docente;
import com.masache.masachetesis.service.DepartamentoService;
import com.masache.masachetesis.service.DocenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/docentes")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private DepartamentoService departamentoService;

    // Obtener todos los docentes con su departamento
    @GetMapping
    public ResponseEntity<List<Docente>> getAllDocentes() {
        List<Docente> docentes = docenteService.getAllDocentesWithDepartamento();
        return new ResponseEntity<>(docentes, HttpStatus.OK);
    }

    // Obtener un docente por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Docente> getDocenteById(@PathVariable("id") Long id) {
        Optional<Docente> docente = docenteService.getDocenteById(id);
        return docente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo docente
    @PostMapping
    public ResponseEntity<Docente> createDocente(@Validated @RequestBody Docente docente) {
        try {
            // Validar y obtener el departamento asociado, si existe
            if (docente.getDepartamento() != null && docente.getDepartamento().getIdDepartamento() != null) {
                Departamento departamento = departamentoService
                        .getDepartamentoById(docente.getDepartamento().getIdDepartamento())
                        .orElseThrow(() -> new RuntimeException("Departamento no encontrado."));
                docente.setDepartamento(departamento);
            }

            // Guardar el docente
            Docente savedDocente = docenteService.saveOrUpdateDocente(docente);
            return new ResponseEntity<>(savedDocente, HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejar errores y devolver respuesta adecuada
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocente(@PathVariable("id") Long id) {
        Optional<Docente> docente = docenteService.getDocenteById(id);
        if (docente.isPresent()) {
            docenteService.deleteDocente(id); // Llamar al servicio para eliminar el docente
            return ResponseEntity.noContent().build(); // Retornar 204 No Content si se elimina correctamente
        } else {
            return ResponseEntity.notFound().build(); // Retornar 404 Not Found si el docente no existe
        }
    }

    // Actualizar un docente existente

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDocente(@PathVariable("id") Long id, @Validated @RequestBody Docente docente) {
        Optional<Docente> existingDocente = docenteService.getDocenteById(id);
        if (existingDocente.isPresent()) {
            try {
                if (docente.getDepartamento() != null && docente.getDepartamento().getIdDepartamento() != null) {
                    Departamento departamento = validarDepartamento(docente.getDepartamento().getIdDepartamento());
                    docente.setDepartamento(departamento);
                }
                docente.setIdDocente(id);
                Docente updatedDocente = docenteService.saveOrUpdateDocente(docente);
                return new ResponseEntity<>(updatedDocente, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Eliminar un docente por su ID

    // Método privado para validar y obtener un departamento
    private Departamento validarDepartamento(Long idDepartamento) {
        return departamentoService.getDepartamentoById(idDepartamento)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado para el ID " + idDepartamento));
    }
}
