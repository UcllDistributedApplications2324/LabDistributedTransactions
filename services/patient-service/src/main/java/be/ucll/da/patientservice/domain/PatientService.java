package be.ucll.da.patientservice.domain;

import org.springframework.stereotype.Service;

@Service
public class PatientService {

    public Patient validatePatient(Long id, String firstName, String lastName) {
        String email = firstName + "." + lastName + "@google.com";

        if (Math.random() > 0.7) {
            return new Patient(id, firstName, lastName, email, true);
        }

        return new Patient(id, firstName, lastName, email, false);
    }
}
