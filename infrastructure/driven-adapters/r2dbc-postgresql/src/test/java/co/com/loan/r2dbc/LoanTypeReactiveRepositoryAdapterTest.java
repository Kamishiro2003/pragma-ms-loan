package co.com.loan.r2dbc;

import static org.mockito.Mockito.when;

import co.com.loan.model.loan.type.LoanType;
import co.com.loan.r2dbc.adapter.LoanTypeReactiveRepositoryAdapter;
import co.com.loan.r2dbc.entity.LoanTypeEntity;
import co.com.loan.r2dbc.repository.LoanTypeReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoanTypeReactiveRepositoryAdapterTest {

  private static final String TEST_NAME = "test name";

  private final LoanTypeEntity loanTypeEntity = LoanTypeEntity.builder()
      .id("1")
      .name(TEST_NAME)
      .build();

  private final LoanType loanType = LoanType.builder().id("1").name(TEST_NAME).build();

  @InjectMocks
  LoanTypeReactiveRepositoryAdapter repositoryAdapter;

  @Mock
  LoanTypeReactiveRepository repository;

  @Mock
  ObjectMapper mapper;

  @Test
  void mustFindValueById() {
    // Arrange
    when(repository.findById("1")).thenReturn(Mono.just(loanTypeEntity));
    when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);

    // Act
    var result = repositoryAdapter.findById("1");

    // Assert
    StepVerifier.create(result).expectNext(loanType).verifyComplete();
  }

  @Test
  void mustFindAllValues() {
    when(repository.findAll()).thenReturn(Flux.just(loanTypeEntity));
    when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);

    Flux<LoanType> result = repositoryAdapter.findAll();

    StepVerifier.create(result).expectNext(loanType).verifyComplete();
  }

  @Test
  void mustFindByExample() {
    when(mapper.map(loanType, LoanTypeEntity.class)).thenReturn(loanTypeEntity);
    when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);
    when(repository.findAll(ArgumentMatchers.<Example<LoanTypeEntity>>any()))
        .thenReturn(Flux.just(loanTypeEntity));

    Flux<LoanType> result = repositoryAdapter.findByExample(loanType);

    StepVerifier.create(result)
        .expectNext(loanType)
        .verifyComplete();
  }



  @Test
  void mustSaveValue() {
    when(repository.save(loanTypeEntity)).thenReturn(Mono.just(loanTypeEntity));
    when(mapper.map(loanType, LoanTypeEntity.class)).thenReturn(loanTypeEntity);
    when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);

    Mono<LoanType> result = repositoryAdapter.save(loanType);

    StepVerifier.create(result).expectNext(loanType).verifyComplete();
  }
}
