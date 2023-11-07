package be.ucll.da.doctorservice.domain;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DoctorService {

    public List<Doctor> getDoctorsOnPayroll(String fieldOfExpertise) {
        if (fieldOfExpertise == null || fieldOfExpertise.isEmpty()) {
            return new ArrayList<>();
        }

        return getAllDoctors().stream()
                .filter(doctor -> doctor.fieldOfExpertise().equals(fieldOfExpertise))
                .collect(Collectors.toList());
    }

    private List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor(1, "Cardiologie", "Juliette",
                "Tucker", 32, "Rue du Centre 259, 3000 Leuven, Belgium"));
        doctors.add(new Doctor(2, "Dermatologie", "Preston",
                "Mueller", 45, "Avenue Emile Vandervelde 465, 3000 Leuven, Belgium"));
        doctors.add(new Doctor(3, "Gynaecologie", "Katrina",
                "Mendoza", 67, "Kapelaniestraat 94 22, 3000 Leuven, Belgium"));
        doctors.add(new Doctor(4, "Neurologie", "Maria",
                "Piron", 82, "Naamsestraat 128, 3000 Leuven, Belgium"));

        return doctors;
    }
}
