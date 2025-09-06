package co.com.loan.usecase.loan;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.BusinessException;
import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.gateways.TokenGateway;
import co.com.loan.model.gateways.TransactionGateway;
import co.com.loan.model.gateways.UserGateway;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.LoanData;
import co.com.loan.model.loan.LoanResponse;
import co.com.loan.model.loan.MonthlyPayment;
import co.com.loan.model.loan.StatusEnum;
import co.com.loan.model.loan.type.LoanType;
import co.com.loan.model.page.PageCommand;
import co.com.loan.model.page.PageResponse;
import co.com.loan.model.user.User;
import co.com.loan.usecase.loan.type.LoanTypeUseCase;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
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
    return transactionGateway.execute(getLoanType(data.typeId())
        .flatMap(loanType -> validateAmountStep(data.amount(), loanType).thenReturn(loanType))
        .flatMap(loanType -> getUserWithValidation(data, loanType))
        .flatMap(loanData -> buildAndSaveLoan(data, loanData)));
  }

  /**
   * Retrieves the loan type associated with the given loan creation request.
   *
   * @param typeId the id of the loan type to get
   * @return a {@link Mono} emitting the found {@link LoanType}, or an error if not found
   */
  private Mono<LoanType> getLoanType(String typeId) {
    return Mono.defer(() -> loanTypeUseCase.getLoanTypeById(typeId));
  }

  /**
   * Validates that the requested loan amount falls within the loan type's allowed range.
   *
   * @param amount   the requested loan amount
   * @param loanType the loan type containing minimum and maximum allowed amounts
   * @return a {@link Mono} that completes if valid, or emits a {@link BusinessException} if invalid
   */
  private Mono<Void> validateAmountStep(BigDecimal amount, LoanType loanType) {
    return Mono.defer(
        () -> validateAmount(amount, loanType.getMinAmount(), loanType.getMaxAmount()));
  }

  /**
   * Retrieves the user associated with the provided document ID and validates that the email from
   * the token matches the user’s email.
   *
   * @param data     the loan creation request containing the user document ID
   * @param loanType the loan type associated with the request
   * @return a Mono emitting a LoanData with user and loan type, or an error if validation fails
   */
  private Mono<LoanData> getUserWithValidation(LoanCreate data, LoanType loanType) {
    return Mono.defer(() -> tokenGateway
        .getEmailFromToken()
        .flatMap(emailFromToken -> userGateway
            .getUserByDocumentId(data.documentId())
            .flatMap(user -> validatePerson(emailFromToken, user.getEmail()).thenReturn(
                new LoanData(loanType, user)))));
  }

  /**
   * Builds a new loan entity from the provided data and saves it into the repository.
   *
   * @param data     the loan creation request
   * @param loanData the validated user and loan type information
   * @return a {@link Mono} emitting the persisted {@link Loan}
   */
  private Mono<Loan> buildAndSaveLoan(LoanCreate data, LoanData loanData) {
    return Mono.defer(() -> {
      Loan loan = Loan
          .builder()
          .amount(data.amount())
          .term(data.term())
          .email(loanData
              .user()
              .getEmail())
          .idStatus(StatusEnum.PENDING.getId())
          .typeId(loanData
              .loanType()
              .getId())
          .build();
      return loanRepository.save(loan);
    });
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

  /**
   * Retrieves a paginated list of loans filtered by their status.
   *
   * @param idStatus the loan status identifier
   * @param command  the pagination command containing page number and size
   * @return a {@link Mono} emitting a {@link PageResponse} with the list of loans
   */
  public Mono<PageResponse<LoanResponse>> findAllLoanByIdStatus(int idStatus, PageCommand command) {
    return getTotalCount(idStatus).flatMap(total -> getLoans(idStatus, command)
        .collectList()
        .flatMap(loans -> buildPageResponse(loans, command, total)));
  }

  /**
   * Retrieves the total number of loans by their status.
   *
   * @param idStatus the loan status identifier
   * @return a {@link Mono} emitting the total count of loans
   */
  private Mono<Integer> getTotalCount(int idStatus) {
    return Mono.defer(() -> loanRepository.getCountByIdStatus(idStatus));
  }

  /**
   * Retrieves a paginated list of loans by their status and maps them to {@link LoanResponse}.
   *
   * @param idStatus the loan status identifier
   * @param command  the pagination command containing page number and size
   * @return a {@link Flux} emitting loan responses
   */
  private Flux<LoanResponse> getLoans(int idStatus, PageCommand command) {
    return loanRepository
        .findAllByIdStatus(idStatus, command)
        .flatMap(this::mapToLoanResponse);
  }

  /**
   * Maps a {@link Loan} entity to a {@link LoanResponse}, retrieving additional user and loan type
   * information, and calculating the monthly payment.
   *
   * @param loan the loan entity
   * @return a {@link Mono} emitting the corresponding loan response
   */
  private Mono<LoanResponse> mapToLoanResponse(Loan loan) {
    return Mono.defer(() -> getLoanType(loan.getTypeId()).flatMap(
        loanType -> getUserByEmail(loan.getEmail()).map(user -> {
          BigDecimal result = MonthlyPayment.calculateMonthlyPayment(
              loan.getAmount(),
              loanType.getInterestRate(), loan.getTerm());
          return buildLoanResponse(loan, loanType, user, result);
        })));
  }

  /**
   * Builds a {@link LoanResponse} DTO with the provided loan, loan type, user, and monthly
   * payment.
   *
   * @param loan           the loan entity
   * @param loanType       the loan type information
   * @param user           the user information
   * @param monthlyPayment the calculated monthly payment
   * @return the constructed loan response
   */
  private LoanResponse buildLoanResponse(Loan loan, LoanType loanType, User user,
      BigDecimal monthlyPayment) {
    return LoanResponse
        .builder()
        .name(user.getName())
        .email(user.getEmail())
        .baseSalary(user.getBaseSalary())
        .amount(loan.getAmount())
        .term(loan.getTerm())
        .status(StatusEnum
            .getStatusEnum(loan.getIdStatus())
            .getDisplayName())
        .type(loanType.getName())
        .totalMonthlyDebt(monthlyPayment)
        .build();
  }

  /**
   * Builds a paginated response for loans.
   *
   * @param content       the list of loan responses
   * @param command       the pagination command containing page and size
   * @param totalElements the total number of elements available
   * @return a {@link Mono} emitting a {@link PageResponse} with pagination details
   */
  private Mono<PageResponse<LoanResponse>> buildPageResponse(List<LoanResponse> content,
      PageCommand command, long totalElements) {
    return Mono.defer(() -> {
      int totalPages = (int) Math.ceil((double) totalElements / command.getSize());
      return Mono.just(PageResponse
          .<LoanResponse>builder()
          .content(content)
          .page(command.getPage())
          .size(command.getSize())
          .totalElements(totalElements)
          .totalPages(totalPages)
          .build());
    });
  }

  /**
   * Retrieves a {@link User} by their email address.
   *
   * @param email the user email
   * @return a {@link Mono} emitting the user information
   */
  private Mono<User> getUserByEmail(String email) {
    return Mono.defer(() -> userGateway.getUserByEmail(email));
  }
}

