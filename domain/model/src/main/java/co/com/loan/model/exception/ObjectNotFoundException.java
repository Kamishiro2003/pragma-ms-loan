package co.com.loan.model.exception;

import co.com.loan.model.error.ErrorCode;

/**
 * Exception throw when an object was not found.
 */
public class ObjectNotFoundException extends ApplicationException {

  /**
   * Constructs a new ObjectNotFoundException with the specified error code.
   *
   * @param errorCode the error code associated with this exception
   */
  public ObjectNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ObjectNotFoundException(ErrorCode errorCode, String value) {
    super(errorCode, value);
  }

  public ObjectNotFoundException(ErrorCode errorCode, int value) {
    super(errorCode, value);
  }
}
