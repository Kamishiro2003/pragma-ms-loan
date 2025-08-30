package co.com.loan.model.loan;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.ObjectNotFoundException;
import java.util.Arrays;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * Enum representing the various statuses in the system.
 */
@Getter
public enum StatusEnum {
  PENDING(1,"Pendiente de revisión"),
  REJECTED(2,"Rechazadas"),
  MANUAL_REVIEW(3,"Revisión manual");

  private final int id;
  private final String displayName;

  StatusEnum(int id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public static Mono<StatusEnum> fromId(int id) {
    return Mono.justOrEmpty(
        Arrays.stream(values())
            .filter(s -> s.id == id)
            .findFirst()
    ).switchIfEmpty(Mono.error(new ObjectNotFoundException(ErrorCode.STATUS_NOT_FOUND, id)));
  }
}
