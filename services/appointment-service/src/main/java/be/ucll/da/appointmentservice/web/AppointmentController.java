package be.ucll.da.appointmentservice.web;

import be.ucll.da.appointmentservice.api.AppointmentApiDelegate;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentConfirmation;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentRequest;
import be.ucll.da.appointmentservice.api.model.ApiAppointmentRequestResponse;
import be.ucll.da.appointmentservice.domain.appointment.AppointmentService;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentController implements AppointmentApiDelegate {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public ResponseEntity<ApiAppointmentRequestResponse> apiV1AppointmentRequestPost(ApiAppointmentRequest apiAppointmentRequest) {
        ApiAppointmentRequestResponse response = new ApiAppointmentRequestResponse();
        response.appointmentRequestNumber(appointmentService.registerRequest(apiAppointmentRequest));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> apiV1AppointmentConfirmationPost(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        appointmentService.finalizeAppointment(apiAppointmentConfirmation);
        return ResponseEntity.ok().build();
    }
}
