package be.ucll.da.accountingservice.domain;

public class AccountingService {

    // Returns true if successful, false if patient is not insured
    public boolean openAccount(Long patientId) {
        if (Math.random() > 0.7) {
            return true;
        }

        return false;
    }
}
