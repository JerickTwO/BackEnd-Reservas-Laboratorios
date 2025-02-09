package com.masache.masachetesis.service;

import com.masache.masachetesis.models.Administrador;
import com.masache.masachetesis.models.Reserva;
import com.masache.masachetesis.models.Usuario;
import com.masache.masachetesis.repositories.AdministradorRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    @Autowired
    private AdministradorRepository administradorRepository;

    private final JavaMailSender mailSender;

    public void enviarCorreoReserva(Reserva reserva, Usuario usuario) {
        List<Administrador> administradores = administradorRepository.findAll();
        if (administradores.isEmpty()) {
            log.warn("No hay administradores registrados. No se enviará el correo.");
            return;
        }

        List<String> correosAdmin = administradores.stream()
                .map(Administrador::getCorreoAdministrador)
                .collect(Collectors.toList());

        try {
            InputStream inputStream = new ClassPathResource("templates/new_reservation.html").getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder emailContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                emailContent.append(line).append("\n");
            }
            log.info("Usuario: {}", usuario.getUsuario());
            log.info("Nombre Completo: {}", reserva.getNombreCompleto());
            log.info("Correo: {}", reserva.getCorreo());
            log.info("Teléfono: {}", reserva.getTelefono());
            log.info("Laboratorio: {}", (reserva.getLaboratorio() != null ? reserva.getLaboratorio().getNombreLaboratorio() : "NULL"));
            log.info("Hora Inicio: {}", reserva.getHoraInicio());
            log.info("Hora Fin: {}", reserva.getHoraFin());
            log.info("Motivo: {}", reserva.getMotivoReserva());
            log.info("Estado: {}", reserva.getEstado());


            String emailBody = emailContent.toString()
                    .replace("{{usuario}}", usuario.getUsuario() != null ? usuario.getUsuario() : "Usuario Desconocido")
                    .replace("{{nombreCompleto}}", reserva.getNombreCompleto() != null ? reserva.getNombreCompleto() : "No especificado")
                    .replace("{{correo}}", reserva.getCorreo() != null ? reserva.getCorreo() : "No disponible")
                    .replace("{{telefono}}", reserva.getTelefono() != null ? reserva.getTelefono() : "No disponible")
                    .replace("{{nombreLaboratorio}}", reserva.getLaboratorio() != null && reserva.getLaboratorio().getNombreLaboratorio() != null ? reserva.getLaboratorio().getNombreLaboratorio() : "Sin laboratorio")
                    .replace("{{horaInicio}}", reserva.getHoraInicio() != null ? reserva.getHoraInicio().toString() : "No especificado")
                    .replace("{{horaFin}}", reserva.getHoraFin() != null ? reserva.getHoraFin().toString() : "No especificado")
                    .replace("{{motivoReserva}}", reserva.getMotivoReserva() != null ? reserva.getMotivoReserva() : "No especificado")
                    .replace("{{estadoReserva}}", reserva.getEstado() != null ? reserva.getEstado().toString() : "No especificado");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(correosAdmin.get(0)); // Primer administrador como destinatario principal
            if (correosAdmin.size() > 1) {
                helper.setCc(correosAdmin.subList(1, correosAdmin.size()).toArray(new String[0])); // Resto en CC
            }
            helper.setSubject("Nueva Reserva Creada");
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("Correo de reserva enviado correctamente a administradores: {}", correosAdmin);
        } catch (Exception e) {
            log.error("Error al enviar el correo de reserva: {}", e.getMessage(), e);
        }
    }

    public void sendNewUserEmail(Usuario user, String password) {
        try {
            // Load the email template
            InputStream inputStream = new ClassPathResource("templates/new_user.html").getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder emailContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                emailContent.append(line).append("\n");
            }

            // Prevent null values
            String nombre = user.getUsuario() != null ? user.getUsuario() : "Usuario";
            String correo = user.getCorreo() != null ? user.getCorreo() : "correo@example.com";
            String userPassword = password != null ? password : "N/A";

            // Replace placeholders with actual values
            String emailBody = emailContent.toString()
                    .replace("{{nombre}}", nombre)
                    .replace("{{correo}}", correo)
                    .replace("{{password}}", userPassword);

            // Configure the email
            log.info("Los datos mandados son {} ", user);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getCorreo());
            helper.setSubject("LabReservas - Credenciales de Acceso");
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("Correo de bienvenida enviado correctamente a: {}", user.getCorreo());

        } catch (Exception e) {
            log.error("Error al enviar el correo de bienvenida: {}", e.getMessage(), e);
            throw new IllegalStateException("No se pudo enviar el correo de bienvenida.", e);
        }
    }



}
