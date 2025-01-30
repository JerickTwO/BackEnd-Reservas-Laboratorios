package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Materia;
import com.masache.masachetesis.service.MateriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/materias")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    /**
     * Obtener todas las materias.
     * @return Lista de materias.
     */
    @GetMapping
    public ResponseEntity<List<Materia>> getAllMaterias() {
        List<Materia> materias = materiaService.getAllMaterias();
        return new ResponseEntity<>(materias, HttpStatus.OK);
    }

    /**
     * Obtener una materia por su ID.
     * @param id ID de la materia.
     * @return Materia correspondiente al ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Materia> getMateriaById(@PathVariable("id") Long id) {
        Optional<Materia> materia = materiaService.getMateriaById(id);
        return materia.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crear una nueva materia.
     * @param materia Objeto Materia recibido en la solicitud.
     * @return Materia creada.
     */
    @PostMapping
    public ResponseEntity<Materia> createMateria(@Validated @RequestBody Materia materia) {
        try {
            Materia savedMateria = materiaService.saveOrUpdateMateria(materia);
            return new ResponseEntity<>(savedMateria, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Actualizar una materia existente.
     * @param id ID de la materia a actualizar.
     * @param materia Objeto Materia recibido en la solicitud.
     * @return Materia actualizada o un error si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Materia> updateMateria(
            @PathVariable("id") Long id,
            @Validated @RequestBody Materia materia
    ) {
        Optional<Materia> existingMateria = materiaService.getMateriaById(id);
        if (existingMateria.isPresent()) {
            materia.setIdMateria(id); // Asegurarse de que el ID sea el correcto
            Materia updatedMateria = materiaService.saveOrUpdateMateria(materia);
            return new ResponseEntity<>(updatedMateria, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar una materia por su ID.
     * @param id ID de la materia a eliminar.
     * @return Respuesta de estado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateria(@PathVariable("id") Long id) {
        Optional<Materia> materia = materiaService.getMateriaById(id);
        if (materia.isPresent()) {
            materiaService.deleteMateria(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
