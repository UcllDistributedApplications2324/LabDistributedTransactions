package be.ucll.da.appointmentservice.domain.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query( "select a " +
            "from Appointment a " +
            "where " +
            "  a.doctor = :doctorId AND " +
            "  a.preferredDay = :day AND " +
            "  (a.status = be.ucll.da.appointmentservice.domain.appointment.AppointmentStatus.REQUEST_REGISTERED OR " +
            "   a.status = be.ucll.da.appointmentservice.domain.appointment.AppointmentStatus.ACCEPTED)")
    List<Appointment> getAppointmentsForDoctorOnDay(Integer doctorId, LocalDate day);
}
