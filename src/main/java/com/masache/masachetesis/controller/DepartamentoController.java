package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Departamento;
import com.masache.masachetesis.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    // Obtener todos los departamentos
    @GetMapping
    public ResponseEntity<List<Departamento>> getAllDepartamentos() {
        List<Departamento> departamentos = departamentoService.getAllDepartamentos();
        return new ResponseEntity<>(departamentos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Departamento> createDepartamento(@Validated @RequestBody Departamento departamento) {
        Departamento savedDepartamento = departamentoService.saveOrUpdateDepartamento(departamento);
        return new ResponseEntity<>(savedDepartamento, HttpStatus.CREATED);
    }
    // Obtener un departamento por su ID

    @GetMapping("/{id}")
    public ResponseEntity<Departamento> getDepartamentoById(@PathVariable("id") Long id) {
        Optional<Departamento> departamento = departamentoService.getDepartamentoById(id);
        return departamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Crear un nuevo departamento

    // Actualizar un departamento existente
    @PutMapping("/{id}")
    public ResponseEntity<Departamento> updateDepartamento(@PathVariable("id") Long id, @Validated @RequestBody Departamento departamento) {
        Optional<Departamento> existingDepartamento = departamentoService.getDepartamentoById(id);
        if (existingDepartamento.isPresent()) {
            departamento.setIdDepartamento(id);
            Departamento updatedDepartamento = departamentoService.saveOrUpdateDepartamento(departamento);
            return new ResponseEntity<>(updatedDepartamento, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un departamento por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartamento(@PathVariable("id") Long id) {
        Optional<Departamento> departamento = departamentoService.getDepartamentoById(id);
        if (departamento.isPresent()) {
            departamentoService.deleteDepartamento(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
