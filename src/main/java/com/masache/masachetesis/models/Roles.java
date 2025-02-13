package com.masache.masachetesis.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
