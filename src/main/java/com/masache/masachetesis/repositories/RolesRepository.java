package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>{
    Optional<Roles> findByNombre(String nombre);
    Optional<Roles> findById(Long id);
}
