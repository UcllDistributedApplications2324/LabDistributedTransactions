package be.ucll.da.accountingservice.domain;

import org.springframework.stereotype.Component;

@Component
public class AccountingService {

    private Integer accountId = 0;

    // Returns -1 if  not successful and patient is not insured, accountId otherwise
    public Integer openAccount(Integer patientId) {
        if (Math.random() > 0.3) {
            return ++accountId;
        }

        return -1;
    }

    public boolean closeAccount(Integer accountId) {
       return true;
    }
}
