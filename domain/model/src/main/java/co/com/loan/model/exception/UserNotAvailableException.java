package co.com.loan.model.exception;

import co.com.loan.model.error.ErrorCode;

public class UserNotAvailableException extends ApplicationException {

  public UserNotAvailableException(ErrorCode errorCode) {
    super(errorCode);
  }
}
