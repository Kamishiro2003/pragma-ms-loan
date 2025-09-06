package co.com.loan.usecase.loan.type;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.ObjectNotFoundException;
import co.com.loan.model.gateways.LoanTypeRepository;
import co.com.loan.model.loan.type.LoanType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Use case for retrieving loan type information.
 */
@RequiredArgsConstructor
public class LoanTypeUseCase {

  private final LoanTypeRepository repository;

  /**
   * Retrieves a loan type by its ID.
   *
   * @param id the ID of the loan type
   * @return a Mono containing the LoanType, or an error if not found
   */
  public Mono<LoanType> getLoanTypeById(String id) {
    return repository.findById(id)
        .switchIfEmpty(Mono.error(new ObjectNotFoundException(ErrorCode.LOAN_TYPE_NOT_FOUND, id)));
  }

}
