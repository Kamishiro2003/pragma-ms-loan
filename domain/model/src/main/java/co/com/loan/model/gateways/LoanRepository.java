package co.com.loan.model.gateways;

import co.com.loan.model.loan.Loan;
import co.com.loan.model.page.PageCommand;
import reactor.core.publisher.Flux;
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

  /**
   * Finds a loan by its unique identifier.
   *
   * @param idLoan the loan ID to search for
   * @return a Mono containing the found loan, or empty if not found
   */
  Mono<Loan> findById(String idLoan);

  Flux<Loan> findAllByIdStatus(int idStatus, PageCommand command);

  Mono<Integer> getCountByIdStatus(int idStatus);
}
