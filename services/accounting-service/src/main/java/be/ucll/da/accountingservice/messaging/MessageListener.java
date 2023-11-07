package be.ucll.da.accountingservice.messaging;

import be.ucll.da.accountingservice.api.messaging.model.ClosePatientAccountCommand;
import be.ucll.da.accountingservice.api.messaging.model.OpenPatientAccountCommand;
import be.ucll.da.accountingservice.api.messaging.model.PatientAccountCreatedEvent;
import be.ucll.da.accountingservice.api.messaging.model.PatientAccountTerminatedEvent;
import be.ucll.da.accountingservice.domain.AccountingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final AccountingService accountingService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(AccountingService accountingService, RabbitTemplate rabbitTemplate) {
        this.accountingService = accountingService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.account-service.open-account"})
    public void onOpenAccount(OpenPatientAccountCommand command) {
        LOGGER.info("Received command: " + command);

        Integer accountId = accountingService.openAccount(command.getPatientId());

        PatientAccountCreatedEvent event = new PatientAccountCreatedEvent();
        event.appointmentId(command.getAppointmentId());
        event.patientId(command.getPatientId());
        event.dayOfAppointment(command.getDayOfAppointment());
        if (accountId != -1) {
            event.accountCreated(true);
            event.accountId(accountId);
        } else {
            event.accountCreated(false);
            event.error("There was an error opening the account");
        }

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.account-openings", "", event);
    }

    @RabbitListener(queues = {"q.account-service.close-account"})
    public void onCloseAccount(ClosePatientAccountCommand command) {
        LOGGER.info("Received command: " + command);
        accountingService.closeAccount(command.getAccountId());

        PatientAccountTerminatedEvent event = new PatientAccountTerminatedEvent();
        event.appointmentId(command.getAppointmentId());
        event.accountId(command.getAccountId());
        event.patientId(command.getPatientId());

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.account-terminations", "", event);
    }
}
