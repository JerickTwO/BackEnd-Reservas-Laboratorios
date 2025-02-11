package com.masache.masachetesis.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@Builder
public class Roles {
    @Id
    private Long id;
    private String nombre;
    private String descripcion;
}
