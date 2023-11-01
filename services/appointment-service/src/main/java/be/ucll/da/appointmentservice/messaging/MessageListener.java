package be.ucll.da.appointmentservice.messaging;

import be.ucll.da.appointmentservice.client.accounting.api.model.PatientAccountCreatedEvent;
import be.ucll.da.appointmentservice.client.accounting.api.model.PatientAccountNotCreatedEvent;
import be.ucll.da.appointmentservice.client.doctor.api.model.DoctorsOnPayrollEvent;
import be.ucll.da.appointmentservice.client.patient.api.model.PatientValidatedEvent;
import be.ucll.da.appointmentservice.client.room.api.model.RoomReservedEvent;
import be.ucll.da.appointmentservice.domain.appointment.AppointmentRequestSaga;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class MessageListener {

    private final AppointmentRequestSaga saga;

    @Autowired
    public MessageListener(AppointmentRequestSaga saga) {
        this.saga = saga;
    }

    @RabbitListener(queues = {"q.patient-validated.appointment-service"})
    public void onPatientValidated(PatientValidatedEvent event) {
        this.saga.executeSaga(event.getAppointmentId(), event);
    }

    @RabbitListener(queues = {"q.doctors-employed.appointment-service"})
    public void onDoctorsEmployed(DoctorsOnPayrollEvent event) {
        this.saga.executeSaga(event.getAppointmentId(), event);
    }

    @RabbitListener(queues = {"q.room-bookings.appointment-service"})
    public void onRoomReserved(RoomReservedEvent event) {
        this.saga.executeSaga(event.getAppointmentId(), event);
    }

    @RabbitListener(queues = {"q.account-openings.appointment-service"})
    public void onRoomReserved(PatientAccountCreatedEvent event) {
        this.saga.executeSaga(event.getAppointmentId(), event);
    }

    @RabbitListener(queues = {"q.account-openings.appointment-service"})
    public void onRoomReserved(PatientAccountNotCreatedEvent event) {
        this.saga.executeSaga(event.getAppointmentId(), event);
    }
}
