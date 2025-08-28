package co.com.loan.r2dbc.adapter;

import co.com.loan.model.gateways.LoanTypeRepository;
import co.com.loan.model.loan.type.LoanType;
import co.com.loan.r2dbc.entity.LoanTypeEntity;
import co.com.loan.r2dbc.helper.ReactiveAdapterOperations;
import co.com.loan.r2dbc.repository.LoanTypeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class LoanTypeReactiveRepositoryAdapter extends
    ReactiveAdapterOperations<LoanType, LoanTypeEntity, String, LoanTypeReactiveRepository> implements
    LoanTypeRepository {

  public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository,
      ObjectMapper mapper) {
    super(repository, mapper, d -> mapper.map(d, LoanType.class));
  }

  @Override
  public Mono<LoanType> findById(String id) {
    log.debug("Trying to find a LoanType with id: {}", id);
    return super.findById(id)
        .doOnNext(loanType -> log.info("LoanType with id {} was found", id))
        .switchIfEmpty(Mono.defer(() -> {
          log.debug("LoanType with id {} was not found", id);
          return Mono.empty();
        }));
  }
}
