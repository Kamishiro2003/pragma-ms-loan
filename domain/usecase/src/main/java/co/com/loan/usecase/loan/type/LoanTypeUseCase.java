package co.com.loan.usecase.loan.type;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.ObjectNotFoundException;
import co.com.loan.model.gateways.LoanTypeRepository;
import co.com.loan.model.loan.type.LoanType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanTypeUseCase {

  private final LoanTypeRepository repository;

  public Mono<LoanType> getLoanTypeById(String id) {
    return repository.findById(id)
        .switchIfEmpty(Mono.error(new ObjectNotFoundException(ErrorCode.LOAN_TYPE_NOT_FOUND, id)));
  }

}
