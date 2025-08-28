package co.com.loan.model.exception;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.error.ExceptionCode;
import lombok.Getter;

/**
 * Generic exception for the application. This exception is used to handle application-specific
 * errors.
 */
@Getter
public class ApplicationException extends RuntimeException {

  private final ExceptionCode exceptionCode;
  private final String fullErrorCode;

  /**
   * Constructor for ApplicationException.
   *
   * @param errorCode the full error code associated with the exception
   */
  public ApplicationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.exceptionCode = errorCode.getExceptionCode();
    this.fullErrorCode = errorCode.getCode();
  }

  public ApplicationException(ErrorCode errorCode, String value) {
    super(errorCode.getMessage() + value);
    this.exceptionCode = errorCode.getExceptionCode();
    this.fullErrorCode = errorCode.getCode();
  }

  public ApplicationException(ErrorCode errorCode, int value) {
    super(errorCode.getMessage() + value);
    this.exceptionCode = errorCode.getExceptionCode();
    this.fullErrorCode = errorCode.getCode();
  }
}
