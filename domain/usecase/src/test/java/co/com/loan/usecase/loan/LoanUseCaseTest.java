package co.com.loan.usecase.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.BusinessException;
import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.gateways.TransactionGateway;
import co.com.loan.model.gateways.UserGateway;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.StatusEnum;
import co.com.loan.model.loan.type.LoanType;
import co.com.loan.model.user.User;
import co.com.loan.usecase.loan.type.LoanTypeUseCase;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoanUseCaseTest {

  private final LoanCreate loanCreate = new LoanCreate(BigDecimal.valueOf(2222),
      12,
      "documentId",
      "typeId");

  private final Loan loan = Loan.builder()
      .amount(BigDecimal.valueOf(2222))
      .term(12)
      .email("git@mail.com")
      .idStatus(StatusEnum.PENDING.getId())
      .typeId("typeId")
      .build();

  private final User user = User.builder().email(loan.getEmail()).build();

  private final LoanType loanType = LoanType.builder()
      .id("typeId")
      .minAmount(BigDecimal.valueOf(1000))
      .maxAmount(BigDecimal.valueOf(5000))
      .build();

  @Mock
  private LoanRepository loanRepository;
  @Mock
  private LoanTypeUseCase loanTypeUseCase;
  @Mock
  private TransactionGateway transactionGateway;
  @Mock
  private UserGateway userGateway;

  @InjectMocks
  private LoanUseCase loanUseCase;

  @BeforeEach
  void setup() {
    when(transactionGateway.execute(ArgumentMatchers.<Mono<?>>any())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void shouldCreateLoanSuccessfully() {
    // Arrange
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));
    when(userGateway.getUserByDocumentId(loanCreate.documentId())).thenReturn(Mono.just(user));
    when(loanRepository.save(any(Loan.class))).thenReturn(Mono.just(loan));

    // Act
    var result = loanUseCase.createLoan(loanCreate);

    // Assert
    StepVerifier.create(result).expectNext(loan).verifyComplete();

    // Verify interactions
    verify(transactionGateway).execute(any());
    verify(loanTypeUseCase).getLoanTypeById("typeId");
    verify(userGateway).getUserByDocumentId(loanCreate.documentId());
    verify(loanRepository).save(any(Loan.class));
  }

  @Test
  void shouldFailWhenAmountBelowMin() {
    // Arrange
    LoanCreate invalidLoan = new LoanCreate(BigDecimal.valueOf(500), 12, "documentId", "typeId");
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));

    // Act & Assert
    StepVerifier.create(loanUseCase.createLoan(invalidLoan)).expectErrorSatisfies(error -> {
      assertThat(error).isInstanceOf(BusinessException.class);
      BusinessException ex = (BusinessException) error;
      assertThat(ex.getFullErrorCode()).isEqualTo(ErrorCode.AMOUNT_BELOW_MIN.getCode());
    }).verify();

    // Verify
    verify(userGateway, never()).getUserByDocumentId(anyString());
    verify(loanRepository, never()).save(any());
  }

  @Test
  void shouldFailWhenAmountAboveMax() {
    // Arrange
    LoanCreate invalidLoan = new LoanCreate(BigDecimal.valueOf(10000), 12, "documentId", "typeId");
    when(loanTypeUseCase.getLoanTypeById("typeId")).thenReturn(Mono.just(loanType));

    // Act & Assert
    StepVerifier.create(loanUseCase.createLoan(invalidLoan)).expectErrorSatisfies(error -> {
      assertThat(error).isInstanceOf(BusinessException.class);
      BusinessException ex = (BusinessException) error;
      assertThat(ex.getFullErrorCode()).isEqualTo(ErrorCode.AMOUNT_ABOVE_MAX.getCode());
    }).verify();

    // Verify
    verify(userGateway, never()).getUserByDocumentId(anyString());
    verify(loanRepository, never()).save(any());
  }
}


