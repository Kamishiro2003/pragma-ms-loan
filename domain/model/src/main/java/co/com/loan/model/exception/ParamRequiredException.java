package co.com.loan.model.exception;

import co.com.loan.model.error.ErrorCode;

public class ParamRequiredException extends ApplicationException {

  public ParamRequiredException(ErrorCode errorCode) {
    super(errorCode);
  }
}
