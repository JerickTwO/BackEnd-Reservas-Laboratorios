package com.masache.masachetesis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioTablaDto {
    private Long id;
    private String nombreCompleto;
    private String correo;
    private String idInstitucional;
    private String departamento; // Opcional, puede ser null
    private String tipoUsuario;  // "ADMINISTRADOR" o "DOCENTE"
}
