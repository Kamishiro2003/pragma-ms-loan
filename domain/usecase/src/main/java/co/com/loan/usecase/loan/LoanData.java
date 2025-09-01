package co.com.loan.usecase.loan;

import co.com.loan.model.loan.type.LoanType;
import co.com.loan.model.user.User;

/**
 * Data holder for loan creation containing both the loan type and the user.
 */
public record LoanData(LoanType loanType, User user) {

}
