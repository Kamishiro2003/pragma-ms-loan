package co.com.loan.r2dbc.repository;

import co.com.loan.r2dbc.entity.LoanEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanReactiveRepository extends ReactiveCrudRepository<LoanEntity, String>,
    ReactiveQueryByExampleExecutor<LoanEntity> {

}
