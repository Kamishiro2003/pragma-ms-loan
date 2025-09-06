package co.com.loan.model.loan;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a loan in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Loan {

  private String id;
  private BigDecimal amount;
  private Integer term;
  private String email;
  private Integer idStatus;
  private String typeId;
}
