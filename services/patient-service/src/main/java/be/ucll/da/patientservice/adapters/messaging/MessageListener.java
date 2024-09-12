package be.ucll.da.patientservice.adapters.messaging;

import be.ucll.da.patientservice.api.messaging.model.PatientValidatedEvent;
import be.ucll.da.patientservice.api.messaging.model.ValidatePatientCommand;
import be.ucll.da.patientservice.domain.Patient;
import be.ucll.da.patientservice.domain.PatientService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final PatientService patientService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(PatientService patientService, RabbitTemplate rabbitTemplate) {
        this.patientService = patientService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.patient-service.validate-patient"})
    public void onValidatePatient(ValidatePatientCommand command) {
        LOGGER.info("Received command: " + command);

        Patient patient = patientService.validatePatient(command.getPatientId());
        PatientValidatedEvent event = new PatientValidatedEvent();
        event.appointmentId(command.getAppointmentId());
        event.patientId(patient.id());
        event.firstName(patient.firstName());
        event.lastName(patient.lastName());
        event.email(patient.email());
        event.isClient(patient.isClient());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.patient-validated", "", event);
    }
}
