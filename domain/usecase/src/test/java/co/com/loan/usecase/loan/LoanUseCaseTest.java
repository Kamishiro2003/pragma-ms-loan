package co.com.loan.usecase.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.BusinessException;
import co.com.loan.model.exception.ObjectNotFoundException;
import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.gateways.MessageBrokerGateway;
import co.com.loan.model.gateways.TokenGateway;
import co.com.loan.model.gateways.TransactionGateway;
import co.com.loan.model.gateways.UserGateway;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.LoanResponse;
import co.com.loan.model.loan.StatusEnum;
import co.com.loan.model.loan.type.LoanType;
import co.com.loan.model.message.MessageBody;
import co.com.loan.model.page.PageCommand;
import co.com.loan.model.page.PageResponse;
import co.com.loan.model.user.User;
import co.com.loan.usecase.loan.type.LoanTypeUseCase;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoanUseCaseTest {

  private final LoanCreate loanCreate = new LoanCreate(
      BigDecimal.valueOf(2222), 12, "documentId",
      "typeId");

  private final Loan loan = Loan
      .builder()
      .id("idLoan")
      .amount(BigDecimal.valueOf(2222))
      .term(12)
      .email("git@mail.com")
      .idStatus(StatusEnum.PENDING.getId())
      .typeId("typeId")
      .build();

  private final User user = User
      .builder()
      .email(loan.getEmail())
      .build();

  private final LoanType loanType = LoanType
      .builder()
      .id("typeId")
      .minAmount(BigDecimal.valueOf(1000))
      .maxAmount(BigDecimal.valueOf(5000))
      .interestRate(BigDecimal.valueOf(19))
      .build();


  @Mock
  private LoanRepository loanRepository;

  @Mock
  private LoanTypeUseCase loanTypeUseCase;

  @Mock
  private UserGateway userGateway;

  @Mock
  private TransactionGateway transactionGateway;

  @Mock
  private TokenGateway tokenGateway;

  @Mock
  private MessageBrokerGateway messageBrokerGateway;

  @InjectMocks
  private LoanUseCase loanUseCase;

  @Test
  void shouldCreateLoanSuccessfully() {
    // Arrange
    when(transactionGateway.execute(ArgumentMatchers.<Mono<?>>any())).thenAnswer(
        invocation -> invocation.getArgument(0));
    when(tokenGateway.getEmailFromToken()).thenReturn(Mono.just(user.getEmail()));
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));
    when(userGateway.getUserByDocumentId(loanCreate.documentId())).thenReturn(Mono.just(user));
    when(loanRepository.save(any(Loan.class))).thenReturn(Mono.just(loan));

    // Act
    var result = loanUseCase.createLoan(loanCreate);

    // Assert
    StepVerifier
        .create(result)
        .expectNext(loan)
        .verifyComplete();
  }

  @Test
  void shouldFailWhenAmountBelowMin() {
    // Arrange
    lenient()
        .when(transactionGateway.execute(ArgumentMatchers.<Mono<?>>any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    LoanCreate invalidLoan = new LoanCreate(BigDecimal.valueOf(500), 12, "documentId", "typeId");
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));

    // Act & Assert
    StepVerifier
        .create(loanUseCase.createLoan(invalidLoan))
        .expectErrorSatisfies(error -> {
          assertThat(error).isInstanceOf(BusinessException.class);
          BusinessException ex = (BusinessException) error;
          assertThat(ex.getFullErrorCode()).isEqualTo(ErrorCode.AMOUNT_BELOW_MIN.getCode());
        })
        .verify();
  }


  @Test
  void shouldFailWhenAmountAboveMax() {
    // Arrange
    lenient()
        .when(transactionGateway.execute(ArgumentMatchers.<Mono<?>>any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    LoanCreate invalidLoan = new LoanCreate(BigDecimal.valueOf(10000), 12, "documentId", "typeId");
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));

    // Act & Assert
    StepVerifier
        .create(loanUseCase.createLoan(invalidLoan))
        .expectErrorSatisfies(error -> {
          assertThat(error).isInstanceOf(BusinessException.class);
          BusinessException ex = (BusinessException) error;
          assertThat(ex.getFullErrorCode()).isEqualTo(ErrorCode.AMOUNT_ABOVE_MAX.getCode());
        })
        .verify();
  }

  @Test
  void shouldFailWhenPersonIsDifferent() {
    // Arrange
    lenient()
        .when(transactionGateway.execute(ArgumentMatchers.<Mono<?>>any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenGateway.getEmailFromToken()).thenReturn(Mono.just(user.getEmail()));
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));
    User otherUser = User
        .builder()
        .email("other@mail.com")
        .build();
    when(userGateway.getUserByDocumentId("documentId")).thenReturn(Mono.just(otherUser));
    LoanCreate loanCreateDifferentPerson = new LoanCreate(
        BigDecimal.valueOf(2000), 12,
        "documentId", "typeId");

    // Act & Assert
    StepVerifier
        .create(loanUseCase.createLoan(loanCreateDifferentPerson))
        .expectErrorSatisfies(error -> {
          assertThat(error).isInstanceOf(BusinessException.class);
          BusinessException ex = (BusinessException) error;
          assertThat(ex.getFullErrorCode()).isEqualTo(ErrorCode.SHOULD_BE_SAME_PERSON.getCode());
        })
        .verify();
  }

  @Test
  void shouldFindAllLoansSuccessfully() {
    // Arrange
    PageCommand command = new PageCommand(0, 10);

    when(loanRepository.getCountByIdStatus(StatusEnum.PENDING.getId())).thenReturn(Mono.just(1));
    when(loanRepository.findAllByIdStatus(StatusEnum.PENDING.getId(), command)).thenReturn(
        Flux.just(loan));
    when(loanTypeUseCase.getLoanTypeById(anyString())).thenReturn(Mono.just(loanType));
    when(userGateway.getUserByEmail(anyString())).thenReturn(Mono.just(user));

    // Act
    Mono<PageResponse<LoanResponse>> result = loanUseCase.findAllLoanByIdStatus(
        StatusEnum.PENDING.getId(), command);

    // Assert
    StepVerifier
        .create(result)
        .assertNext(page -> {
          assertThat(page.getContent()).hasSize(1);
          LoanResponse response = page
              .getContent()
              .get(0);
          assertThat(response.getEmail()).isEqualTo(user.getEmail());
          assertThat(response.getAmount()).isEqualTo(loan.getAmount());
          assertThat(page.getTotalElements()).isEqualTo(1);
          assertThat(page.getTotalPages()).isEqualTo(1);
        })
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyPageWhenNoLoansFound() {
    // Arrange
    PageCommand command = new PageCommand(0, 10);

    when(loanRepository.getCountByIdStatus(StatusEnum.PENDING.getId())).thenReturn(Mono.just(0));
    when(loanRepository.findAllByIdStatus(StatusEnum.PENDING.getId(), command)).thenReturn(
        Flux.empty());

    // Act
    Mono<PageResponse<LoanResponse>> result = loanUseCase.findAllLoanByIdStatus(
        StatusEnum.PENDING.getId(), command);

    // Assert
    StepVerifier
        .create(result)
        .assertNext(page -> {
          assertThat(page.getContent()).isEmpty();
          assertThat(page.getTotalElements()).isZero();
          assertThat(page.getTotalPages()).isZero();
        })
        .verifyComplete();
  }

  @Test
  void shouldUpdateLoanStatusAndSendMessage() {
    // Arrange
    Loan updatedLoan = loan
        .toBuilder()
        .idStatus(StatusEnum.APPROVED.getId())
        .build();

    when(loanRepository.findById("idLoan")).thenReturn(Mono.just(loan));
    when(loanRepository.save(any(Loan.class))).thenReturn(Mono.just(updatedLoan));
    when(messageBrokerGateway.sendMessage(any(MessageBody.class))).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = loanUseCase.updateLoanStatusById("idLoan", StatusEnum.APPROVED.getId());

    // Assert
    StepVerifier
        .create(result)
        .verifyComplete();

    verify(loanRepository).findById("idLoan");
    verify(loanRepository).save(
        argThat(saved -> saved.getIdStatus() == StatusEnum.APPROVED.getId()));
    verify(messageBrokerGateway).sendMessage(any(MessageBody.class));
  }


  @Test
  void shouldThrowWhenLoanNotFound() {
    // Arrange
    String idLoan = "nonExistingId";
    when(loanRepository.findById(idLoan)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = loanUseCase.updateLoanStatusById(idLoan, StatusEnum.APPROVED.getId());

    // Assert
    StepVerifier
        .create(result)
        .expectErrorSatisfies(error -> assertThat(error)
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessageContaining(ErrorCode.LOAN_NOT_FOUND.getMessage() + idLoan))
        .verify();

    verify(loanRepository).findById(idLoan);
    verifyNoInteractions(messageBrokerGateway);
  }
}


