package be.ucll.da.appointmentservice.adapters.rest.incoming;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentController implements AppointmentApiDelegate {

    @Override
    public ResponseEntity<ApiAppointmentRequestResponse> apiV1AppointmentRequestPost(ApiAppointmentRequest apiAppointmentRequest) {
        throw new RuntimeException();
    }

    @Override
    public ResponseEntity<Void> apiV1AppointmentConfirmationPost(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        throw new RuntimeException();
    }
}
