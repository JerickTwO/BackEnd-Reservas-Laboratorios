package com.masache.masachetesis.service;

import com.masache.masachetesis.repositories.RolesRepository;
import com.masache.masachetesis.models.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RolesService {
    private final RolesRepository rolesRepository;

    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    // Obtener un rol por ID
    public Optional<Roles> getRolById(Long id) {
        return rolesRepository.findById(id);
    }

    // Guardar un nuevo rol
    public Roles saveRol(Roles rol) {
        return rolesRepository.save(rol);
    }

    // Eliminar un rol por ID
    public void deleteRol(Long id) {
        Optional<Roles> rol = rolesRepository.findById(id);
        if (rol.isPresent()) {
            rolesRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("El rol con ID " + id + " no existe.");
        }
    }

    // Actualizar un rol existente
    public Roles updateRol(long id, Roles rolDetails) {
        return rolesRepository.findById(id)
                .map(rol -> {
                    rol.setNombre(rolDetails.getNombre()); // Actualizar solo el nombre, puedes ajustar según tus necesidades
                    return rolesRepository.save(rol);
                })
                .orElseThrow(() -> new IllegalArgumentException("El rol con ID " + id + " no se encuentra."));
    }

}
