package co.com.loan.model.loan;

import java.math.BigDecimal;

/**
 * Represents the data required to create a new loan.
 */
public record LoanCreate(BigDecimal amount, Integer term, String documentId, String typeId) {

}
