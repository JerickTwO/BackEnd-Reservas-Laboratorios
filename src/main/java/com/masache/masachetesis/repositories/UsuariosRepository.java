package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long>{

    Usuario findUsuarioByUsuarioAndEstadoTrue(String usuario);
    List<Usuario> findUsuariosByEstadoTrue();
<<<<<<< HEAD
=======
    Usuario findByCorreoAndEstadoTrue(String correo);
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo) AND u.estado = true")
    Usuario buscarPorCorreoYEstado(@Param("correo") String correo);
>>>>>>> 1ec40cdddf3f426bcc1eb5857951843d51545b3b
    boolean existsByUsuario(String usuario);
    boolean existsByUsuarioAndEstadoTrue(String usuario);
    Usuario findByUsuarioAndAndEstadoIsTrue(String username);
}
