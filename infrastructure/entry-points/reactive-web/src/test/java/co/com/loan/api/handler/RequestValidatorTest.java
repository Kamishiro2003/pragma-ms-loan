package co.com.loan.api.handler;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.loan.model.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {

  @Mock
  private Validator validator;

  private RequestValidator requestValidator;

  @BeforeEach
  void setUp() {
    requestValidator = new RequestValidator(validator);
  }

  @Test
  void validate_noViolations_shouldCompleteMono() {
    Object request = new Object();
    when(validator.validate(request)).thenReturn(Collections.emptySet());

    StepVerifier.create(requestValidator.validate(request)).verifyComplete();

    verify(validator).validate(request);
  }

  @Test
  void validate_withViolations_shouldEmitValidationException() {
    Object request = new Object();
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Invalid field");
    Set<ConstraintViolation<Object>> violations = Set.of(violation);
    when(validator.validate(request)).thenReturn(violations);

    StepVerifier.create(requestValidator.validate(request))
        .expectErrorMatches(throwable -> throwable instanceof ValidationException
            && ((ValidationException) throwable).getDetails().contains("Invalid field"))
        .verify();

    verify(validator).validate(request);
  }

  @Test
  void validate_multipleViolations_shouldEmitValidationExceptionWithAllMessages() {
    Object request = new Object();
    ConstraintViolation<Object> v1 = mock(ConstraintViolation.class);
    ConstraintViolation<Object> v2 = mock(ConstraintViolation.class);
    when(v1.getMessage()).thenReturn("Field1 invalid");
    when(v2.getMessage()).thenReturn("Field2 missing");

    Set<ConstraintViolation<Object>> violations = Set.of(v1, v2);
    when(validator.validate(request)).thenReturn(violations);

    StepVerifier.create(requestValidator.validate(request)).expectErrorSatisfies(error -> {
      assertInstanceOf(ValidationException.class, error);
      List<String> details = ((ValidationException) error).getDetails();
      assertTrue(details.contains("Field1 invalid"));
      assertTrue(details.contains("Field2 missing"));
    }).verify();

    verify(validator).validate(request);
  }
}
