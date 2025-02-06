package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Carrera;
import com.masache.masachetesis.models.Departamento;
import com.masache.masachetesis.models.Estudiante;
import com.masache.masachetesis.service.CarreraService;
import com.masache.masachetesis.service.DepartamentoService;
import com.masache.masachetesis.service.DocenteService;
import com.masache.masachetesis.service.EstudianteService;
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
@RequestMapping("api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final CarreraService carreraService;

    // Obtener todos los docentes con su departamento
    @GetMapping
    public ResponseEntity<List<Estudiante>> getAllDocentes() {
        List<Estudiante> docentes = estudianteService.getAllDocentesWithDepartamento();
        return new ResponseEntity<>(docentes, HttpStatus.OK);
    }

    // Obtener un estudiante por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> getDocenteById(@PathVariable("id") Long id) {
        Optional<Estudiante> estudiante = estudianteService.getEstudianteById(id);
        return estudiante.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo estudiante
    @PostMapping
    public ResponseEntity<Estudiante> createDocente(@Validated @RequestBody Estudiante estudiante) {
        try {
            // Validar y obtener el departamento asociado, si existe
            if (estudiante.getCarrera() != null && estudiante.getCarrera().getIdCarrera() != null) {
                Carrera carrera = carreraService
                        .getCarreraById(estudiante.getCarrera().getIdCarrera())
                        .orElseThrow(() -> new RuntimeException("Departamento no encontrado."));
                estudiante.setCarrera(carrera);
            }

            // Guardar el estudiante
            Estudiante savedDocente = estudianteService.saveOrUpdateEstudiante(estudiante);
            return new ResponseEntity<>(savedDocente, HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejar errores y devolver respuesta adecuada
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocente(@PathVariable("id") Long id) {
        Optional<Estudiante> estudiante = estudianteService.getEstudianteById(id);
        if (estudiante.isPresent()) {
            estudianteService.deleteDocente(id); // Llamar al servicio para eliminar el estudiante
            return ResponseEntity.noContent().build(); // Retornar 204 No Content si se elimina correctamente
        } else {
            return ResponseEntity.notFound().build(); // Retornar 404 Not Found si el estudiante no existe
        }
    }

    // Actualizar un estudiante existente

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDocente(@PathVariable("id") Long id, @Validated @RequestBody Estudiante estudiante) {
        Optional<Estudiante> existingEstudiante = estudianteService.getEstudianteById(id);
        if (existingEstudiante.isPresent()) {
            try {
                if (estudiante.getCarrera() != null && estudiante.getCarrera().getIdCarrera() != null) {
                    Carrera carrera = validarCarrera(estudiante.getCarrera().getIdCarrera());
                    estudiante.setCarrera(carrera);
                }
                estudiante.setIdEstudiante(id);
                Estudiante updatedDocente = estudianteService.saveOrUpdateEstudiante(estudiante);
                return new ResponseEntity<>(updatedDocente, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Método privado para validar y obtener un departamento
    private Carrera validarCarrera(Long idCarrera) {
        return carreraService.getCarreraById(idCarrera)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado para el ID " + idCarrera));
    }
}
