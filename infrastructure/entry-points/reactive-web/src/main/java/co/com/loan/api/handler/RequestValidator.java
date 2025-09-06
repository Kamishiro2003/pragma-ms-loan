package co.com.loan.api.handler;

import co.com.loan.model.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Utility class for validating request objects using Jakarta Bean Validation.
 */
@Component
@RequiredArgsConstructor
public class RequestValidator {

  private final Validator validator;

  /**
   * Validates the given request object and throws a {@link ValidationException} if any constraint
   * violations are found.
   *
   * @param <T>     the type of the request object
   * @param request the request object to validate
   */
  public <T> Mono<Void> validate(T request) {
    Set<ConstraintViolation<T>> violations = validator.validate(request);

    if (violations.isEmpty()) {
      return Mono.empty();
    }

    List<String> details = violations.stream().map(ConstraintViolation::getMessage).toList();
    return Mono.error(new ValidationException(details));
  }
}