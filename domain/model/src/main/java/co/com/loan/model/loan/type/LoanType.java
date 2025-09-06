package co.com.loan.model.loan.type;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the type of loan in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {

  private String id;
  private String name;
  private BigDecimal minAmount;
  private BigDecimal maxAmount;
  private BigDecimal interestRate;
  private Boolean automaticValidation;
}
