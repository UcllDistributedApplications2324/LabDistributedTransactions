package be.ucll.da.appointmentservice.web;

import be.ucll.da.appointmentservice.api.AppointmentApiDelegate;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentConfirmation;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentRequest;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentRequestResponse;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentController implements AppointmentApiDelegate {

    @Override
    public ResponseEntity<ApiAppointmentRequestResponse> apiV1AppointmentRequestPost(ApiAppointmentRequest apiAppointmentRequest) {
        throw new NotYetImplementedException();
    }

    @Override
    public ResponseEntity<Void> apiV1AppointmentConfirmationPost(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        throw new NotYetImplementedException();
    }
}
