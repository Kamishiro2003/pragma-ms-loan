package co.com.loan.api.handler;

import co.com.loan.api.mapper.LoanRestMapper;
import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler for loan-related operations in a reactive web context.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanHandler {

  private final LoanUseCase useCase;
  private final LoanRestMapper mapper;
  private final RequestValidator requestValidator;

  public Mono<ServerResponse> listenCreateLoan(ServerRequest serverRequest) {
    log.info("Received request to create a loan at path={} method={}",
        serverRequest.path(),
        serverRequest.method());
    return serverRequest.bodyToMono(LoanCreateRequest.class)
        .flatMap(request -> requestValidator.validate(request).then(Mono.defer(() -> {
          LoanCreate loanCreate = mapper.toLoanCreate(request);
          return useCase.createLoan(loanCreate)
              .flatMap(mapper::toResponse)
              .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue(response));
        })));
  }
}
