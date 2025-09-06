package co.com.loan.api.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.com.loan.api.mapper.LoanRestMapper;
import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.api.model.response.LoanRestResponse;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.usecase.loan.LoanUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoanHandlerTest {

  @Mock
  private LoanUseCase useCase;
  @Mock
  private LoanRestMapper mapper;
  @Mock
  private RequestValidator requestValidator;
  @Mock
  private ServerRequest serverRequest;

  @InjectMocks
  private LoanHandler loanHandler;

  @Test
  void listenCreateLoan_success() {
    LoanCreateRequest request = new LoanCreateRequest();
    LoanCreate loanCreate = mock(LoanCreate.class);
    LoanRestResponse restResponse = new LoanRestResponse();
    Loan loan = new Loan();

    when(serverRequest.path()).thenReturn("/loan");
    when(serverRequest.method()).thenReturn(HttpMethod.POST);
    when(serverRequest.bodyToMono(LoanCreateRequest.class)).thenReturn(Mono.just(request));
    when(requestValidator.validate(request)).thenReturn(Mono.empty()); // <-- Aquí
    when(mapper.toLoanCreate(request)).thenReturn(loanCreate);
    when(useCase.createLoan(loanCreate)).thenReturn(Mono.just(loan));
    when(mapper.toResponse(loan)).thenReturn(Mono.just(restResponse));

    Mono<ServerResponse> responseMono = loanHandler.listenCreateLoan(serverRequest);

    StepVerifier.create(responseMono).assertNext(response -> {
      assertEquals(HttpStatus.CREATED, response.statusCode());
      assertEquals(MediaType.APPLICATION_JSON, response.headers().getContentType());
    }).verifyComplete();

    verify(requestValidator).validate(request);
    verify(mapper).toLoanCreate(request);
    verify(useCase).createLoan(loanCreate);
    verify(mapper).toResponse(loan);
  }


  @Test
  void listenCreateLoan_validationError() {
    LoanCreateRequest request = new LoanCreateRequest();

    when(serverRequest.bodyToMono(LoanCreateRequest.class)).thenReturn(Mono.just(request));
    doThrow(new IllegalArgumentException("Invalid request")).when(requestValidator)
        .validate(request);

    Mono<ServerResponse> responseMono = loanHandler.listenCreateLoan(serverRequest);

    StepVerifier.create(responseMono)
        .expectErrorMatches(error -> error instanceof IllegalArgumentException && error.getMessage()
            .equals("Invalid request"))
        .verify();

    verify(requestValidator).validate(request);
    verifyNoInteractions(mapper, useCase);
  }

  @Test
  void listenCreateLoan_createLoanError() {
    LoanCreateRequest request = new LoanCreateRequest();
    LoanCreate loanCreate = mock(LoanCreate.class);

    when(serverRequest.bodyToMono(LoanCreateRequest.class)).thenReturn(Mono.just(request));
    when(requestValidator.validate(request)).thenReturn(Mono.empty());
    when(mapper.toLoanCreate(request)).thenReturn(loanCreate);
    when(useCase.createLoan(loanCreate)).thenReturn(Mono.error(new RuntimeException("DB error")));

    Mono<ServerResponse> responseMono = loanHandler.listenCreateLoan(serverRequest);

    StepVerifier.create(responseMono)
        .expectErrorMatches(error -> error instanceof RuntimeException && error.getMessage()
            .equals("DB error"))
        .verify();

    verify(requestValidator).validate(request);
    verify(mapper).toLoanCreate(request);
    verify(useCase).createLoan(loanCreate);
  }

}
