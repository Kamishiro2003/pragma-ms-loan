package co.com.loan.model.exception;

import co.com.loan.model.error.ErrorCode;

/**
 * Custom exception class for handling business logic errors.
 */
public class BusinessException extends ApplicationException {

  /**
   * Constructs a new BusinessException with the specified error code.
   *
   * @param errorCode the error code associated with this exception
   */
  public BusinessException(ErrorCode errorCode) {
    super(errorCode);
  }
}
