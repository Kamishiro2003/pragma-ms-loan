package co.com.loan.model.gateways;

import co.com.loan.model.loan.Loan;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Loan operations.
 */
public interface LoanRepository {

  /**
   * Saves a Loan.
   *
   * @param loan the Loan to save
   * @return a Mono containing the saved Loan
   */
  Mono<Loan> save(Loan loan);
}
