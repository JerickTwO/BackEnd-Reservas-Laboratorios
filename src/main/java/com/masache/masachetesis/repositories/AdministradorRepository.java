package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    // Método para verificar si existe un administrador por correo
    boolean existsByCorreoAdministrador(String correoAdministrador);

    // Puedes agregar más métodos si es necesario, por ejemplo, buscar por ID institucional
    Optional<Administrador> findByIdInstitucional(String idInstitucional);
}
