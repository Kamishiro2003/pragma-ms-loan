package co.com.loan.r2dbc.repository;

import co.com.loan.r2dbc.entity.LoanEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanReactiveRepository extends ReactiveCrudRepository<LoanEntity, String>,
    ReactiveQueryByExampleExecutor<LoanEntity> {

  Flux<LoanEntity> findByIdStatus(Integer idStatus, Pageable pageable);

  Mono<Integer> countByIdStatus(Integer idStatus);
}
