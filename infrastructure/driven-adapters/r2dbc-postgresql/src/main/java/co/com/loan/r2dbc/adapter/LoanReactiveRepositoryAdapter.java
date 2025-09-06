package co.com.loan.r2dbc.adapter;

import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.page.PageCommand;
import co.com.loan.r2dbc.entity.LoanEntity;
import co.com.loan.r2dbc.helper.ReactiveAdapterOperations;
import co.com.loan.r2dbc.repository.LoanReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class LoanReactiveRepositoryAdapter extends
    ReactiveAdapterOperations<Loan, LoanEntity, String, LoanReactiveRepository> implements
    LoanRepository {

  public LoanReactiveRepositoryAdapter(LoanReactiveRepository repository, ObjectMapper mapper) {
    super(repository, mapper, d -> mapper.map(d, Loan.class));
  }

  @Override
  public Mono<Loan> save(Loan loan) {
    log.debug("Saving loan: {}", loan);
    return super
        .save(loan)
        .doOnSuccess(saved -> log.info("Loan saved with id: {}", saved.getId()))
        .doOnError(e -> log.error("Unexpected error while saving loan: {}", e.getMessage(), e));
  }

  @Override
  public Flux<Loan> findAllByIdStatus(int idStatus, PageCommand command) {
    log.info("Retrieving loans by idStatus: {}", idStatus);
    Pageable pageable = PageRequest.of(command.getPage(), command.getSize());
    return repository
        .findByIdStatus(idStatus, pageable)
        .map(this::toEntity)
        .doOnError(e -> log.error(
            "Unexpected error while retrieving loans by idStatus {}: {}", idStatus,
            e.getMessage()));
  }

  @Override
  public Mono<Integer> getCountByIdStatus(int idStatus) {
    log.info("Counting loans by idStatus: {}", idStatus);
    return repository
        .countByIdStatus(idStatus)
        .doOnSuccess(count -> log.info("Found {} loans with idStatus: {}", count, idStatus))
        .doOnError(
            e -> log.error(
                "Unexpected error while counting loans by idStatus {}: {}", idStatus,
                e.getMessage()));
  }
}
