package co.com.loan.api.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.error.ExceptionCode;
import co.com.loan.model.exception.ApplicationException;
import co.com.loan.model.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GlobalErrorWebFilterTest {

  @Mock
  private ExceptionCodeMap exceptionCodeMap;
  @Mock
  private ServerRequest serverRequest;
  @Mock
  private HandlerFunction<ServerResponse> next;

  private GlobalErrorWebFilter filter;

  @BeforeEach
  void setUp() {
    filter = new GlobalErrorWebFilter(exceptionCodeMap);

    when(serverRequest.path()).thenReturn("/test");
    var exchange = mock(org.springframework.web.server.ServerWebExchange.class);
    var request = mock(org.springframework.http.server.reactive.ServerHttpRequest.class);
    when(serverRequest.exchange()).thenReturn(exchange);
    when(exchange.getRequest()).thenReturn(request);
    when(request.getId()).thenReturn("req-123");
  }

  @Test
  void filter_shouldHandleApplicationException() {
    ErrorCode errorCode = mock(ErrorCode.class);
    when(errorCode.getExceptionCode()).thenReturn(ExceptionCode.INVALID_INPUT);
    when(errorCode.getCode()).thenReturn("ERR-001");
    when(errorCode.getMessage()).thenReturn("Invalid input");

    ApplicationException ex = new ApplicationException(errorCode);

    when(next.handle(serverRequest)).thenReturn(Mono.error(ex));
    when(exceptionCodeMap.getHttpStatusFromExceptionCode(ExceptionCode.INVALID_INPUT)).thenReturn(
        HttpStatus.BAD_REQUEST);

    StepVerifier.create(filter.filter(serverRequest, next)).assertNext(resp -> {
      assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode());
      assertEquals(MediaType.APPLICATION_JSON, resp.headers().getContentType());
    }).verifyComplete();

    verify(exceptionCodeMap, times(2)).getHttpStatusFromExceptionCode(ExceptionCode.INVALID_INPUT);
  }


  @Test
  void filter_shouldHandleValidationException() {
    ValidationException ex = new ValidationException(List.of("field1 invalid", "field2 missing"));
    when(next.handle(serverRequest)).thenReturn(Mono.error(ex));

    StepVerifier.create(filter.filter(serverRequest, next)).assertNext(resp -> {
      assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode());
      assertEquals(MediaType.APPLICATION_JSON, resp.headers().getContentType());
    }).verifyComplete();
  }

  @Test
  void filter_shouldHandleGenericException() {
    Exception ex = new RuntimeException("Unexpected");
    when(next.handle(serverRequest)).thenReturn(Mono.error(ex));

    StepVerifier.create(filter.filter(serverRequest, next)).assertNext(resp -> {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.statusCode());
      assertEquals(MediaType.APPLICATION_JSON, resp.headers().getContentType());
    }).verifyComplete();
  }
}
