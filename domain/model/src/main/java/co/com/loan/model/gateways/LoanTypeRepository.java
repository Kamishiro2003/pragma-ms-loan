package co.com.loan.model.gateways;

import co.com.loan.model.loan.type.LoanType;
import reactor.core.publisher.Mono;

/**
 * Gateway interface for accessing loan type data.
 */
public interface LoanTypeRepository {

  /**
   * Retrieves a LoanType by its ID.
   *
   * @param id the ID of the loan type
   * @return a Mono emitting the LoanType if found, or empty if not
   */
  Mono<LoanType> findById(String id);

}
