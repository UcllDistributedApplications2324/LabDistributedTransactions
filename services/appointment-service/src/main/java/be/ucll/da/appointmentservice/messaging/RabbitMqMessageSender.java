package be.ucll.da.appointmentservice.messaging;

import be.ucll.da.appointmentservice.client.accounting.api.model.ClosePatientAccountCommand;
import be.ucll.da.appointmentservice.client.accounting.api.model.OpenPatientAccountCommand;
import be.ucll.da.appointmentservice.client.doctor.api.model.CheckDoctorEmployedCommand;
import be.ucll.da.appointmentservice.client.notification.api.model.SendEmailCommand;
import be.ucll.da.appointmentservice.client.patient.api.model.ValidatePatientCommand;
import be.ucll.da.appointmentservice.client.room.api.model.ReleaseRoomCommand;
import be.ucll.da.appointmentservice.client.room.api.model.ReserveRoomCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RabbitMqMessageSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessageSender.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMqMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendValidatePatientCommand(Integer appointmentId, Integer patientId) {
        var command = new ValidatePatientCommand();
        command.patientId(patientId);
        command.appointmentId(appointmentId);
        sendToQueue("q.patient-service.validate-patient", command);
    }

    public void sendValidateDoctorCommand(Integer appointmentId, String fieldOfExpertise) {
        var command = new CheckDoctorEmployedCommand();
        command.appointmentId(appointmentId);
        command.fieldOfExpertise(fieldOfExpertise);
        sendToQueue("q.doctor-service.check-employed-doctors", command);
    }

    public void sendBookRoomCommand(Integer appointmentId, LocalDate preferredDay) {
        var command = new ReserveRoomCommand();
        command.appointmentId(appointmentId);
        command.day(preferredDay);
        sendToQueue("q.room-service.book-room", command);
    }

    public void sendReleaseRoomCommand(Integer appointmentId, Integer roomId, LocalDate preferredDay) {
        var command = new ReleaseRoomCommand();
        command.appointmentId(appointmentId);
        command.roomId(roomId);
        command.day(preferredDay);
        sendToQueue("q.room-service.release-room", command);
    }

    public void sendOpenAccountCommand(Integer appointmentId, Integer patientId, Integer doctorId, Integer roomId, LocalDate day) {
        var command = new OpenPatientAccountCommand();
        command.appointmentId(appointmentId);
        command.patientId(patientId);
        command.doctorId(doctorId);
        command.roomId(roomId);
        command.dayOfAppointment(day);
        sendToQueue("q.account-service.open-account", command);
    }

    public void sendCloseAccountCommand(Integer id, Integer patientId, Integer accountId) {
        var command = new ClosePatientAccountCommand();
        command.appointmentId(id);
        command.patientId(patientId);
        command.setAccountId(accountId);
        sendToQueue("q.account-service.close-account", command);
    }

    public void sendEmail(String recipient, String message) {
        var command = new SendEmailCommand();
        command.recipient(recipient);
        command.message(message);
        sendToQueue("q.notification-service.send-email", command);
    }

    private void sendToQueue(String queue, Object message) {
        LOGGER.info("Sending message: " + message);

        this.rabbitTemplate.convertAndSend(queue, message);
    }
}
