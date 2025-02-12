package com.masache.masachetesis.repositories;

import com.masache.masachetesis.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long>{

    Usuario findUsuarioByUsuarioAndEstadoTrue(String usuario);
    List<Usuario> findUsuariosByEstadoTrue();
    boolean existsByUsuario(String usuario);
    boolean existsByCorreo(String correo);
    boolean existsByUsuarioAndEstadoTrue(String usuario);

}
