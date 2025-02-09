package com.masache.masachetesis.config;

import com.masache.masachetesis.models.Dia;
import com.masache.masachetesis.models.Laboratorio;
import com.masache.masachetesis.models.Roles;
import com.masache.masachetesis.repositories.DiaRepository;
import com.masache.masachetesis.repositories.LaboratorioRepository;
import com.masache.masachetesis.repositories.RolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Inicializador implements CommandLineRunner {

    private final RolesRepository rolesRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final DiaRepository diaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        log.info("Verificando e insertando parámetros de sistema si no existen...");
        List<Roles> roles = List.of(
                Roles.builder().nombre("admin").descripcion("Rol de administrador").build(),
                Roles.builder().nombre("user").descripcion("Rol de usuario estándar").build()
        );



        List<Roles> newRoles = roles.stream()
                .filter(role -> rolesRepository.findByNombre(role.getNombre()).isEmpty())
                .toList();

        if (!newRoles.isEmpty()) {
            log.info("Insertando nuevos roles: {}", newRoles);
            rolesRepository.saveAll(newRoles);
        }

        List<Laboratorio> laboratorios = List.of(
                new Laboratorio(null, "LAB-01", "BLOQUE A", 32),
                new Laboratorio(null, "LAB-02", "BLOQUE A", 32),
                new Laboratorio(null, "LAB-03", "BLOQUE A", 32),
                new Laboratorio(null, "LAB-04", "BLOQUE A", 32),
                new Laboratorio(null, "LAB-05", "BLOQUE B", 20),
                new Laboratorio(null, "LAB-06", "BLOQUE B", 20)
        );

        List<Laboratorio> newLaboratorios = laboratorios.stream()
                .filter(lab -> laboratorioRepository.findByNombreLaboratorio(lab.getNombreLaboratorio()).isEmpty())
                .toList();

        if (!newLaboratorios.isEmpty()) {
            log.info("Insertando nuevos laboratorios: {}", newLaboratorios);
            laboratorioRepository.saveAll(newLaboratorios);
        }

        List<String> diasSemana = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");

        for (String dia : diasSemana) {
            if (diaRepository.findByNombre(dia).isEmpty()) {
                Dia nuevoDia = new Dia();
                nuevoDia.setNombre(dia);
                diaRepository.save(nuevoDia);
            }
        }

        log.info("Parámetros de sistema verificados/inicializados correctamente.");
    }
}
