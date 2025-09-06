package co.com.loan.usecase.loan.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.loan.model.exception.ObjectNotFoundException;
import co.com.loan.model.gateways.LoanTypeRepository;
import co.com.loan.model.loan.type.LoanType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoanTypeUseCaseTest {

  private static final String LOAN_TYPE_ID = "loanTypeId";

  LoanType loanType = LoanType.builder().id(LOAN_TYPE_ID).name("Personal Loan").build();

  @Mock
  private LoanTypeRepository repository;

  @InjectMocks
  private LoanTypeUseCase useCase;

  @Test
  void shouldReturnLoanTypeSuccessfully() {
    // Arrange
    when(repository.findById(LOAN_TYPE_ID)).thenReturn(Mono.just(loanType));

    // Act
    var result = useCase.getLoanTypeById(LOAN_TYPE_ID);

    // Assert
    StepVerifier.create(result).expectNext(loanType).verifyComplete();

    // Verify interaction
    verify(repository, times(1)).findById(LOAN_TYPE_ID);
  }

  @Test
  void shouldThrowObjectNotFoundException() {
    // Arrange
    when(repository.findById(LOAN_TYPE_ID)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(useCase.getLoanTypeById(LOAN_TYPE_ID))
        .expectErrorSatisfies(error -> assertThat(error).isInstanceOf(ObjectNotFoundException.class))
        .verify();

    // Verify interaction
    verify(repository, times(1)).findById(LOAN_TYPE_ID);
  }

}