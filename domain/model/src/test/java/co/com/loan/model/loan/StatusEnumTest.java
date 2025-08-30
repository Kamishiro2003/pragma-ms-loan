package co.com.loan.model.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.com.loan.model.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class StatusEnumTest {

  @Test
  void fromId_shouldReturnPending_whenIdIs1() {
    // Arrange
    int id = 1;

    // Act & Assert
    StepVerifier.create(StatusEnum.fromId(id)).expectNext(StatusEnum.PENDING).verifyComplete();
  }

  @Test
  void fromId_shouldReturnRejected_whenIdIs2() {
    // Arrange
    int id = 2;

    // Act & Assert
    StepVerifier.create(StatusEnum.fromId(id)).expectNext(StatusEnum.REJECTED).verifyComplete();
  }

  @Test
  void fromId_shouldReturnManualReview_whenIdIs3() {
    // Arrange
    int id = 3;

    // Act & Assert
    StepVerifier.create(StatusEnum.fromId(id))
        .expectNext(StatusEnum.MANUAL_REVIEW)
        .verifyComplete();
  }

  @Test
  void fromId_shouldReturnError_whenIdIsInvalid() {
    // Arrange
    int id = 99;

    // Act & Assert
    StepVerifier.create(StatusEnum.fromId(id))
        .expectErrorSatisfies(error -> assertThat(error).isInstanceOf(ObjectNotFoundException.class))
        .verify();
  }

  @Test
  void getId_shouldReturnCorrectId() {
    // Arrange
    StatusEnum status = StatusEnum.PENDING;

    // Act
    int id = status.getId();

    // Assert
    assertEquals(1, id);
  }

  @Test
  void getDisplayName_shouldReturnCorrectDisplayName() {
    // Arrange
    StatusEnum status = StatusEnum.REJECTED;

    // Act
    String displayName = status.getDisplayName();

    // Assert
    assertEquals("Rechazadas", displayName);
  }
}