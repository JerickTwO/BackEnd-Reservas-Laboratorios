package com.masache.masachetesis.config;

import com.masache.masachetesis.models.*;
import com.masache.masachetesis.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UsuariosRepository usuariosRepository;
    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        log.info("Verificando e insertando parámetros de sistema si no existen...");

        if (!rolesRepository.existsById(1L)) {
            rolesRepository.save(new Roles(1L, "admin", "Rol de administrador"));
        }
        if (!rolesRepository.existsById(2L)) {
            rolesRepository.save(new Roles(2L, "docente", "Rol de docente"));
        }
        if (!rolesRepository.existsById(3L)) {
            rolesRepository.save(new Roles(3L, "estudiante", "Rol de estudiante"));
        }

        Roles adminRole = rolesRepository.findById(1L).orElseThrow(() -> new RuntimeException("Rol de administrador no encontrado"));
        Roles docenteRole = rolesRepository.findById(2L).orElseThrow(() -> new RuntimeException("Rol de docente no encontrado"));
        Roles estudianteRole = rolesRepository.findById(3L).orElseThrow(() -> new RuntimeException("Rol de estudiante no encontrado"));

        List<Usuario> usuarios = List.of(
                new Usuario(null, "admin", "admin", "admin", "admin@admin.com", "$2a$12$PgD/fKcNC46SjVLzhR3CdeEDj9UYvM.pvJ2lkvjDQqOMLLRocAGDW", adminRole, true, true),
                new Usuario(null, "docente", "docente", "docente", "docente@escuela.com", "$2a$12$.5mrqMQIp5/uqvjwhhY86OeraRv3sDVE2S9ga/ovR4joA02krE3rC", docenteRole, true, true),
                new Usuario(null, "estudiante", "estudiante", "estudiante", "estudiante@escuela.com", "$2a$12$HguyT.7iuUU6vcWSsEw.tOF6023CceTyXpv/gA6m7c5mHSEd86PZO", estudianteRole, true, true)
        );

        List<Usuario> newUsuarios = usuarios.stream()
                .filter(usuario -> !usuariosRepository.existsByUsuario(usuario.getUsuario()))
                .toList();
        if (!newUsuarios.isEmpty()) {
            log.info("Insertando nuevos usuarios: {}", newUsuarios);
            usuariosRepository.saveAll(newUsuarios);
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
        List<Carrera> carreras = List.of(
                new Carrera(null, "CIENCIAS ECON. ADMIN. Y COMERC"),
                new Carrera(null, "CIENCIAS HUMANAS Y SOCIALES"),
                new Carrera(null, "CIENCIAS DE LA COMPUTACION")
        );

        List<Carrera> newCarreras = carreras.stream()
                .filter(carrera -> !carreraRepository.existsByNombreCarrera(carrera.getNombreCarrera()))
                .toList();

        if (!newCarreras.isEmpty()) {
            log.info("Insertando nuevas carreras: {}", newCarreras);
            carreraRepository.saveAll(newCarreras);
        }

        List<Materia> materias = List.of(
                new Materia(null, "GESTION Y EMPRENDIMIENTO", "6050", 6),
                new Materia(null, "ADMINISTRACION AGROPECUARIA", "4756", 9),
                new Materia(null, "METOD. DE LA INVESTG. CIENTIF.", "6051", 6),
                new Materia(null, "MINERIA DE DATOS", "1430", 9),
                new Materia(null, "INTELIGENCIA ARTIFICIAL", "1424", 6),
                new Materia(null, "MODELADO AVAN DE BASE DE DATOS", "1425", 9),
                new Materia(null, "PRACTICAS LABORALES II", "3184", 6),
                new Materia(null, "GEST. SEGURIDAD INFORMATICA", "1434", 6),
                new Materia(null, "FUND. DE PROGRAMACION", "1391", 9),
                new Materia(null, "PROGRAMACION AVANZADA", "1429", 9),
                new Materia(null, "SISTEMAS OPERATIVOS", "1409", 6),
                new Materia(null, "ARQUITECTURA DE SOFTWARE", "1436", 9),
                new Materia(null, "LECT. ESC. TEXTOS ACADEMICOS", "1427", 6)
        );

        List<Materia> newMaterias = materias.stream()
                .filter(materia -> !materiaRepository.existsByNrc(materia.getNrc()))
                .toList();

        if (!newMaterias.isEmpty()) {
            log.info("Insertando nuevas materias: {}", newMaterias);
            materiaRepository.saveAll(newMaterias);
        }

        log.info("Parámetros de sistema verificados/inicializados correctamente.");
    }



}
