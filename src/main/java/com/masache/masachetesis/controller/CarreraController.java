package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Carrera;
import com.masache.masachetesis.service.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/carreras")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    /**
     * Obtener todas las carreras.
     * @return Lista de carreras.
     */
    @GetMapping
    public ResponseEntity<List<Carrera>> getAllCarreras() {
        List<Carrera> carreras = carreraService.getAllCarreras();
        return new ResponseEntity<>(carreras, HttpStatus.OK);
    }

    /**
     * Obtener una carrera por ID.
     * @param id ID de la carrera.
     * @return Carrera si existe, 404 si no.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Carrera> getCarreraById(@PathVariable Long id) {
        try {
            Optional<Carrera> carrera = carreraService.getCarreraById(id);
            return carrera.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // ID inválido
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Otro error
        }
    }

    /**
     * Crear una nueva carrera.
     * @param carrera Objeto Carrera con datos.
     * @return Carrera creada.
     */
    @PostMapping
    public ResponseEntity<Carrera> createCarrera(@Validated @RequestBody Carrera carrera) {
        try {
            Carrera savedCarrera = carreraService.saveOrUpdateCarrera(carrera);
            return new ResponseEntity<>(savedCarrera, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Validaciones fallidas
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Otro error
        }
    }

    /**
     * Actualizar una carrera existente.
     * @param id ID de la carrera a actualizar.
     * @param carrera Datos actualizados.
     * @return Carrera actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Carrera> updateCarrera(@PathVariable Long id, @Validated @RequestBody Carrera carrera) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Validación del ID
        }

        carrera.setIdCarrera(id); // Establecer el ID

        try {
            Carrera updatedCarrera = carreraService.saveOrUpdateCarrera(carrera);
            return new ResponseEntity<>(updatedCarrera, HttpStatus.OK);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Si no existe la carrera
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Datos inválidos
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Otro error
        }
    }

    /**
     * Eliminar una carrera por ID.
     * @param id ID de la carrera a eliminar.
     * @return 204 No Content si fue exitosa, 404 o 500 si falla.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrera(@PathVariable Long id) {
        try {
            carreraService.deleteCarrera(id);
            return ResponseEntity.noContent().build(); // Eliminación exitosa
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Carrera no encontrada
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Otro error
        }
    }
}
