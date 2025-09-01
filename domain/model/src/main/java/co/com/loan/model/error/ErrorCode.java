package co.com.loan.model.error;

import lombok.Getter;

/**
 * Enum representing various error codes used in the application.
 */
@Getter
public enum ErrorCode {
  LOAN_TYPE_NOT_FOUND("LOAN-TYPE-NOT-FOUND",
      ExceptionCode.NOT_FOUND,
      "No se encontró el tipo de préstamo con id: "),
  STATUS_NOT_FOUND("STATUS-NOT-FOUND",
      ExceptionCode.NOT_FOUND,
      "No se encontró el estado con id: "),
  USER_NOT_FOUND_BY_DOCUMENT_ID("USER-NOT-FOUND-BY-DOCUMENT-ID",
      ExceptionCode.NOT_FOUND,
      "No se encontró el usuario con documento de identidad: "),
  AMOUNT_BELOW_MIN("AMOUNT-BELOW-MIN",
      ExceptionCode.INVALID_INPUT,
      "El monto ingresado está por debajo del mínimo permitido"),
  AMOUNT_ABOVE_MAX("AMOUNT-ABOVE-MAX",
      ExceptionCode.INVALID_INPUT,
      "El monto ingresado supera el máximo permitido"),
  SHOULD_BE_SAME_PERSON("SHOULD_BE_SAME_PERSON",
      ExceptionCode.INVALID_INPUT,
      "Solo se pueden crear solicitudes personales"),
  ;

  private final String code;
  private final ExceptionCode exceptionCode;
  private final String message;

  /**
   * Constructor for ErrorCode enum.
   *
   * @param code          the unique error code
   * @param exceptionCode the associated exception code
   * @param message       the error message
   */
  ErrorCode(String code, ExceptionCode exceptionCode, String message) {
    this.code = code;
    this.exceptionCode = exceptionCode;
    this.message = message;
  }
}
