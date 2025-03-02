package com.masache.masachetesis.controller;

import com.masache.masachetesis.dto.HorarioReservasDto;
import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.service.HorarioClaseReservaServiceImpl;
import com.masache.masachetesis.service.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;
    private final HorarioClaseReservaServiceImpl horarioClaseReservaServiceImpl;

    @GetMapping
    public ResponseEntity<JsonResponseDto> obtenerTodos() {
        JsonResponseDto response = horarioService.obtenerTodos();
        return ResponseEntity.status(response.getCodigoHttp()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonResponseDto> obtenerPorId(@PathVariable Long id) {
        JsonResponseDto response = horarioService.obtenerPorId(id);
        return ResponseEntity.status(response.getCodigoHttp()).body(response);
    }

    @PostMapping
    public ResponseEntity<JsonResponseDto> crearHorario(@RequestBody Horario horario) {
        JsonResponseDto response = horarioService.saveHorario(horario);
        return ResponseEntity.status(response.getCodigoHttp()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonResponseDto> actualizarHorario(@PathVariable Long id, @RequestBody Horario horario) {
        JsonResponseDto response = horarioService.updatedHorario(id, horario);
        return ResponseEntity.status(response.getCodigoHttp()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponseDto> eliminarHorario(@PathVariable Long id) {
        JsonResponseDto response = horarioService.eliminar(id);
        return ResponseEntity.status(response.getCodigoHttp()).body(response);
    }

    @GetMapping("/clases-reservas")
    public ResponseEntity<List<HorarioReservasDto>> getHorarioClaseReserva() {
        List<HorarioReservasDto> horarios = horarioClaseReservaServiceImpl.getHorarioClaseReserva();
        return ResponseEntity.ok(horarios);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
