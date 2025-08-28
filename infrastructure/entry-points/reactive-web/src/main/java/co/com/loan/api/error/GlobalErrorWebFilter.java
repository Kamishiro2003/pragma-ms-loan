package co.com.loan.api.error;

import co.com.loan.model.exception.ApplicationException;
import co.com.loan.model.exception.ValidationException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalErrorWebFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  private final ExceptionCodeMap exceptionCodeMap;

  @Override
  public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
    return next.handle(request)
        .onErrorResume(ApplicationException.class,
            ex -> buildErrorResponse(request,
                exceptionCodeMap.getHttpStatusFromExceptionCode(ex.getExceptionCode()),
                ex.getMessage(),
                null).doOnNext(error -> log.debug("ApplicationException caught: {} - {}",
                    ex.getFullErrorCode(),
                    ex.getMessage()))
                .flatMap(error -> buildServerResponse(error,
                    exceptionCodeMap.getHttpStatusFromExceptionCode(ex.getExceptionCode()))))
        .onErrorResume(ValidationException.class,
            ex -> buildErrorResponse(request,
                HttpStatus.BAD_REQUEST,
                "Parámetros inválidos o faltantes en la solicitud",
                ex.getDetails()).flatMap(error -> buildServerResponse(error,
                HttpStatus.BAD_REQUEST)))
        .onErrorResume(Exception.class,
            ex -> buildErrorResponse(request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                null).doOnNext(error -> log.error("Unexpected error", ex))
                .flatMap(error -> buildServerResponse(error, HttpStatus.INTERNAL_SERVER_ERROR)));
  }

  private Mono<ErrorResponse> buildErrorResponse(ServerRequest request, HttpStatus status,
      String message, List<String> details) {
    return Mono.just(ErrorResponse.builder()
        .timestamp(ZonedDateTime.now(ZoneOffset.UTC).toString())
        .path(request.path())
        .status(status.value())
        .error(message)
        .requestId(request.exchange().getRequest().getId())
        .details(details)
        .build());
  }

  private Mono<ServerResponse> buildServerResponse(ErrorResponse error, HttpStatus status) {
    return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).bodyValue(error);
  }
}
