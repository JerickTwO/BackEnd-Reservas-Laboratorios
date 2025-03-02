package com.masache.masachetesis.config;

import com.masache.masachetesis.models.*;
import com.masache.masachetesis.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Inicializador implements CommandLineRunner {

    private final RolesRepository rolesRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final DocenteRepository docenteRepository;
    private final UsuariosRepository usuariosRepository;
    private final AdministradorRepository administradorRepository;
    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final HorarioRepository horarioRepository;

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

        Roles adminRole = rolesRepository.findById(1L).orElseThrow(() -> new RuntimeException("Rol de administrador no encontrado"));
        Roles docenteRole = rolesRepository.findById(2L).orElseThrow(() -> new RuntimeException("Rol de docente no encontrado"));

        // Crear usuarios
        List<Usuario> usuarios = List.of(new Usuario(null, "admin", "Ana", "López", "jerickjjtm774@gmail.com", "$2a$12$PgD/fKcNC46SjVLzhR3CdeEDj9UYvM.pvJ2lkvjDQqOMLLRocAGDW", adminRole, true, true, null), new Usuario(null, "L123456789", "Juan", "Pérez", "juan.perez@escuela.com", "$2a$12$.5mrqMQIp5/uqvjwhhY86OeraRv3sDVE2S9ga/ovR4joA02krE3rC", docenteRole, true, true, null));

        List<Usuario> newUsuarios = usuarios.stream().filter(usuario -> !usuariosRepository.existsByUsuario(usuario.getUsuario())).toList();
        if (!newUsuarios.isEmpty()) {
            log.info("Insertando nuevos usuarios: {}", newUsuarios);
            usuariosRepository.saveAll(newUsuarios);
        }

        // Verificar y agregar departamentos
        if (!departamentoRepository.existsByNombreDepartamento("Ciencias de la vida")) {
            Departamento departamento1 = new Departamento();
            departamento1.setNombreDepartamento("Ciencias de la vida");
            departamento1.setDescripcion("Departamento dedicado al estudio de los seres vivos.");
            departamentoRepository.save(departamento1);
            log.info("Departamento 'Ciencias de la vida' creado.");
        }

        if (!departamentoRepository.existsByNombreDepartamento("Ciencias exactas")) {
            Departamento departamento2 = new Departamento();
            departamento2.setNombreDepartamento("Ciencias exactas");
            departamento2.setDescripcion("Departamento dedicado al estudio de las ciencias matemáticas y físicas.");
            departamentoRepository.save(departamento2);
            log.info("Departamento 'Ciencias exactas' creado.");
        }

        if (!departamentoRepository.existsByNombreDepartamento("Ciencias de la computación")) {
            Departamento departamento3 = new Departamento();
            departamento3.setNombreDepartamento("Ciencias de la computación");
            departamento3.setDescripcion("Departamento dedicado al estudio de la informática y la programación.");
            departamentoRepository.save(departamento3);
            log.info("Departamento 'Ciencias de la computación' creado.");
        }

        // Recuperar departamento 'Ciencias de la computación'
        Departamento departamentoCiencasComputacion = departamentoRepository.findByNombreDepartamento("Ciencias de la computación").orElseThrow(() -> new RuntimeException("Departamento 'Ciencias de la computación' no encontrado"));

        // Verificar si el docente ya existe
        if (!docenteRepository.existsByCorreoDocente("juan.perez@escuela.com")) {
            Docente docente = new Docente();
            docente.setNombreDocente("Juan");
            docente.setApellidoDocente("Pérez");
            docente.setCorreoDocente("juan.perez@escuela.com");
            docente.setDepartamento(departamentoCiencasComputacion);
            docente.setIdInstitucional("L123456789");
            docenteRepository.save(docente);
            log.info("Docente Juan Pérez creado.");
        }

        if (!administradorRepository.existsByCorreoAdministrador("ana.lopez@admin.com")) {
            Administrador administrador = new Administrador();
            administrador.setNombreAdministrador("Ana");
            administrador.setApellidoAdministrador("López");
            administrador.setCorreoAdministrador("ana.lopez@admin.com");
            administrador.setIdInstitucional("L123456789");
            administradorRepository.save(administrador);
            log.info("Administrador Ana López creado.");
        }

        List<Laboratorio> laboratorios = List.of(new Laboratorio(null, "LAB-01", "BLOQUE A", 32), new Laboratorio(null, "LAB-02", "BLOQUE A", 32), new Laboratorio(null, "LAB-03", "BLOQUE A", 32), new Laboratorio(null, "LAB-04", "BLOQUE A", 32), new Laboratorio(null, "LAB-05", "BLOQUE B", 20), new Laboratorio(null, "LAB-06", "BLOQUE B", 20));

        List<Laboratorio> newLaboratorios = laboratorios.stream().filter(lab -> laboratorioRepository.findByNombreLaboratorio(lab.getNombreLaboratorio()).isEmpty()).toList();

        if (!newLaboratorios.isEmpty()) {
            log.info("Insertando nuevos laboratorios: {}", newLaboratorios);
            laboratorioRepository.saveAll(newLaboratorios);
        }
        List<Carrera> carreras = List.of(new Carrera(null, "CIENCIAS ECON. ADMIN. Y COMERC"), new Carrera(null, "CIENCIAS HUMANAS Y SOCIALES"), new Carrera(null, "CIENCIAS DE LA COMPUTACION"));
        List<Carrera> newCarreras = carreras.stream().filter(carrera -> !carreraRepository.existsByNombreCarrera(carrera.getNombreCarrera())).toList();

        if (!newCarreras.isEmpty()) {
            log.info("Insertando nuevas carreras: {}", newCarreras);
            carreraRepository.saveAll(newCarreras);
        }

        // Crear materias si no existen
        List<Materia> materias = List.of(new Materia(null, "GESTION Y EMPRENDIMIENTO", "6050", 6), new Materia(null, "ADMINISTRACION AGROPECUARIA", "4756", 9), new Materia(null, "METOD. DE LA INVESTG. CIENTIF.", "6051", 6), new Materia(null, "MINERIA DE DATOS", "1430", 9), new Materia(null, "INTELIGENCIA ARTIFICIAL", "1424", 6), new Materia(null, "MODELADO AVAN DE BASE DE DATOS", "1425", 9), new Materia(null, "PRACTICAS LABORALES II", "3184", 6), new Materia(null, "GEST. SEGURIDAD INFORMATICA", "1434", 6), new Materia(null, "FUND. DE PROGRAMACION", "1391", 9), new Materia(null, "PROGRAMACION AVANZADA", "1429", 9), new Materia(null, "SISTEMAS OPERATIVOS", "1409", 6), new Materia(null, "ARQUITECTURA DE SOFTWARE", "1436", 9), new Materia(null, "LECT. ESC. TEXTOS ACADEMICOS", "1427", 6));

        List<Materia> newMaterias = materias.stream().filter(materia -> !materiaRepository.existsByNrc(materia.getNrc())).toList();

        if (!newMaterias.isEmpty()) {
            log.info("Insertando nuevas materias: {}", newMaterias);
            materiaRepository.saveAll(newMaterias);
        }
        List<String> franjas = Arrays.asList("07:00-08:00", "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00");

        List<String> dias = Arrays.asList("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES");

        boolean horarioExiste = franjas.stream().anyMatch(franja -> dias.stream().anyMatch(dia -> horarioRepository.existsByFranjaAndDia(franja, dia)));

        if (!horarioExiste) {
            Horario horario = new Horario();
            horario.setFranjasHorario(franjas);
            horario.setDiasHorario(dias);
            horarioRepository.save(horario);
            log.info("Horario de prueba creado.");
        } else {
            log.info("El horario ya existe. No se insertará nuevamente.");
        }


        log.info("Parámetros de sistema verificados/inicializados correctamente.");
    }


}
