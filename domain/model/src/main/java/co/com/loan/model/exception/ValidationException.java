package co.com.loan.model.exception;

import java.util.List;
import lombok.Getter;

/**
 * Custom exception to represent validation errors.
 */
@Getter
public class ValidationException extends RuntimeException {

  private final List<String> details;

  /**
   * Constructs a new ValidationException with the specified details.
   *
   * @param details a list of validation error messages
   */
  public ValidationException(List<String> details) {
    super("Validation failed");
    this.details = details;
  }

}
