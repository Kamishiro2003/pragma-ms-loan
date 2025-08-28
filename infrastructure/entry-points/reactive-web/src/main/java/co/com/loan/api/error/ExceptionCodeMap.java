package co.com.loan.api.error;

import co.com.loan.model.error.ExceptionCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * This class maps general exception codes to HTTP status codes.
 */
@Component
public class ExceptionCodeMap {

  /**
   * Map an {@link ExceptionCode} to an appropriate {@link HttpStatus} response code.
   *
   * @param exceptionCode the {@link ExceptionCode} representing the specific error encountered
   * @return the corresponding {@link HttpStatus} to use in the HTTP response
   */
  public HttpStatus getHttpStatusFromExceptionCode(ExceptionCode exceptionCode) {
    return switch (exceptionCode) {
      case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
