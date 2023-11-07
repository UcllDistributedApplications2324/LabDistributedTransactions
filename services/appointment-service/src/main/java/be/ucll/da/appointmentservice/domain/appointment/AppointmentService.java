package be.ucll.da.appointmentservice.domain.appointment;

import be.ucll.da.appointmentservice.api.model.ApiAppointmentConfirmation;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentRequestSaga requestSaga;

    @Autowired
    public AppointmentService(AppointmentRepository repository, AppointmentRequestSaga requestSaga) {
        this.repository = repository;
        this.requestSaga = requestSaga;
    }

    public String registerRequest(ApiAppointmentRequest request) {
        var appointment = new Appointment(request.getPatientId(), request.getPreferredDay(), request.getNeededExpertise());

        appointment = repository.save(appointment);
        requestSaga.executeSaga(appointment);

        return appointment.getId().toString();
    }

    public void finalizeAppointment(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        if (apiAppointmentConfirmation.getAcceptProposedAppointment()) {
            requestSaga.accept(Integer.valueOf(apiAppointmentConfirmation.getAppointmentRequestNumber()));
        } else {
            requestSaga.decline(Integer.valueOf(apiAppointmentConfirmation.getAppointmentRequestNumber()));
        }
    }
}
