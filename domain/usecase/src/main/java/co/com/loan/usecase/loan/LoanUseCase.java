package co.com.loan.usecase.loan;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.BusinessException;
import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.gateways.TokenGateway;
import co.com.loan.model.gateways.TransactionGateway;
import co.com.loan.model.gateways.UserGateway;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.StatusEnum;
import co.com.loan.usecase.loan.type.LoanTypeUseCase;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Use case for creating and validating loans.
 */
@RequiredArgsConstructor
public class LoanUseCase {

  private static final int COMPARISON_EQUAL = 0;

  private final LoanRepository loanRepository;
  private final LoanTypeUseCase loanTypeUseCase;
  private final UserGateway userGateway;
  private final TransactionGateway transactionGateway;
  private final TokenGateway tokenGateway;

  /**
   * Creates a loan after validating amount and user email.
   *
   * @param data the loan creation request
   * @return a Mono emitting the saved Loan
   */
  public Mono<Loan> createLoan(LoanCreate data) {
    return transactionGateway.execute(loanTypeUseCase.getLoanTypeById(data.typeId())
        .flatMap(loanType -> validateAmount(data.amount(),
            loanType.getMinAmount(),
            loanType.getMaxAmount()).thenReturn(loanType))
        .flatMap(loanType -> tokenGateway.getToken()
            .zipWhen(jwt -> tokenGateway.getEmailFromToken())
            .flatMap(tuple -> {
              String jwtToken = tuple.getT1();
              String emailFromToken = tuple.getT2();

              return userGateway.getUserByDocumentId(data.documentId(), jwtToken)
                  .flatMap(user -> validatePerson(emailFromToken, user.getEmail()).then(Mono.just(
                      new LoanData(loanType, user))));
            }))
        .flatMap(loanData -> {
          Loan loan = Loan.builder()
              .amount(data.amount())
              .term(data.term())
              .email(loanData.user()
                  .getEmail())
              .idStatus(StatusEnum.PENDING.getId())
              .typeId(loanData.loanType()
                  .getId())
              .build();
          return loanRepository.save(loan);
        }));
  }

  /**
   * Validates that the loan amount is within the allowed range.
   *
   * @param amount    the requested loan amount
   * @param minAmount minimum allowed amount
   * @param maxAmount maximum allowed amount
   * @return a Mono that completes if valid or emits an error if invalid
   */
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

  /**
   * Validates that the user's email from the token matches the email retrieved from the system.
   *
   * @param emailFromToken the email extracted from the JWT
   * @param emailFromUser  the email fetched from the user service
   * @return a Mono that completes if emails match or emits an error if they differ
   */
  private Mono<Void> validatePerson(String emailFromToken, String emailFromUser) {
    if (emailFromToken.equals(emailFromUser)) {
      return Mono.empty();
    }
    return Mono.error(new BusinessException(ErrorCode.SHOULD_BE_SAME_PERSON));
  }

}

