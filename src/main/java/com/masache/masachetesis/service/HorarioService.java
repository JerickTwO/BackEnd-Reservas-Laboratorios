package com.masache.masachetesis.service;

import com.masache.masachetesis.dto.JsonResponseDto;
import com.masache.masachetesis.models.Horario;
import com.masache.masachetesis.repositories.HorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HorarioService {
    private final HorarioRepository horarioRepository;

    public JsonResponseDto obtenerTodos() {
        List<Horario> horarios = horarioRepository.findAll();
        if (horarios.isEmpty()) {
            return new JsonResponseDto(true, HttpStatus.OK.value(), "No hay horarios registrados", horarios,null);
        }
        return new JsonResponseDto(true, HttpStatus.OK.value(), "Lista de horarios obtenida exitosamente", horarios,null);
    }

    public JsonResponseDto obtenerPorId(Long id) {
        Optional<Horario> horario = horarioRepository.findById(id);
        return horario.map(value -> new JsonResponseDto(true, HttpStatus.OK.value(), "Horario encontrado",null,null)).orElseGet(() -> new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "Horario no encontrado con el ID " + id, null,null));
    }

    public JsonResponseDto saveHorario(Horario horario) {
        if (horario.getFranjasHorario() == null || horario.getFranjasHorario().isEmpty()) {
            horario.setFranjasHorario(List.of("07:00-08:00", "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00"));
        }
        if (horario.getDiasHorario() == null || horario.getDiasHorario().isEmpty()) {
            horario.setDiasHorario(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"));
        }

        try {
            Horario nuevoHorario = horarioRepository.save(horario);
            return new JsonResponseDto(true, HttpStatus.CREATED.value(), "Horario guardado exitosamente", nuevoHorario,null);
        } catch (Exception e) {
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al guardar el horario: " + e.getMessage(), null,null);
        }
    }

    public JsonResponseDto updatedHorario(Long id, Horario horarioActualizado) {
        Optional<Horario> horarioExistenteOpt = horarioRepository.findById(id);

        if (horarioExistenteOpt.isEmpty()) {
            return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "El horario con el ID proporcionado no existe", null, null);
        }

        Horario horarioExistente = horarioExistenteOpt.get();

        boolean actualizado = false;

        if (horarioActualizado.getFranjasHorario() != null && !horarioActualizado.getFranjasHorario().isEmpty() && !horarioActualizado.getFranjasHorario().equals(horarioExistente.getFranjasHorario())) {
            horarioExistente.setFranjasHorario(horarioActualizado.getFranjasHorario());
            actualizado = true;
        }

        if (horarioActualizado.getDiasHorario() != null && !horarioActualizado.getDiasHorario().isEmpty() && !horarioActualizado.getDiasHorario().equals(horarioExistente.getDiasHorario())) {
            horarioExistente.setDiasHorario(horarioActualizado.getDiasHorario());
            actualizado = true;
        }

        if (!actualizado) {
            return new JsonResponseDto(false, HttpStatus.BAD_REQUEST.value(), "No se realizaron cambios en el horario", horarioExistente,null);
        }

        try {
            Horario horarioGuardado = horarioRepository.save(horarioExistente);
            return new JsonResponseDto(true, HttpStatus.OK.value(), "Horario actualizado correctamente", horarioGuardado,null);
        } catch (Exception e) {
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al actualizar el horario: " + e.getMessage(),null, null);
        }
    }

    public JsonResponseDto eliminar(Long id) {
        if (!horarioRepository.existsById(id)) {
            return new JsonResponseDto(false, HttpStatus.NOT_FOUND.value(), "El horario con el ID proporcionado no existe", null, null);
        }

        try {
            horarioRepository.deleteById(id);
            return new JsonResponseDto(true, HttpStatus.OK.value(), "Horario eliminado correctamente", null, null);
        } catch (Exception e) {
            return new JsonResponseDto(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al eliminar el horario: " + e.getMessage(), null, null);
        }
    }
}
