package com.payr.loan_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payr.loan_service.model.LoanApplication;
import com.payr.loan_service.model.LoanStatus;
import com.payr.loan_service.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentSuccessListener {

    @Autowired
    private LoanApplicationRepository loanRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment-success", groupId = "loan-service-group")
    @Transactional
    public void handlePaymentSuccess(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            Integer loanId = event.get("loanId").asInt();
            BigDecimal amountPaid = new BigDecimal(event.get("amountPaid").asText());
            String status = event.get("status").asText();

            if ("SUCCESS".equals(status)) {
                Optional<LoanApplication> optionalLoan = loanRepository.findById(loanId);
                
                if (optionalLoan.isPresent()) {
                    LoanApplication loan = optionalLoan.get();
                    
                    // Initialize remainingAmount if null (to avoid NullPointerException)
                    if (loan.getRemainingAmount() == null) {
                        loan.setRemainingAmount(loan.getRequestedAmount());
                    }

                    BigDecimal newRemaining = loan.getRemainingAmount().subtract(amountPaid);
                    
                    // Prevent negative balance
                    if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
                        loan.setRemainingAmount(BigDecimal.ZERO);
                        loan.setStatus(LoanStatus.PAID_OFF);
                    } else {
                        loan.setRemainingAmount(newRemaining);
                    }

                    loanRepository.save(loan);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
