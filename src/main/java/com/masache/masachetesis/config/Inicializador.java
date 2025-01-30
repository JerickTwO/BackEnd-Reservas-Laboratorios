package com.masache.masachetesis.config;

import com.masache.masachetesis.models.Roles;
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


        log.info("Parámetros de sistema verificados/inicializados correctamente.");
    }
}
