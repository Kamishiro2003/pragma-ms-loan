package co.com.loan.api.handler;

import co.com.loan.api.mapper.LoanRestMapper;
import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.InvalidFormatParamException;
import co.com.loan.model.exception.ParamRequiredException;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.page.PageCommand;
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
    log.info(
        "Received request to create a loan at path={} method={}", serverRequest.path(),
        serverRequest.method());
    return serverRequest
        .bodyToMono(LoanCreateRequest.class)
        .flatMap(request -> requestValidator
            .validate(request)
            .then(Mono.defer(() -> {
              LoanCreate loanCreate = mapper.toLoanCreate(request);
              return useCase
                  .createLoan(loanCreate)
                  .flatMap(mapper::toResponse)
                  .flatMap(response -> ServerResponse
                      .status(HttpStatus.CREATED)
                      .contentType(MediaType.APPLICATION_JSON)
                      .bodyValue(response));
            })));
  }

  public Mono<ServerResponse> listenFindLoansByIdStatus(ServerRequest serverRequest) {
    log.info(
        "Received request to retrieve loan list by status at path={} method={}",
        serverRequest.path(), serverRequest.method());

    String idStatusParam = serverRequest
        .queryParam("idStatus")
        .orElseThrow(() -> new ParamRequiredException(ErrorCode.STATUS_IS_REQUIRED));
    String pageParam = serverRequest
        .queryParam("page")
        .orElse("0");
    String sizeParam = serverRequest
        .queryParam("size")
        .orElse("10");

    try {
      int idStatus = Integer.parseInt(idStatusParam);
      int page = Integer.parseInt(pageParam);
      int size = Integer.parseInt(sizeParam);

      PageCommand command = new PageCommand(page, size);

      return useCase
          .findAllLoanByIdStatus(idStatus, command)
          .flatMap(pageResponse -> ServerResponse
              .ok()
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(pageResponse));

    } catch (NumberFormatException ex) {
      log.error("Invalid parameter format", ex);
      return Mono.error(
          new InvalidFormatParamException(ErrorCode.FIND_LOANS_BY_STATUS_PARAM_INVALID));
    }
  }

  public Mono<ServerResponse> listenUpdateLoanStatusById(ServerRequest serverRequest) {
    log.info(
        "Received request to update loan status by idLoan at path={} method={}",
        serverRequest.path(), serverRequest.method());
    String idStatusParam = serverRequest
        .queryParam("idStatus")
        .orElseThrow(() -> new ParamRequiredException(ErrorCode.STATUS_IS_REQUIRED));
    String idLoanParam = serverRequest
        .queryParam("idLoan")
        .orElseThrow(() -> new ParamRequiredException(ErrorCode.LOAN_ID_IS_REQUIRED));

    try {
      int idStatus = Integer.parseInt(idStatusParam);

      return useCase
          .updateLoanStatusById(idLoanParam, idStatus)
          .then(ServerResponse
              .noContent()
              .build());
    } catch (NumberFormatException e) {
      log.error("Invalid parameter format", e);
      return Mono.error(new InvalidFormatParamException(ErrorCode.STATUS_PARAM_INVALID));
    }

  }

}
