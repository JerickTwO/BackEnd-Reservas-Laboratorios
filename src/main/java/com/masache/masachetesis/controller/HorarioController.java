package com.masache.masachetesis.controller;

import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.service.HorarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("api/v1/horarios")
public class HorarioController {
    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public List<Horario> obtenerTodos() {
        return horarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horario> obtenerPorId(@PathVariable Long id) {
        return horarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Horario guardar(@RequestBody Horario horario) {
        return horarioService.guardar(horario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizar(@PathVariable Long id, @RequestBody Horario horario) {
        if (!horarioService.obtenerPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        horario.setId(id);
        return ResponseEntity.ok(horarioService.guardar(horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!horarioService.obtenerPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}