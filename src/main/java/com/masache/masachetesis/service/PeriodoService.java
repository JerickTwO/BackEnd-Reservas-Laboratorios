package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Periodo;
import com.masache.masachetesis.repositories.PeriodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodoService {

    private final PeriodoRepository periodoRepository;
    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }
    public List<Periodo> obtenerTodos() {
        return periodoRepository.findAll();
    }
    public Optional<Periodo> obtenerPorId(Long id) {
        return periodoRepository.findById(id);
    }

    @Transactional
    public Periodo guardar(Periodo periodo) {
        if (periodoRepository.existsByNombrePeriodo(periodo.getNombrePeriodo())) {
            throw new IllegalArgumentException("El nombre del periodo ya está en uso.");
        }
        return periodoRepository.save(periodo);
    }

    @Transactional
    public Periodo actualizar(Long id, Periodo periodoActualizado) {
        if (!periodoRepository.existsById(id)) {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
        periodoActualizado.setIdPeriodo(id);
        periodoActualizado.setFechaInicio(periodoActualizado.getFechaInicio());
        periodoActualizado.setFechaFin(periodoActualizado.getFechaFin());
        periodoActualizado.setEstado(false);
        return periodoRepository.save(periodoActualizado);
    }

    @Transactional
    public Periodo cambiarEstado(Long id, boolean estado) {
        Optional<Periodo> periodoOpt = periodoRepository.findById(id);
        if (periodoOpt.isPresent()) {
            Periodo periodo = periodoOpt.get();
            if (estado) {
                List<Periodo> periodosActivos = periodoRepository.findByEstado(true);
                for (Periodo p : periodosActivos) {
                    p.setEstado(false);
                    periodoRepository.save(p);
                }
            }
            periodo.setEstado(estado);
            return periodoRepository.save(periodo);
        } else {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        if (!periodoRepository.existsById(id)) {
            throw new IllegalArgumentException("El periodo con el ID proporcionado no existe.");
        }
        periodoRepository.deleteById(id);
    }
}
