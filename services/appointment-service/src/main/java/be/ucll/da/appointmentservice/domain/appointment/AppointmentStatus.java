package be.ucll.da.appointmentservice.domain.appointment;

public enum AppointmentStatus {

    // Happy Flow
    REGISTERED,
    VALIDATING_PATIENT,
    VALIDATING_DOCTOR,
    BOOKING_ROOM,
    OPENING_ACCOUNT,
    REQUEST_REGISTERED,

    // Failure States
    NO_PATIENT,
    NO_DOCTOR,
    NO_ROOM,
    NO_INSURANCE,
    DOUBLE_BOOKING,

    // End States
    ACCEPTED,
    DECLINED;
}
