package co.com.loan.model.loan;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.ObjectNotFoundException;
import java.util.Arrays;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * Enum representing the various statuses in the loan system.
 */
@Getter
public enum StatusEnum {
  PENDING(1, "Pendiente de revisión"),
  APPROVED(2, "Aprobada"),
  REJECTED(3, "Rechazada"),
  MANUAL_REVIEW(4, "Revisión manual");

  private final int id;
  private final String displayName;

  StatusEnum(int id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public static Mono<StatusEnum> fromId(int id) {
    return Mono
        .justOrEmpty(Arrays
            .stream(values())
            .filter(s -> s.id == id)
            .findFirst())
        .switchIfEmpty(Mono.error(new ObjectNotFoundException(ErrorCode.STATUS_NOT_FOUND, id)));
  }

  public static StatusEnum getStatusEnum(int id) {
    return Arrays
        .stream(values())
        .filter(s -> s.id == id)
        .findFirst()
        .orElseThrow(() -> new ObjectNotFoundException(ErrorCode.STATUS_NOT_FOUND, id));
  }

}
