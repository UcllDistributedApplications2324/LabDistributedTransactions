package be.ucll.da.appointmentservice.domain.appointment;

import be.ucll.da.appointmentservice.client.accounting.api.model.PatientAccountCreatedEvent;
import be.ucll.da.appointmentservice.client.doctor.api.model.DoctorOnPayroll;
import be.ucll.da.appointmentservice.client.doctor.api.model.DoctorsOnPayrollEvent;
import be.ucll.da.appointmentservice.client.patient.api.model.PatientValidatedEvent;
import be.ucll.da.appointmentservice.client.room.api.model.RoomReservedEvent;
import be.ucll.da.appointmentservice.messaging.RabbitMqMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentRequestSaga {

    private final RabbitMqMessageSender eventSender;
    private final AppointmentRepository repository;

    @Autowired
    public AppointmentRequestSaga(RabbitMqMessageSender eventSender, AppointmentRepository repository) {
        this.eventSender = eventSender;
        this.repository = repository;
    }

    public void executeSaga(Appointment appointment) {
        appointment.validatingPatient();
        eventSender.sendValidatePatientCommand(appointment.getId(), appointment.getPatientId());
    }

    public void executeSaga(Integer id, PatientValidatedEvent event) {
        Appointment appointment = getAppointmentById(id);
        if (event.getIsClient()) {
            appointment.patientValid(event.getEmail());
            eventSender.sendValidateDoctorCommand(appointment.getId(), appointment.getNeededExpertise());
        } else {
            appointment.patientInvalid();
            eventSender.sendEmail(event.getEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, you are not a patient at this clinic."));
        }
    }

    public void executeSaga(Integer id, DoctorsOnPayrollEvent event) {
        Appointment appointment = getAppointmentById(id);

        Integer selectedDoctor = null;
        if (event.getDoctors() != null && !event.getDoctors().isEmpty()) {
            for (DoctorOnPayroll doctor : event.getDoctors()) {
                List<Appointment> appointments = repository.getAppointmentsForDoctorOnDay(doctor.getId(), appointment.getPreferredDay());

                if (appointments.isEmpty()) {
                    selectedDoctor = doctor.getId();
                    break;
                }
            }
        }

        if (selectedDoctor != null) {
            appointment.doctorSelected(selectedDoctor);
            eventSender.sendBookRoomCommand(appointment.getId(), appointment.getPreferredDay());
        } else {
            appointment.noDoctorsFound();
            eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, we have no doctors for your requested expertise available on this day."));
        }
    }

    public void executeSaga(Integer id, RoomReservedEvent event) {
        Appointment appointment = getAppointmentById(id);
        if (event.getRoomAvailable()) {
            appointment.roomAvailable(event.getRoomId());
            eventSender.sendOpenAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getDoctor(), appointment.getRoomId(), appointment.getPreferredDay());
        } else {
            appointment.noRoomAvailable();
            eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, we have no room available on your preferred day."));
        }
    }

    public void executeSaga(Integer id, PatientAccountCreatedEvent event) {
        Appointment appointment = getAppointmentById(id);

        if (event.getAccountCreated()) {
            // Final check to make sure that another request that went through in parallel does not also succeed
            List<Appointment> appointments = repository.getAppointmentsForDoctorOnDay(appointment.getDoctor(), appointment.getPreferredDay());
            if (appointments.isEmpty()) {
                appointment.finalizeAppointment(event.getAccountId());
                eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "Proposal for appointment registered. Please accept or decline."));
            } else {
                appointment.doubleBooking();
                eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
                eventSender.sendCloseAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getAccountId());
                eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, there was an error in our system."));
            }
        } else {
            appointment.noInsurance();
            eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
            eventSender.sendEmail(appointment.getPatientEmail(), generateMessage(appointment.getId(), "You cannot book an appointment, you are not insured."));
        }


    }

    public void accept(Integer id) {
        Appointment appointment = getAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.REQUEST_REGISTERED) {
            appointment.accept();
        } else {
            throw new RuntimeException("AppointmentRequest is not in a valid status to be accepted");
        }
    }

    public void decline(Integer id) {
        Appointment appointment = getAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.REQUEST_REGISTERED) {
            appointment.decline();
            eventSender.sendReleaseRoomCommand(appointment.getId(), appointment.getRoomId(), appointment.getPreferredDay());
            eventSender.sendCloseAccountCommand(appointment.getId(), appointment.getPatientId(), appointment.getAccountId());
        } else {
            throw new RuntimeException("AppointmentRequest is not in a valid status to be declined");
        }
    }

    private Appointment getAppointmentById(Integer id) {
        return repository.findById(id).orElseThrow();
    }

    private String generateMessage(Integer id, String message) {
        return "Regarding appointment " + id + ". " + message;
    }
}
