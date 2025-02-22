package com.masache.masachetesis.controller;

import com.masache.masachetesis.dto.HorarioReservasDto;
import com.masache.masachetesis.service.HorarioClaseReservaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1 /horarios")
@RequiredArgsConstructor
public class HorarioClaseReservaController {

    private final HorarioClaseReservaServiceImpl horarioClaseReservaService;

    @GetMapping("/clases-reservas")
    public ResponseEntity<List<HorarioReservasDto>> getHorarioClaseReserva() {
        List<HorarioReservasDto> horarios = horarioClaseReservaService.getHorarioClaseReserva();
        return ResponseEntity.ok(horarios);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}