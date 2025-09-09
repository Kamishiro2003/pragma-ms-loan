package co.com.loan.model.loan;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanResponse {

  private String id;
  private String name;
  private String email;
  private BigDecimal baseSalary;
  private BigDecimal amount;
  private Integer term;
  private String status;
  private String type;
  private BigDecimal totalMonthlyDebt;
}
