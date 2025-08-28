package co.com.loan.usecase.loan;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.BusinessException;
import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.gateways.TransactionGateway;
import co.com.loan.model.gateways.UserGateway;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.StatusEnum;
import co.com.loan.usecase.loan.type.LoanTypeUseCase;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

  private static final int COMPARISON_EQUAL = 0;

  private final LoanRepository loanRepository;
  private final LoanTypeUseCase loanTypeUseCase;
  private final UserGateway userGateway;
  private final TransactionGateway transactionGateway;

  public Mono<Loan> createLoan(LoanCreate data) {
    return transactionGateway.execute(loanTypeUseCase.getLoanTypeById(data.typeId())
        .flatMap(loanType -> validateAmount(data.amount(),
            loanType.getMinAmount(),
            loanType.getMaxAmount()))
        .then(Mono.defer(() -> userGateway.getUserByDocumentId(data.documentId()).flatMap(user -> {
          Loan loan = Loan.builder()
              .amount(data.amount())
              .term(data.term())
              .email(user.getEmail())
              .idStatus(StatusEnum.PENDING.getId())
              .typeId(data.typeId())
              .build();
          return loanRepository.save(loan);
        }))));
  }


  private Mono<Void> validateAmount(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
    return Mono.defer(() -> {
      if (amount.compareTo(minAmount) < COMPARISON_EQUAL) {
        return Mono.error(new BusinessException(ErrorCode.AMOUNT_BELOW_MIN));
      }
      if (amount.compareTo(maxAmount) > COMPARISON_EQUAL) {
        return Mono.error(new BusinessException(ErrorCode.AMOUNT_ABOVE_MAX));
      }
      return Mono.empty();
    });
  }
}

