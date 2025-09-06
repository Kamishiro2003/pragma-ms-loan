package co.com.loan.model.exception;

import co.com.loan.model.error.ErrorCode;

public class InvalidFormatParamException extends ApplicationException{

  public InvalidFormatParamException(ErrorCode errorCode) {
    super(errorCode);
  }
}
