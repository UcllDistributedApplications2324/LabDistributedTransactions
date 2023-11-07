package be.ucll.da.doctorservice.messaging;

import be.ucll.da.doctorservice.api.messaging.model.CheckDoctorEmployedCommand;
import be.ucll.da.doctorservice.api.messaging.model.DoctorOnPayroll;
import be.ucll.da.doctorservice.api.messaging.model.DoctorsOnPayrollEvent;
import be.ucll.da.doctorservice.domain.Doctor;
import be.ucll.da.doctorservice.domain.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class MessageListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final DoctorService doctorService;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageListener(DoctorService doctorService, RabbitTemplate rabbitTemplate) {
        this.doctorService = doctorService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"q.doctor-service.check-employed-doctors"})
    public void onCheckEmployedDoctors(CheckDoctorEmployedCommand command) {
        LOGGER.info("Received command: " + command);

        List<Doctor> doctors = doctorService.getDoctorsOnPayroll(command.getFieldOfExpertise());
        DoctorsOnPayrollEvent event = new DoctorsOnPayrollEvent();
        event.appointmentId(command.getAppointmentId());
        event.fieldOfExpertise(command.getFieldOfExpertise());
        for (Doctor doctor : doctors) {
            DoctorOnPayroll dop = new DoctorOnPayroll();
            dop.id(doctor.id());
            dop.firstName(doctor.firstName());
            dop.lastName(doctor.lastName());
            dop.age(doctor.age());
            dop.address(doctor.address());
            event.addDoctorsItem(dop);
        }

        LOGGER.info("Sending event: " + event);
        this.rabbitTemplate.convertAndSend("x.doctors-employed", "", event);
    }
}
