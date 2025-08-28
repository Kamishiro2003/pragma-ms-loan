package co.com.loan.r2dbc.adapter;

import static org.mockito.Mockito.when;

import co.com.loan.model.loan.Loan;
import co.com.loan.r2dbc.entity.LoanEntity;
import co.com.loan.r2dbc.repository.LoanReactiveRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoanReactiveRepositoryAdapterTest {

  private static final String TEST_ID = "1";

  private final LoanEntity loanEntity = LoanEntity.builder()
      .id(TEST_ID)
      .amount(BigDecimal.valueOf(2000))
      .term(12)
      .email("test@mail.com")
      .typeId("typeId")
      .idStatus(1)
      .build();

  private final Loan loan = Loan.builder()
      .amount(BigDecimal.valueOf(2000))
      .term(12)
      .email("test@mail.com")
      .typeId("typeId")
      .idStatus(1)
      .build();

  @InjectMocks
  LoanReactiveRepositoryAdapter repositoryAdapter;

  @Mock
  LoanReactiveRepository repository;

  @Mock
  ObjectMapper mapper;

  @Test
  void mustFindValueById() {
    // Arrange
    when(repository.findById(TEST_ID)).thenReturn(Mono.just(loanEntity));
    when(mapper.map(loanEntity, Loan.class)).thenReturn(loan);

    // Act
    var result = repositoryAdapter.findById(TEST_ID);

    // Assert
    StepVerifier.create(result).expectNext(loan).verifyComplete();
  }

  @Test
  void mustFindAllValues() {
    // Arrange
    when(repository.findAll()).thenReturn(Flux.just(loanEntity));
    when(mapper.map(loanEntity, Loan.class)).thenReturn(loan);

    // Act
    Flux<Loan> result = repositoryAdapter.findAll();

    // Assert
    StepVerifier.create(result).expectNext(loan).verifyComplete();
  }

  @Test
  void mustFindByExample() {
    // Arrange
    when(mapper.map(loan, LoanEntity.class)).thenReturn(loanEntity);
    when(mapper.map(loanEntity, Loan.class)).thenReturn(loan);
    when(repository.findAll(ArgumentMatchers.any())).thenReturn(Flux.just(loanEntity));

    // Act
    Flux<Loan> result = repositoryAdapter.findByExample(loan);

    // Assert
    StepVerifier.create(result).expectNext(loan).verifyComplete();
  }

  @Test
  void mustSaveValue() {
    // Arrange
    when(mapper.map(loan, LoanEntity.class)).thenReturn(loanEntity);
    when(mapper.map(loanEntity, Loan.class)).thenReturn(loan);
    when(repository.save(loanEntity)).thenReturn(Mono.just(loanEntity));

    // Act
    Mono<Loan> result = repositoryAdapter.save(loan);

    // Assert
    StepVerifier.create(result).expectNext(loan).verifyComplete();
  }
}
