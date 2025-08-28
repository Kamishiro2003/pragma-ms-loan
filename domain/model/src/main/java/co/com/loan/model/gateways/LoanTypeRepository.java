package co.com.loan.model.gateways;

import co.com.loan.model.loan.type.LoanType;
import reactor.core.publisher.Mono;

/**
 * Gateway interface for accessing loan type data.
 */
public interface LoanTypeRepository {

  Mono<LoanType> findById(String id);

}
