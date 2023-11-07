package be.ucll.da.appointmentservice.domain.appointment;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer patientId;

    private Boolean isValidPatientId;

    private String patientEmail;

    private LocalDate preferredDay;

    private Integer roomId;

    private String neededExpertise;

    private Integer doctor;

    private Integer accountId;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    protected Appointment() {}

    public Appointment(Integer patientId, LocalDate preferredDay, String neededExpertise) {
        this.patientId = patientId;
        this.preferredDay = preferredDay;
        this.neededExpertise = neededExpertise;
        this.status = AppointmentStatus.REGISTERED;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public Boolean getValidPatientId() {
        return isValidPatientId;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public LocalDate getPreferredDay() {
        return preferredDay;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getNeededExpertise() {
        return neededExpertise;
    }

    public Integer getDoctor() {
        return doctor;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void validatingPatient() {
        this.status = AppointmentStatus.VALIDATING_PATIENT;
    }

    public void patientValid(String patientEmail) {
        this.status = AppointmentStatus.VALIDATING_DOCTOR;
        this.isValidPatientId = true;
        this.patientEmail = patientEmail;
    }

    public void patientInvalid() {
        this.status = AppointmentStatus.NO_PATIENT;
        this.isValidPatientId = false;
    }

    public void doctorSelected(Integer selectedDoctor) {
        this.status = AppointmentStatus.BOOKING_ROOM;
        this.doctor = selectedDoctor;
    }

    public void noDoctorsFound() {
        this.status = AppointmentStatus.NO_DOCTOR;
    }

    public void roomAvailable(Integer id) {
        this.roomId = id;
        this.status = AppointmentStatus.OPENING_ACCOUNT;
    }

    public void noRoomAvailable() {
        this.status = AppointmentStatus.NO_ROOM;
    }

    public void finalizeAppointment(Integer accountId) {
        this.accountId = accountId;
        this.status = AppointmentStatus.REQUEST_REGISTERED;
    }

    public void noInsurance() {
        this.status = AppointmentStatus.NO_INSURANCE;
    }

    public void doubleBooking() {
        this.status = AppointmentStatus.DOUBLE_BOOKING;
    }

    public void accept() {
        this.status = AppointmentStatus.ACCEPTED;
    }

    public void decline() {
        this.status = AppointmentStatus.DECLINED;
    }
}
